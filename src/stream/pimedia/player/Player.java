package stream.pimedia.player;

import java.beans.PropertyChangeListener;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Player {

    /**
     * play the next item
     */
    void next();

    /**
     * play the previous item
     */
    void previous();

    /**
     * Pause the current item
     */
    void pause();

    /**
     * start playing the current item
     */
    void play();

    /**
     * stops playing. And returns to the beginning of the item list.
     */
    void stop();

    /**
     * Set a List of Items
     * @param items the items to be played
     */
    void setItems(PlayableItem... items);


    /**
     * Drops all Items.
     */
    void clear();

    /**
     * Kill the  player.
     */
    void onDestroy();

    /**
     * Set the name of the player.
     * @param name the name
     */
    void setName(String name);

    /**
     * Get the player name.
     * @return the name
     */
    String getName();

    /**
     * Exit the player.
     */
    void exit();

    /**
     * Returns the id of the Player.
     * @return the id
     */
    int getId();


    /**
     * add a property change listener
     * @param listener
     */
    void addPropertyChangeListener(PropertyChangeListener listener);

    /**
     * remove a property change listener
     * @param listener
     */
    void removePropertyChangeListener(PropertyChangeListener listener);

    /**
     * returns the current item position in the playlist
     * @return the position string
     */
    public String getPositionString();

    /**
     * returns the title of the current item
     * @return the title
     */
    public String getCurrentItemTitle();


    /**
     * returns the title of the next current item
     * @return the title
     */
    public String getNextItemTitle();

}
