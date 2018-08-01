package nuim.cs.musicplayer;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.marcinmoskala.arcseekbar.ArcSeekBar;
import com.marcinmoskala.arcseekbar.ProgressListener;
import com.triggertrap.seekarc.SeekArc;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;


/**
 * Created by david on 31/12/2017.
 */

public class Player extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener {

    //Rotate button and animation
    Button rot;
    Animation rotate;

    private Utilities utils;

    // Handler to update UI timer, progress bar etc,.
    private Handler mHandler = new Handler();;
    ImageView robt;

    //Main UI buttons
    ImageButton button_play, btFF, btRW, btNxt, btPre;
    ImageButton go_to_pl, go_to_eq;

    //Circular seekbar
    SeekArc sb;

    //Handlers are used to smooth out the on touch of the seekbar
    Handler handler;
    Runnable runnable;

    //Used to display the current song playing
    private TextView songTitleLabel;

    //Button variables for shuffle and repeat
    public static ImageButton shuffle, repeat;
    private boolean isShuffle = false;
    private boolean isRepeat = false;

    //Media player
    public static MediaPlayer mp;

    //Array list of audio files/songs
    ArrayList<File> mySongs;

    // By default play first song
    int playSong = 0;

    private short seekForwardTime = 5000; // 5000 milliseconds
    private short seekBackwardTime = 5000; // 5000 milliseconds

    private TextView songCurrentDurationLabel;
    private TextView songTotalDurationLabel;


    public static int position;

    Uri u;

    Thread updateSeekArc;

    //Volume seekbar with audio manager and linked arc seekbar
    public SeekBar volumeSeekBar;
    public AudioManager audioManager;
    ArcSeekBar arcSeekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //linked xml layout is player_layout
        setContentView(R.layout.player_layout);

        //for volume control
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();

        // Mediaplayer
        mp = new MediaPlayer();

        //mp.setOnCompletionListener(this);
        //mp.setOnPreparedListener(this);
        //mp.reset();
        //mp.setOnErrorListener(this);

        //For on click listeners when using buttons
        button_play = findViewById(R.id.play);
        btFF = findViewById(R.id.btFF);
        btRW = findViewById(R.id.btRW);
        btNxt = findViewById(R.id.playNext);
        btPre = findViewById(R.id.playPrev);
        go_to_eq = findViewById(R.id.go_to_eq);
        go_to_pl = findViewById(R.id.go_to_pl);

        //Calling shuffle and repeat button flags
        shuffle = findViewById(R.id.btshf);
        repeat = findViewById(R.id.btrep);

        //for image buttons' java instructions
        button_play.setOnClickListener(this);
        btFF.setOnClickListener(this);
        btRW.setOnClickListener(this);
        btNxt.setOnClickListener(this);
        btPre.setOnClickListener(this);
        shuffle.setOnClickListener(this);
        repeat.setOnClickListener(this);

        songCurrentDurationLabel = (TextView) findViewById(R.id.songCurrentDurationLabel);
        songTotalDurationLabel = (TextView) findViewById(R.id.songTotalDurationLabel);

        songTitleLabel = (TextView) findViewById(R.id.songTitle);

