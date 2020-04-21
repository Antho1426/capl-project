package ch.epfl.mobots.papl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final int START_ACTIVITY = 1;

    private Button btnFreeGame;

    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Moving from MainActivity to FreeGameActivity (with a setOnClickListener)
        btnFreeGame = findViewById(R.id.btn_free_game);
        btnFreeGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveToGameActivity();
            }
        });

        welcomeMessage = findViewById(R.id.welcomeMessage);
        welcomeMessage.bringToFront();

    }





    // Function for the setOnClickListener to lead from MainActivity to FreeGameActivity
    private void moveToGameActivity() {
        Intent intent = new Intent(MainActivity.this, FreeGameActivity.class);
        startActivity(intent);
    }


    // XML callback leading from MainActivity to OpenCVTestActivity
    public void StartOpenCVTest(View view) {
        Intent intentToActivity = new Intent(this, OpenCVTestActivity.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }


    // XML callback leading from MainActivity to TakeAndLoadPictureTestActivity
    public void StartTakeAndLoadPictureTest(View view) {
        Intent intentToActivity = new Intent(this, TakeAndLoadPictureTestActivity.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to TakeAndLoadPictureTestActivity_2
    public void StartTakeAndLoadPictureTest_2(View view) {
        Intent intentToActivity = new Intent(this, TakeAndLoadPictureTestActivity_2.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to ProgressBarTestActivity
    public void StartProgressBarTestActivity(View view) {
        Intent intentToActivity = new Intent(this, ProgressBarTestActivity.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }


}

