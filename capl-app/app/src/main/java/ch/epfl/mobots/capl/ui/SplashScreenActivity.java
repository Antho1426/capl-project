package ch.epfl.mobots.capl.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import ch.epfl.mobots.capl.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;

//import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private int SLEEP_TIMER = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide(); // Hiding the action bar
        // Starting our thread (= fil, filet)
        LogoLauncher logoLauncher = new LogoLauncher();
        logoLauncher.start();

    }


    private class LogoLauncher extends Thread {
        public void run() {
            try {
                sleep(1000 * SLEEP_TIMER); // running for 1[s] * SLEEP_TIMER (i.e. displaying the logo during this duration)
            } catch(InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            SplashScreenActivity.this.finish(); // to destroy SpashScreenActivity and MainActivity will be the only activity that remains so far...
            // After that, the control is given to MainActivity

        }
    }
}
