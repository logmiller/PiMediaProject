package stream.pimedia.imageviewer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask.Status;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.*;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import stream.pimedia.R;
import stream.pimedia.settings.ImageViewerSettingsActivity;
import stream.pimedia.settings.SettingsActivity;
import stream.pimedia.util.AboutActivity;
import stream.pimedia.util.ActivitySwipeDetector;
import stream.pimedia.util.SwipeReceiver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created with IntelliJ IDEA.
 * User: Logan
 * Date: 12/5/13
 * Time: 11:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class ImageViewerActivity extends Activity implements SwipeReceiver {

    public static final String URIS = "URIS_PARAM"; // String Intent parameter
    public static final String AUTO_START_SHOW = "AUTO_START_SHOW"; // Boolean
    // Intent
    // parameter
    // default
    // false
    private ImageView imageView;
    private RetrieveImageTask retrieveImageTask;

    private List<Uri> imageUris; // playlist
    private int currentImageIndex = 0;

    private boolean pictureShowActive = false;
    private boolean isProcessingCommand = false; // indicates an command
    private Timer pictureShowTimer;
    private ImageViewerBroadcastReceiver imageViewerBroadcastReceiver;



    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        menuBarsHide();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON);
        setContentView(R.layout.image_viewer);
        imageView = (ImageView) findViewById(R.id.imageView);
        ActivitySwipeDetector activitySwipeDetector = new ActivitySwipeDetector(
                this);
        RelativeLayout layout = (RelativeLayout) this.findViewById(R.id.layout);
        layout.setOnTouchListener(activitySwipeDetector);
        currentImageIndex = 0;
        imageUris = new ArrayList<Uri>();
        if (savedInstanceState != null) {
            pictureShowActive = savedInstanceState
                    .getBoolean("pictureShowActive");
            currentImageIndex = savedInstanceState.getInt("currentImageIndex");
            imageUris = (List<Uri>) savedInstanceState
                    .getSerializable("imageUris");
        }

        Intent i = getIntent();
        Log.d(this.getClass().getName(),
                "Received Action View! now setting items ");
        Serializable urisData = i.getSerializableExtra(URIS);
        if (urisData != null) {
            if (urisData instanceof List) {
                imageUris = (List<Uri>) urisData;
            }

        } else {
            if (i.getData() != null) {
                imageUris.add(i.getData());
            }
        }
        pictureShowActive = i.getBooleanExtra(AUTO_START_SHOW, false);
        if (imageUris.size() > 0) {
            loadImage();
        } else {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(ImageViewerActivity.this,
                            R.string.no_valid_uri_data_found_to_display,
                            Toast.LENGTH_LONG);
                    toast.show();
                    menuBarsHide();
                }
            });
        }


    }


    /* (non-Javadoc)
     * @see android.app.Activity#onResume()
     */
    @Override
    protected void onResume() {

        imageViewerBroadcastReceiver = new ImageViewerBroadcastReceiver();
        imageViewerBroadcastReceiver.setImageViewer(this);
        imageViewerBroadcastReceiver.registerReceiver();
        super.onResume();
    }


    /*
     * (non-Javadoc)
     *
     * @see android.app.Activity#onPause()
     */
    @Override
    protected void onPause() {
        cancleTimer();
        unregisterReceiver(imageViewerBroadcastReceiver);
        imageViewerBroadcastReceiver=null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.image_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent i = null;
        switch (item.getItemId()) {
            case R.id.menu_settings:
                i = new Intent(this, ImageViewerSettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.pimedia_menu_settings:
                i = new Intent(this, SettingsActivity.class);
                startActivity(i);
                return true;
            case R.id.menu_next:
                next();
                return true;
            case R.id.menu_pause:
                pause();
                return true;
            case R.id.menu_play:
                play();
                return true;
            case R.id.menu_previous:
                previous();
                return true;
            case R.id.menu_stop:
                stop();
                return true;
            case R.id.yaacc_about:
                AboutActivity.showAbout(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putBoolean("pictureShowActive", pictureShowActive);
        savedInstanceState.putInt("currentImageIndex", currentImageIndex);
        if (!(imageUris instanceof ArrayList)) {
            imageUris = new ArrayList<Uri>(imageUris);
        }
        savedInstanceState.putSerializable("imageUris",
                (ArrayList<Uri>) imageUris);
    }

    public void startTimer() {

        pictureShowTimer = new Timer();
        pictureShowTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                Log.d(getClass().getName(), "TimerEvent" + this);
                ImageViewerActivity.this.next();

            }
        }, getDuration());

    }

    public void play() {
        if (isProcessingCommand)
            return;
        isProcessingCommand = true;
        if (currentImageIndex < imageUris.size()) {
            runOnUiThread(new Runnable() {
                public void run() {
                    Toast toast = Toast.makeText(ImageViewerActivity.this,
                            getResources().getString(R.string.play)
                                    + getPositionString(), Toast.LENGTH_SHORT);
                    toast.show();

                }
            });
            // Start the pictureShow
            pictureShowActive = true;
            loadImage();
            startMenuHideTimer();
            isProcessingCommand = false;

        }
    }

    private void loadImage() {
        if (retrieveImageTask != null
                && retrieveImageTask.getStatus() == Status.RUNNING) {
            return;
        }
        retrieveImageTask = new RetrieveImageTask(this);
        retrieveImageTask.execute(imageUris.get(currentImageIndex));

    }

    public void stop() {
        if (isProcessingCommand)
            return;
        isProcessingCommand = true;
        cancleTimer();
        currentImageIndex = 0;
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(ImageViewerActivity.this,
                        getResources().getString(R.string.stop)
                                + getPositionString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        showDefaultImage();

        pictureShowActive = false;
        startMenuHideTimer();
        isProcessingCommand = false;
    }

    private void cancleTimer() {
        if (pictureShowTimer != null) {
            pictureShowTimer.cancel();
        }
    }

    private void showDefaultImage() {
        imageView.setImageDrawable(getResources().getDrawable(
                R.drawable.ic_launcher));
    }

    public void pause() {
        if (isProcessingCommand)
            return;
        isProcessingCommand = true;
        cancleTimer();
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(ImageViewerActivity.this,
                        getResources().getString(R.string.pause)
                                + getPositionString(), Toast.LENGTH_SHORT);
                toast.show();

            }
        });
        pictureShowActive = false;
        startMenuHideTimer();
        isProcessingCommand = false;
    }

    public void previous() {
        if (isProcessingCommand)
            return;
        isProcessingCommand = true;
        cancleTimer();
        currentImageIndex--;
        if (currentImageIndex < 0) {
            if (imageUris.size() > 0) {
                currentImageIndex = imageUris.size() - 1;
            } else {
                currentImageIndex = 0;
            }
        }
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(ImageViewerActivity.this,
                        getResources().getString(R.string.previous)
                                + getPositionString(), Toast.LENGTH_SHORT);
                toast.show();
            }
        });
        loadImage();
        runOnUiThread(new Runnable() {
            public void run() {
                menuBarsHide();
            }

        });
        isProcessingCommand = false;
    }

    public void next() {
        if (isProcessingCommand)
            return;
        isProcessingCommand = true;
        cancleTimer();
        currentImageIndex++;
        if (currentImageIndex > imageUris.size() - 1) {
            currentImageIndex = 0;
            // pictureShowActive = false; restart after last image
        }
        runOnUiThread(new Runnable() {
            public void run() {
                Toast toast = Toast.makeText(ImageViewerActivity.this,
                        getResources().getString(R.string.next)
                                + getPositionString(), Toast.LENGTH_SHORT);

                toast.show();
            }

        });
        loadImage();
        startMenuHideTimer();
        isProcessingCommand = false;
    }

    public void showImage(final Drawable image) {
        if (image == null) {
            showDefaultImage();
            return;
        }
        Log.d(this.getClass().getName(), "image bounds: " + image.getBounds());
        runOnUiThread(new Runnable() {
            public void run() {
                Log.d(getClass().getName(),
                        "Start set image: " + System.currentTimeMillis());
                imageView.setImageDrawable(image);
                Log.d(getClass().getName(),
                        "End set image: " + System.currentTimeMillis());
            }
        });

    }

    private int getDuration() {
        SharedPreferences preferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        return Integer
                .parseInt(preferences.getString(
                        getString(R.string.image_viewer_settings_duration_key),
                        "2000"));
    }

    // interface SwipeReceiver
    @Override
    public void onRightToLeftSwipe() {

        if (imageUris.size() > 1) {
            next();
        }

    }

    @Override
    public void onLeftToRightSwipe() {

        if (imageUris.size() > 1) {
            previous();
        }

    }

    @Override
    public void onTopToBottomSwipe() {
        // do nothing

    }

    @Override
    public void onBottomToTopSwipe() {
        // do nothing

    }

    @Override
    public void beginOnTouchProcessing(View v, MotionEvent event) {
        runOnUiThread(new Runnable() {
            public void run() {
                menuBarsShow();
            }
        });

    }

    @Override
    public void endOnTouchProcessing(View v, MotionEvent event) {
        startMenuHideTimer();
    }

    private void startMenuHideTimer() {
        Timer menuHideTimer = new Timer();
        menuHideTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        menuBarsHide();
                    }
                });
            }
        }, 4000);
    }

    public boolean isPictureShowActive() {
        return pictureShowActive && imageUris != null && imageUris.size() > 1;
    }

    private String getPositionString() {
        return " (" + (currentImageIndex + 1) + "/" + imageUris.size() + ")";
    }

    private void menuBarsHide() {
        Log.d(getClass().getName(), "menuBarsHide");
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            Log.d(getClass().getName(), "menuBarsHide ActionBar is null");
            return;
        }
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LOW_PROFILE);

        actionBar.hide(); // slides out

    }

    private void menuBarsShow() {
        Log.d(getClass().getName(), "menuBarsShow");
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            Log.d(getClass().getName(), "menuBarsShowr ActionBar is null");
            return;
        }
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowHomeEnabled(false);
        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_VISIBLE);

        actionBar.show();

    }
}