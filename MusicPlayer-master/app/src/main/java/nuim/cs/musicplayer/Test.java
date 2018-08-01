package nuim.cs.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.audiofx.BassBoost;
import android.media.audiofx.Equalizer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * Created by Sean on 10/01/2018.
 */

//Note: I think the issue we are having stems from the EXTENDS option in public class.
    //Willy's version has extends Activity

public class Test extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{

    TextView bass_boost_label = null;
    SeekBar bass_boost = null;
    CheckBox enabled = null;
    Button flat = null;

    Equalizer eq = null;
    BassBoost bb = null;

    ImageButton go_to_playlist_from_equaliser, go_to_player_from_equaliser;

    int min_level = 0;
    int max_level = 100;

    static final int MAX_SLIDERS = 8; // Must match the XML layout
    SeekBar sliders[] = new SeekBar[MAX_SLIDERS];
    TextView slider_labels[] = new TextView[MAX_SLIDERS];
    int num_sliders = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equaliser);

        go_to_playlist_from_equaliser = findViewById(R.id.go_to_playlist_from_equaliser);
        go_to_player_from_equaliser = findViewById(R.id.go_to_player);

        //Checkbox is causing a crash
        //enabled = (CheckBox)findViewById(R.id.enabled);
        //enabled.setOnCheckedChangeListener ((CompoundButton.OnCheckedChangeListener) this);

        //flat is causing a crash
        //flat = (Button)findViewById(R.id.flat);
        //flat.setOnClickListener((View.OnClickListener) this);

        //bass boost is causing a crash
        //bass_boost = (SeekBar)findViewById(R.id.bass_boost);
        //bass_boost.setOnSeekBarChangeListener(this);
        //bass_boost_label = (TextView) findViewById (R.id.bass_boost_label);


        //Activity button that will that directs back to the playlist
        go_to_playlist_from_equaliser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent playlist = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(playlist);
            }
        });


        //Activity button that goes to the equaliser controls
        go_to_player_from_equaliser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent equaliser = new Intent(getApplicationContext(), Player.class);
                startActivity(equaliser);
            }
        });

        sliders[0] = (SeekBar)findViewById(R.id.slider_1);
        slider_labels[0] = (TextView)findViewById(R.id.slider_label_1);

        sliders[1] = (SeekBar)findViewById(R.id.slider_2);
        slider_labels[1] = (TextView)findViewById(R.id.slider_label_2);
        sliders[2] = (SeekBar)findViewById(R.id.slider_3);
        slider_labels[2] = (TextView)findViewById(R.id.slider_label_3);
        sliders[3] = (SeekBar)findViewById(R.id.slider_4);


        //Note: Attempting to decrease the number of sliders to see if it is causing the crashing

        slider_labels[3] = (TextView)findViewById(R.id.slider_label_4);
        sliders[4] = (SeekBar)findViewById(R.id.slider_5);
        slider_labels[4] = (TextView)findViewById(R.id.slider_label_5);
        sliders[5] = (SeekBar)findViewById(R.id.slider_6);
        slider_labels[5] = (TextView)findViewById(R.id.slider_label_6);
        sliders[6] = (SeekBar)findViewById(R.id.slider_7);
        slider_labels[6] = (TextView)findViewById(R.id.slider_label_7);
        sliders[7] = (SeekBar)findViewById(R.id.slider_8);
        slider_labels[7] = (TextView)findViewById(R.id.slider_label_8);


//Again, causes a crash
/**
        eq = new Equalizer (0, 0);
        if (eq != null)
        {
            eq.setEnabled (true);
            int num_bands = eq.getNumberOfBands();
            num_sliders = num_bands;
            short r[] = eq.getBandLevelRange();
            min_level = r[0];
            max_level = r[1];
        }

        bb = new BassBoost (0, 0);
        if (bb != null)
        {
        }
        else
        {
            bass_boost.setVisibility(View.GONE);
            bass_boost_label.setVisibility(View.GONE);
        }


        updateUI();
**/
    }


    /*=============================================================================
    onProgressChanged
=============================================================================*/
    //@Override
    public void onProgressChanged (SeekBar seekBar, int level,
                                   boolean fromTouch)
    {
        if (seekBar == bass_boost)
        {
            bb.setEnabled (level > 0 ? true : false);
            bb.setStrength ((short)level); // Already in the right range 0-1000
        }
        else if (eq != null)
        {
            int new_level = min_level + (max_level - min_level) * level / 100;

            for (int i = 0; i < num_sliders; i++)
            {
                if (sliders[i] == seekBar)
                {
                    eq.setBandLevel ((short)i, (short)new_level);
                    break;
                }
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    /*=============================================================================
        updateUI
    =============================================================================*/
    public void updateUI ()
    {
        updateSliders();
        updateBassBoost();
        enabled.setChecked (eq.getEnabled());
    }

    /*=============================================================================
        updateBassBoost
    =============================================================================*/
    public void updateBassBoost ()
    {
        if (bb != null)
            bass_boost.setProgress (bb.getRoundedStrength());
        else
            bass_boost.setProgress (0);
    }

    /*=============================================================================
        updateSliders
    =============================================================================*/
    public void updateSliders ()
    {
        for (int i = 0; i < num_sliders; i++)
        {
            int level;
            if (eq != null)
                level = eq.getBandLevel ((short)i);
            else
                level = 0;
            int pos = 100 * level / (max_level - min_level) + 50;
            sliders[i].setProgress (pos);
        }
    }

    /*=============================================================================
    onCheckedChange
=============================================================================*/
    //@Override
    public void onCheckedChanged (CompoundButton view, boolean isChecked)
    {
        if (view == (View) enabled)
        {
            eq.setEnabled (isChecked);
        }
    }

    /*=============================================================================
    onClick
=============================================================================*/
   // @Override
    public void onClick (View view)
    {
        if (view == (View) flat)
        {
            setFlat();
        }
    }

    /*=============================================================================
      setFlat
  =============================================================================*/
    public void setFlat ()
    {
        if (eq != null)
        {
            for (int i = 0; i < num_sliders; i++)
            {
                eq.setBandLevel ((short)i, (short)0);
            }
        }

        if (bb != null)
        {
            bb.setEnabled (false);
            bb.setStrength ((short)0);
        }

        updateUI();
    }


}