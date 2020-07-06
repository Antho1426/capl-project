package ch.epfl.mobots.capl.ui;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.mobots.capl.Constants;
import ch.epfl.mobots.capl.MicroBit;
import ch.epfl.mobots.capl.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;

//import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static final int START_ACTIVITY = 1;

    // Boolean to to toggle the indications and the hidden activities here and there to debug and
    // test the app
    // This Boolean is passed to the next activities through intent and is hence defined only once,
    // here in the MainActivity
    //++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++
    private boolean DebugModeOn = true;
    //++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++
    //++++++++++++++++++++++++++++++++++

    private GridLayout gridLayoutMainActivity;

    private Button btnDeviceList;
//    private Button btnOpencvTest;
//    private Button btnControlledImageView;
//    private Button btnTakeAndLoadPicture;
//    private Button btnAsyncTask;
//    private Button btnMCQ;
//    private Button btnFloating;
//    private Button btnFloating2;
//
//    private Button btnShortcutToMenuActivity;
//    private Button btnShortcutToFreeGame;
//    private Button btnShortcutToGeographyGame;
//    private Button btnShortcutToDeviceInformationActivity;
//    private Button btnShortcutToGamepadControllerActivity;
//
//    //private TextView welcomeMessage;
//    private TextView textViewDebugIndication;
//    private TextView textViewTests;
//    private TextView textViewTests2;
//    private TextView textViewShortcuts;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_initial_activity, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_main_initial_activity_about) {
                Intent intent = new Intent(MainActivity.this, HelpActivity.class);
                intent.putExtra(Constants.URI, Constants.MAIN_INITIAL_ACTIVITY_ABOUT);
                startActivity(intent);
                return true;
        }

        if (id == R.id.menu_main_initial_activity_debug) {
            switchOnDebugMode();
        }

        return super.onOptionsItemSelected(item);
    }






    private void switchOnDebugMode() {

        if (!DebugModeOn) {

            gridLayoutMainActivity.setVisibility(View.INVISIBLE);
            Toast.makeText(MainActivity.this, "DebugModeOn is false", Toast.LENGTH_SHORT).show();

//            // Buttons
//            btnOpencvTest.setVisibility(View.INVISIBLE);
//            btnControlledImageView.setVisibility(View.INVISIBLE);
//            btnTakeAndLoadPicture.setVisibility(View.INVISIBLE);
//            btnAsyncTask.setVisibility(View.INVISIBLE);
//            btnMCQ.setVisibility(View.INVISIBLE);
//            btnFloating.setVisibility(View.INVISIBLE);
//            btnFloating2.setVisibility(View.INVISIBLE);
//
//            btnShortcutToMenuActivity.setVisibility(View.INVISIBLE);
//            btnShortcutToFreeGame.setVisibility(View.INVISIBLE);
//            btnShortcutToGeographyGame.setVisibility(View.INVISIBLE);
//            btnShortcutToDeviceInformationActivity.setVisibility(View.INVISIBLE);
//            btnShortcutToGamepadControllerActivity.setVisibility(View.INVISIBLE);
//
//            // TextView
//            textViewDebugIndication.setVisibility(View.INVISIBLE);
//            textViewTests.setVisibility(View.INVISIBLE);
//            textViewTests2.setVisibility(View.INVISIBLE);
//            textViewShortcuts.setVisibility(View.INVISIBLE);

            DebugModeOn = true;

        } else {

            gridLayoutMainActivity.setVisibility(View.VISIBLE);
            Toast.makeText(MainActivity.this, "DebugModeOn is true", Toast.LENGTH_SHORT).show();

//            // Buttons
//            btnOpencvTest.setVisibility(View.VISIBLE);
//            btnControlledImageView.setVisibility(View.VISIBLE);
//            btnTakeAndLoadPicture.setVisibility(View.VISIBLE);
//            btnAsyncTask.setVisibility(View.VISIBLE);
//            btnMCQ.setVisibility(View.VISIBLE);
//            btnFloating.setVisibility(View.VISIBLE);
//            btnFloating2.setVisibility(View.VISIBLE);
//
//            btnShortcutToMenuActivity.setVisibility(View.VISIBLE);
//            btnShortcutToFreeGame.setVisibility(View.VISIBLE);
//            btnShortcutToGeographyGame.setVisibility(View.VISIBLE);
//            btnShortcutToDeviceInformationActivity.setVisibility(View.VISIBLE);
//            btnShortcutToGamepadControllerActivity.setVisibility(View.VISIBLE);
//
//            // TextView
//            textViewDebugIndication.setVisibility(View.VISIBLE);
//            textViewTests.setVisibility(View.VISIBLE);
//            textViewTests2.setVisibility(View.VISIBLE);
//            textViewShortcuts.setVisibility(View.VISIBLE);

            DebugModeOn = false;

        }

    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridLayoutMainActivity = (GridLayout) findViewById(R.id.grid_layout_main_activity);

        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/chalkboardseregular.ttf");
        //welcomeMessage = (TextView) findViewById(R.id.welcome_message);
        //welcomeMessage.setTypeface(type);

        btnDeviceList = (Button) findViewById(R.id.btn_device_list);
        btnDeviceList.setTypeface(type);

//        // Hiding the buttons if we are not in debugger mode (i.e. DebugModeOn = false)
//        btnOpencvTest = (Button) findViewById(R.id.btn_opencv_test);
//        btnControlledImageView = (Button) findViewById(R.id.btn_controlled_image_view);
//        btnTakeAndLoadPicture = (Button) findViewById(R.id.btn_take_and_load_picture);
//        btnAsyncTask = (Button) findViewById(R.id.btn_async_task);
//        btnMCQ = (Button) findViewById(R.id.btn_mcq);
//        btnFloating = (Button) findViewById(R.id.btn_floating_action_button);
//        btnFloating2 = (Button) findViewById(R.id.btn_floating_action_button_2);
//
//        btnShortcutToMenuActivity = (Button) findViewById(R.id.btn_menu_activity);
//        btnShortcutToFreeGame = (Button) findViewById(R.id.btn_free_game);
//        btnShortcutToGeographyGame = (Button) findViewById(R.id.btn_geography);
//        btnShortcutToDeviceInformationActivity = (Button) findViewById(R.id.btn_device_information);
//        btnShortcutToGamepadControllerActivity = (Button) findViewById(R.id.btn_dual_d_pad_controller);
//
//        // And hiding the TextView indicating debug mode is on
//        textViewDebugIndication = (TextView) findViewById(R.id.text_view_debug_indication);
//        textViewTests = (TextView) findViewById(R.id.text_view_tests);
//        textViewTests2 = (TextView) findViewById(R.id.text_view_tests_2);
//        textViewShortcuts = (TextView) findViewById(R.id.text_view_shortcuts);

        gridLayoutMainActivity.setVisibility(View.INVISIBLE);

    }






    // XML callback leading from MainActivity to DeviceListActivity
    public void StartDeviceListActivity(View view) {
        Intent intentToActivity = new Intent(this, DeviceListActivity.class);
        // Passing the DebugModeOn boolean to DeviceListActivity
        intentToActivity.putExtra("MyBoolean", DebugModeOn);
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



    // XML callback leading from MainActivity to TakeAndLoadPictureTestActivity
    public void StartTakeAndLoadPicture(View view) {
        Intent intentToActivity = new Intent(this, TakeAndLoadPictureActivity.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to AsyncTaskTestActivity
    public void StartAsyncTaskActivity(View view) {
        Intent intentToActivity = new Intent(this, AsyncTaskActivity.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to AsyncTaskTestActivity
    public void StartMCQTestActivity(View view) {
        Intent intentToActivity = new Intent(this, McqTestActivity.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to FloatingActionButtonActivity
    public void StartDropDownListTest(View view) {
        Intent intentToActivity = new Intent(this, DropDownListTest.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to FloatingActionButtonActivity2
    public void StartFloatingActionButton2(View view) {
        Intent intentToActivity = new Intent(this, FloatingActionButtonActivity2.class);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to MenuActivity (shortcut)
    public void StartMenuActivityWithShortcut(View view) {
        Intent intentToActivity = new Intent(this, MenuActivity.class);
        intentToActivity.putExtra("MyBoolean", DebugModeOn);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to FreeGameActivity (shortcut)
    public void StartFreeGameActivityWithShortcut(View view) {
        Intent intentToActivity = new Intent(this, FreeGameActivity.class);
        intentToActivity.putExtra("MyBoolean", DebugModeOn);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to GeographyActivity (shortcut)
    public void StartGeographyActivityWithShortcut(View view) {
        Intent intentToActivity = new Intent(this, GeographyActivity.class);
        intentToActivity.putExtra("MyBoolean", DebugModeOn);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to DeviceInformationActivity (shortcut)
    public void StartDeviceInformationActivityWithShortcut(View view) {
        Intent intentToActivity = new Intent(this, DeviceInformationActivity.class);
        intentToActivity.putExtra("MyBoolean", DebugModeOn);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }

    // XML callback leading from MainActivity to GamepadControllerActivity (shortcut)
    public void StartGamepadControllerActivityWithShortcut(View view) {
        Intent intentToActivity = new Intent(this, GamepadControllerActivity.class);
        intentToActivity.putExtra("MyBoolean", DebugModeOn);
        startActivityForResult(intentToActivity, START_ACTIVITY);
    }



}

