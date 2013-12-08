package stream.pimedia.player;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import stream.pimedia.R;
import stream.pimedia.settings.SettingsActivity;
import stream.pimedia.util.AboutActivity;
import android.util.Log;



/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class AVTransportPlayerActivity extends Activity {

    private Player player;
    private int playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.avtransport_player);
        // initialize buttons
        playerId = getIntent().getIntExtra(AVTransportPlayer.PLAYER_ID, -1);
        Log.d(getClass().getName(), "Got id from intent: " + playerId);
        Player player = getPlayer();
        ImageButton btnPrev = (ImageButton) findViewById(R.id.avtransportPlayerActivityControlPrev);
        ImageButton btnNext = (ImageButton) findViewById(R.id.avtransportPlayerActivityControlNext);
        ImageButton btnStop = (ImageButton) findViewById(R.id.avtransportPlayerActivityControlStop);
        ImageButton btnPlay = (ImageButton) findViewById(R.id.avtransportPlayerActivityControlPlay);
        ImageButton btnPause = (ImageButton) findViewById(R.id.avtransportPlayerActivityControlPause);
        ImageButton btnExit = (ImageButton) findViewById(R.id.avtransportPlayerActivityControlExit);
        if (player == null) {
            btnPrev.setActivated(false);
            btnNext.setActivated(false);
            btnStop.setActivated(false);
            btnPlay.setActivated(false);
            btnPause.setActivated(false);
            btnExit.setActivated(false);
        } else {
            btnPrev.setActivated(true);
            btnNext.setActivated(true);
            btnStop.setActivated(true);
            btnPlay.setActivated(true);
            btnPause.setActivated(true);
            btnExit.setActivated(true);
        }
        btnPrev.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Player player = getPlayer();
                if (player != null) {
                    player.previous();
                }

            }
        });
        btnNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Player player = getPlayer();
                if (player != null) {
                    player.next();
                }

            }
        });
        btnPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Player player = getPlayer();
                if (player != null) {
                    player.play();
                }

            }
        });
        btnPause.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Player player = getPlayer();
                if (player != null) {
                    player.pause();
                }

            }
        });
        btnStop.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Player player = getPlayer();
                if (player != null) {
                    player.stop();
                }

            }
        });
        btnExit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Player player = getPlayer();
                if (player != null) {
                    player.exit();
                }
                finish();

            }
        });
    }





    private Player getPlayer() {
        Player result = null;
        List<Player> players = PlayerFactory
                .getCurrentPlayersOfType(AVTransportPlayer.class);
        if (players != null) { // assume that there
            for (Player player : players) {
                Log.d(getClass().getName(), "Found networkplayer: " + player.getId() + " Searched  for id: " + playerId);
                if(player.getId() == playerId){
                    result = player;
                    break;
                }
            }
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.avtransport_player, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_settings:
                Intent i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.yaacc_about:
                AboutActivity.showAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}