        sb = findViewById(R.id.seekArc);
        updateSeekArc = new Thread() {
            @Override
            public void run() {
                int totalDuration = mp.getDuration();
                int currentPosition = 0;

                while (currentPosition < totalDuration) {

                    try {
                        sleep(500);
                        currentPosition = mp.getCurrentPosition();
                        sb.setProgress(currentPosition);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                super.run();
            }
        };

        if (mp != null) {
            mp.stop();
            mp.release();

        }

        Intent i = getIntent();
        Bundle b = i.getExtras();


        mySongs = (ArrayList) b.getParcelableArrayList("songlist");
        position = b.getInt("pos", 0);

        u = Uri.parse(mySongs.get(position).toString());
        mp = MediaPlayer.create(getApplicationContext(), u);
        mp.start();
        sb.setMax(mp.getDuration());
        updateSeekArc.start();


        //Audio progress seekbar
        sb.setOnSeekArcChangeListener(new SeekArc.OnSeekArcChangeListener() {
            @Override
            public void onProgressChanged(SeekArc seekArc, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekArc seekArc) {
                // remove message Handler from updating progress bar
               mHandler.removeCallbacks(mUpdateTimeTask);
            }

            @Override
            public void onStopTrackingTouch(SeekArc seekArc) {
                mHandler.removeCallbacks(mUpdateTimeTask);
                mp.seekTo(seekArc.getProgress());
            }
        });


        //Activity button that will that directs back to the playlist
        go_to_pl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent playlist = new Intent(Player.this, MainActivity.class);
                startActivity(playlist);
            }
        });

        //Activity button that goes to the equaliser controls
        go_to_eq.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent equaliser = new Intent(getApplicationContext(), Test.class);
                startActivity(equaliser);
            }
        });


        //Arc seek bar linked to volume control seekbar
        arcSeekBar = findViewById(R.id.arcSeekBar);
        arcSeekBar.setOnProgressChangedListener(new ProgressListener() {
            @Override
            public void invoke(int progress) {
                volumeSeekBar.setProgress(progress);

            }

        });


        /**
         * Play button click event
         * plays a song and changes button to pause image
         * pauses a song and changes button to play image
         * */
        button_play.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check for already playing
                if (mp.isPlaying()) {
                    if (mp != null) {
                        mp.pause();
                        // Changing button image to play button
                       //play.setImageResource(R.drawable.pause);
                        // Displaying Song title when play button is pressed
                        String songTitle = mySongs.get(position).getName();
                        songTitleLabel.setText(songTitle);
                    }
                } else {
                    // Resume song
                    if (mp != null) {
                        mp.start();
                        // Changing button image to pause button
                        //play.setImageResource(R.drawable.play);
                        // Displaying Song title when play button is pressed
                        String songTitle = mySongs.get(position).getName();
                        songTitleLabel.setText(songTitle);
                    }
                }

            }
        });

        /**
         * Next button click event
         * Plays next song by taking currentSongIndex + 1
         * */
        btNxt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // check if next song is there or not
                if(position < (mySongs.size() - 1)){
                    playSong(position + 1);
                    position = position + 1;
                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);

                    sb.setMax(mp.getDuration());
                }else{
                    // play first song
                    playSong(0);
                    position = 0;
                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);

                    sb.setMax(mp.getDuration());
                }

            }
        });


        /**
         * Back button click event
         * Plays previous song by currentSongIndex - 1
         * */
        btPre.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if(position > 0){
                    playSong(position - 1);
                    position = position - 1;
                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);

                    sb.setMax(mp.getDuration());
                }else{
                    // play last song
                    playSong(mySongs.size() - 1);
                    position = mySongs.size() - 1;
                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);

                    sb.setMax(mp.getDuration());
                }

            }
        });


        /**
         * Forward button click event
         * Forwards song specified seconds
         * */
        btFF.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekForward time is lesser than song duration
                if(currentPosition + seekForwardTime <= mp.getDuration()){
                    // forward song
                    mp.seekTo(currentPosition + seekForwardTime);
                }else{
                    // forward to end position
                    mp.seekTo(mp.getDuration());
                }
            }
        });


        /**
         * Backward button click event
         * Backward song to specified seconds
         * */
        btRW.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // get current song position
                int currentPosition = mp.getCurrentPosition();
                // check if seekBackward time is greater than 0 sec
                if(currentPosition - seekBackwardTime >= 0){
                    // forward song
                    mp.seekTo(currentPosition - seekBackwardTime);
                }else{
                    // backward to starting position
                    mp.seekTo(0);
                }

            }
        });


        /**
         * Button Click event for Shuffle button
         * Enables shuffle flag to true
         * */
        shuffle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isShuffle) {
                    isShuffle = false;
                    Toast.makeText(getApplicationContext(), "Shuffle is OFF", Toast.LENGTH_SHORT).show();
                    //shuffle.setImageResource(R.drawable.shufflebtn);
                } else {
                    // make repeat to true
                    isShuffle = true;
                    Toast.makeText(getApplicationContext(), "Shuffle is ON", Toast.LENGTH_SHORT).show();
                    // make shuffle to false
                    isRepeat = false;
                    //shuffle.setImageResource(R.drawable.shufflepressed);
                    //repeat.setImageResource(R.drawable.repeatoff);
                }
            }
        });


        /**
         * Button Click event for Repeat button
         * Enables repeat flag to true
         * */
        repeat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (isRepeat) {
                    isRepeat = false;
                    Toast.makeText(getApplicationContext(), "Repeat is OFF", Toast.LENGTH_SHORT).show();

                } else {
                    // make repeat to true
                    isRepeat = true;
                    Toast.makeText(getApplicationContext(), "Repeat is ON", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

        /**
         * Function to play a song
         * @param songIndex - index of song
         * */
    public void  playSong(int songIndex){
        // Play song
        try {
            mp.reset();
            mp.setDataSource(String.valueOf(mySongs.get(songIndex)));
            mp.prepare();
            mp.start();
            // Displaying Song title when play button is pressed
            String songTitle = mySongs.get(position).getName();
            songTitleLabel.setText(songTitle);

            // Changing Button Image to pause image
            //play.setImageResource(R.drawable.pause);

            // set Progress bar values
            sb.setProgress(0);
            sb.setMax(100);

            // Updating progress bar
            updateProgressBar();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * Update timer on seekbar
     * */
    public void updateProgressBar() {
        mHandler.postDelayed(mUpdateTimeTask, 100);
    }

    /**
     * Background Runnable thread
     * */
    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            //int totalDuration = mp.getDuration();
            //int currentDuration = mp.getCurrentPosition();

            //NOTE: Trying to include text display of progress, but it causes the app to crash
            // Displaying Total Duration time
            //songTotalDurationLabel.setText(""+utils.milliSecondsToTimer(totalDuration));
            // Displaying time completed playing
            //songCurrentDurationLabel.setText(""+utils.milliSecondsToTimer(currentDuration));

            // Updating progress bar
            //int progress = (int)(utils.getProgressPercentage(currentDuration, totalDuration));
            //Log.d("Progress", ""+progress);
            //sb.setProgress(progress);

            // Running this thread after 100 milliseconds
            mHandler.postDelayed(this, 200);
        }
    };

    /**
     * On Song Playing completed
     * if repeat is ON play same song again
     * if shuffle is ON play random song
     * */
   // @Override
    public void onCompletion(MediaPlayer arg0) {

        if(mp.getCurrentPosition()<mySongs.size())
        {
            mp.reset();
       /* load the new source */

       /* Prepare the mediaplayer */
           // mp.prepare();
       /* start */
            mp.start();
        }
        else
        {
       /* release mediaplayer */
            mp.release();
        }

        // check for repeat is ON or OFF
        if(isRepeat){
            // repeat is on play same song again
            playSong(position);
        } else if(isShuffle){
            // shuffle is on - play a random song
            Random rand = new Random();
            position = rand.nextInt((mySongs.size() - 1) - 0 + 1)%20;
            playSong(position);
        } else{
            // no repeat or shuffle ON - play next song
            if(position < (mySongs.size() - 1)){
                playSong(position + 1);
                position = position + 1;
            }else{
                // play first song
                playSong(0);
                position = 0;
            }
        }
    }



    //Method to control audio volume
    public void initControls()
    {
        try {
            volumeSeekBar = findViewById(R.id.seekBarVolume);
            volumeSeekBar.setVisibility(View.GONE);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setProgress(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar)
                {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar)
                {

                }
            });

        } catch (Exception e) {

        }
    }


    //Receive song index from playlist view
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == 100){
            position = data.getExtras().getInt("pos");

        }

    }



