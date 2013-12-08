package stream.pimedia.player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import org.teleal.cling.model.meta.Device;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import stream.pimedia.R;
import stream.pimedia.upnp.UpnpClient;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class PlayerFactory {
    private static List<Player> currentPlayers = new ArrayList<Player>();
    /**
     * Creates a player for the given content. Based on the configuration
     * settings in the upnpClient the player may be a player to play on a remote
     * device.
     *
     * @param upnpClient
     * the upnpClient
     * @param items
     * the items to be played
     * @return the player
     */
    public static List<Player> createPlayer(UpnpClient upnpClient,
                                            List<PlayableItem> items) {
        List<Player> resultList = new ArrayList<Player>();
        Player result = null;
        boolean video = false;
        boolean image = false;
        boolean music = false;
        for (PlayableItem playableItem : items) {
            image = image || playableItem.getMimeType().startsWith("image");
            video = video || playableItem.getMimeType().startsWith("video");
            music = music || playableItem.getMimeType().startsWith("audio");
        }
        Log.d(PlayerFactory.class.getName(), "video:" + video + " image: " + image + "audio:" + music );
        for (Device device : upnpClient.getReceiverDevices()) {
            result = createPlayer(upnpClient,device, video, image, music);
            if (result != null) {
                currentPlayers.add(result);
                result.setItems(items.toArray(new PlayableItem[items.size()]));
                resultList.add(result);
            }
        }
        return resultList;
    }
    /**
     * creates a player for the given device
     * @param upnpClient the upnpClient
     * @param receiverDevice the receiverDevice
     * @param video true if video items
     * @param image true if image items
     * @param music true if music items
     * @return the player or null if no device is present
     */
    private static Player createPlayer(UpnpClient upnpClient,Device receiverDevice,
                                       boolean video, boolean image, boolean music) {
        if( receiverDevice == null){
            Toast toast = Toast.makeText(upnpClient.getContext(), upnpClient.getContext().getString(R.string.error_no_receiver_device_found), Toast.LENGTH_SHORT);
            toast.show();
            return null;
        }
        Player result;
        if (!receiverDevice.getIdentity().getUdn().getIdentifierString().equals(UpnpClient.LOCAL_UID)) {
            String deviceName = receiverDevice.getDisplayString();
            if (deviceName.length() > 13) {
                deviceName = deviceName.substring(0, 10) + "...";
            }
            String contentType ="multi";
            if (video && !image && !music) {
                contentType ="video";
            } else if (!video && image && !music) {
                contentType ="image";
            } else if (!video && !image && music) {
                contentType ="music";
            }
            result = new AVTransportPlayer(upnpClient,receiverDevice, upnpClient.getContext()
                    .getString(R.string.playerNameAvTransport)
                    + "-" + contentType + "@"
                    + deviceName);
        } else {
            if (video && !image && !music) {
// use videoplayer
                result = getFirstCurrentPlayerOfType(MultiContentPlayer.class);
                if (result != null) {
                    shutdown(result);
                }
                result = new MultiContentPlayer(upnpClient, upnpClient
                        .getContext().getString(
                                R.string.playerNameMultiContent));
            } else if (!video && image && !music) {
// use imageplayer
                result = createImagePlayer(upnpClient);
            } else if (!video && !image && music) {
// use musicplayer
                result = createMusicPlayer(upnpClient);
            } else {
// use multiplayer
                result = new MultiContentPlayer(upnpClient, upnpClient
                        .getContext()
                        .getString(R.string.playerNameMultiContent));
            }
        }
        return result;
    }
    private static Player createImagePlayer(UpnpClient upnpClient) {
        Player result = getFirstCurrentPlayerOfType(LocalImagePlayer.class);
        if (result != null) {
            shutdown(result);
        }
        return new LocalImagePlayer(upnpClient, upnpClient.getContext()
                .getString(R.string.playerNameImage));
    }
    private static Player createMusicPlayer(UpnpClient upnpClient) {
        boolean background = PreferenceManager.getDefaultSharedPreferences(
                upnpClient.getContext()).getBoolean(
                upnpClient.getContext().getString(R.string.settings_audio_app),
                true);
        Player result = getFirstCurrentPlayerOfType(LocalBackgoundMusicPlayer.class);
        if (result != null) {
            shutdown(result);
        } else {
            result = getFirstCurrentPlayerOfType(LocalThirdPartyMusicPlayer.class);
            if (result != null) {
                shutdown(result);
            }
        }
        if (background) {
            return new LocalBackgoundMusicPlayer(upnpClient, upnpClient
                    .getContext().getString(R.string.playerNameMusic));
        }
        return new LocalThirdPartyMusicPlayer(upnpClient, upnpClient
                .getContext().getString(R.string.playerNameMusic));
    }
    /**
     * returns all current players
     *
     * @return the currentPlayer
     */
    public static List<Player> getCurrentPlayers() {
        return Collections.unmodifiableList(currentPlayers);
    }
    /**
     * returns all current players of the given type.
     *
     * @param typeClazz
     * the requested type
     * @return the currentPlayer
     */
    public static List<Player> getCurrentPlayersOfType(Class typeClazz) {
        List<Player> players = new ArrayList<Player>();
        for (Player player : currentPlayers) {
            if (typeClazz.isInstance(player)) {
                players.add(player);
            }
        }
        return Collections.unmodifiableList(players);
    }
    /**
     * returns the first current player of the given type.
     *
     * @param typeClazz
     * the requested type
     * @return the currentPlayer
     */
    public static Player getFirstCurrentPlayerOfType(Class typeClazz) {
        for (Player player : currentPlayers) {
            if (typeClazz.isInstance(player)) {
                return player;
            }
        }
        return null;
    }
    /**
     * Returns the class of a player for the given mime type.
     *
     * @param mimeType
     * the mime type
     * @return the player class
     */
    public static Class getPlayerClassForMimeType(String mimeType) {
// FIXME don't implement business logic twice
        boolean image = mimeType.startsWith("image");
        boolean video = mimeType.startsWith("video");
        boolean music = mimeType.startsWith("audio");
        Class result = MultiContentPlayer.class;
        if (video && !image && !music) {
// use videoplayer
            result = MultiContentPlayer.class;
        } else if (!video && image && !music) {
// use imageplayer
            result = LocalImagePlayer.class;
        } else if (!video && !image && music) {
// use musicplayer
            result = LocalBackgoundMusicPlayer.class;
        }
        return result;
    }
    /**
     * Kills the given Player
     *
     * @param player
     */
    public static void shutdown(Player player) {
        assert (player != null);
        currentPlayers.remove(player);
        player.onDestroy();
    }
    /**
     * Kill all Players
     */
    public static void shutdown() {
        HashSet<Player> players = new HashSet<Player>();
        players.addAll(currentPlayers);
        for (Player player : players) {
            shutdown(player);
        }
    }
}