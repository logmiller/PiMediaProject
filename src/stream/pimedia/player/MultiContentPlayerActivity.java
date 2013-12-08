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

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 12:12 PM
 * To change this template use File | Settings | File Templates.
 */
public class MultiContentPlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multi_content_player);
        // initialize buttons

        Player player = getPlayer();

        ImageButton btnPrev = (ImageButton) findViewById(R.id.multiContentPlayerActivityControlPrev);
        ImageButton btnNext = (ImageButton) findViewById(R.id.multiContentPlayerActivityControlNext);
        ImageButton btnStop = (ImageButton) findViewById(R.id.multiContentPlayerActivityControlStop);
        ImageButton btnPlay = (ImageButton) findViewById(R.id.multiContentPlayerActivityControlPlay);
        ImageButton btnPause = (ImageButton) findViewById(R.id.multiContentPlayerActivityControlPause);
        ImageButton btnExit = (ImageButton) findViewById(R.id.multiContentPlayerActivityControlExit);
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
        Player player = null;
        List<Player> players = PlayerFactory
                .getCurrentPlayersOfType(MultiContentPlayer.class);
        if (players != null && players.size() == 1) { // assume that there
            // is only one
            // background music
            // player on this
            // device
            player = players.get(0);
        }
        return player;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.multi_content_player, menu);

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
