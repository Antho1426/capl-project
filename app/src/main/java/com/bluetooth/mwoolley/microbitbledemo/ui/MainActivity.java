package com.bluetooth.mwoolley.microbitbledemo.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bluetooth.mwoolley.microbitbledemo.R;

//import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int START_ACTIVITY = 1;

    private Button btnFreeGame;

    private TextView welcomeMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




    }






    // XML callback leading from MainActivity to DeviceListActivity
    public void StartDeviceListActivity(View view) {
        Intent intentToActivity = new Intent(this, DeviceListActivity.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }


    // XML callback leading from MainActivity to OpenCVTestActivity
    public void StartOpenCVTest(View view) {
        Intent intentToActivity = new Intent(this, OpenCVTestActivity2.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }



    // XML callback leading from MainActivity to ControlledImageView
    public void StartControlledImageView(View view) {
        Intent intentToActivity = new Intent(this, ControlledImageView.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }



//    // XML callback leading from MainActivity to TakeAndLoadPictureTestActivity
//    public void StartTakeAndLoadPictureTest(View view) {
//        Intent intentToActivity = new Intent(this, TakeAndLoadPictureTestActivity.class);
//        startActivityForResult(intentToActivity, START_ACTIVITY);
//    }

//    // XML callback leading from MainActivity to TakeAndLoadPictureTestActivity_2
//    public void StartTakeAndLoadPictureTest_2(View view) {
//        Intent intentToActivity = new Intent(this, TakeAndLoadPictureTestActivity_2.class);
//        startActivityForResult(intentToActivity, START_ACTIVITY);
//    }

//    // XML callback leading from MainActivity to ProgressBarTestActivity
//    public void StartProgressBarTestActivity(View view) {
//        Intent intentToActivity = new Intent(this, ProgressBarTestActivity.class);
//        startActivityForResult(intentToActivity, START_ACTIVITY);
//    }


}