/**
    //Code for onClick listener buttons
    @Override
    public void onClick(View v) {
        int id = v.getId();

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                position++;
                mp.start();
            }
        });

        switch (id) {
            case R.id.play:
                if (mp.isPlaying())
                {
                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);

                    if (mp != null)
                    {
                        mp.pause();
                        play.setBackgroundResource(R.drawable.play);
                    }
                }

                else {
                    if (mp != null) {
                        mp.start();
                        play.setBackgroundResource(R.drawable.pause);
                        if (isRepeat) {
                            mp.setLooping(true);
                        }
                    }
                }
                break;


            //skip forward or back 5 seconds when clicked
            case R.id.btFF:
                mp.seekTo(mp.getCurrentPosition() + 5000);
                break;
            case R.id.btRW:
                mp.seekTo(mp.getCurrentPosition() - 5000);
                break;
            //Conditions for playing next track
            case R.id.playNext:


                //If shuffle is true, use randomisation
                if(isShuffle) {
                    mp.stop();
                    mp.release();
                    Random rand = new Random();
                    position = rand.nextInt((mySongs.size() - 1) - 0 + 1) % 6;
                    u = Uri.parse(mySongs.get(position).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    mp.start();
                    sb.setMax(mp.getDuration());

                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);


                    break;
                }
                else{
                    mp.stop();
                    mp.release();
                    position = (position + 1) % mySongs.size();
                    u = Uri.parse(mySongs.get(position).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    mp.start();
                    sb.setMax(mp.getDuration());

                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);
                }

                break;
            case R.id.playPrev:
                if(isShuffle)
                {

                    break;
                }
                else{
                    mp.stop();
                    mp.release();

                    if(position == position - 1)
                    {
                        mp.seekTo(0);
                        break;
                    }
                    position = position - 1;
                    u = Uri.parse(mySongs.get(position).toString());
                    mp = MediaPlayer.create(getApplicationContext(), u);
                    mp.start();
                    sb.setMax(mp.getDuration());

                    // Displaying Song title when play button is pressed
                    String songTitle = mySongs.get(position).getName();
                    songTitleLabel.setText(songTitle);

                    break;
                }


        }

    }**/




    @Override
    public void onDestroy(){
        super.onDestroy();
        mp.release();
    }


    @Override
    public void onClick(View v) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {

    }
}
