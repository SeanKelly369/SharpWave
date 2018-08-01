package nuim.cs.musicplayer;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;


public class splashScreen extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 4000;
    ImageView sharp;
    Animation frombottom;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_splash);

        sharp = (ImageView) findViewById(R.id.splash);

        frombottom = AnimationUtils.loadAnimation(this,R.anim.from_bottom);


        sharp.setAnimation(frombottom);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(splashScreen.this, MainActivity.class);
                startActivity(homeIntent);
                finish();

            }
        },SPLASH_TIME_OUT);
    }
}
