package nuim.cs.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

//remove Fragment activity if issues running
public class MainActivity extends AppCompatActivity {

    //Song playlist
    ListView listView;
    //Array holding playlist
    String[] items;
    //Media player
    MediaPlayer mp;


    ImageButton go_to_player_from_playlist, go_to_equaliser_from_playlist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.list_view1);


        go_to_equaliser_from_playlist = findViewById(R.id.go_to_equaliser_from_playlist);
        go_to_player_from_playlist = findViewById(R.id.go_to_playlist_from_equaliser);


        try {
            //Activity intent to reach the playlist (NOTE: crashes as of now)
            go_to_player_from_playlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent Player = new Intent(getApplicationContext(), Player.class);
                    startActivity(Player);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }


        //No longer crashes and puzzled why one does and another is fine
        try {
            go_to_equaliser_from_playlist.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent equaliser = new Intent(getApplicationContext(), Test.class);
                    startActivity(equaliser);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }




        final ArrayList<File> mySongs = findSongs(Environment.getExternalStorageDirectory().getAbsoluteFile());

        items = new String[mySongs.size()];

        for (int i = 0; i < mySongs.size(); i++) {
            items[i] = mySongs.get(i).getName().toString().replace(".mp3", "").replace(".MP3", "");
        }
        ArrayAdapter<String> adp = new ArrayAdapter<String>(getApplicationContext(), R.layout.song_layout, R.id.textView, items);
        listView.setAdapter(adp);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Tried to prevent app from playing two audio tracks simultaneously, but causes a crash
                //if(mp.isPlaying())
                //{
                //    mp.stop();
               //}
                startActivity(new Intent(getApplicationContext(), Player.class).putExtra("pos", position).putExtra("songlist", mySongs));
            }
        }

        );

    }

    //Seek for mp3 files on Android device
    public ArrayList<File> findSongs(File root) {
        ArrayList<File> al = new ArrayList<File>();
        File[] files = root.listFiles();
        for (File singleFile : files) {
            if (singleFile.isDirectory() && !singleFile.isHidden()) {

                al.addAll(findSongs(singleFile));

            } else {
                if (singleFile.getName().endsWith(".mp3") || singleFile.getName().endsWith(".MP3")) {
                    al.add(singleFile);
                }
            }
        }
        return al;
    }






}

















