package ch.epfl.mobots.papl.ui;
/*
 * Author: Martin Woolley
 * Twitter: @bluetooth_mdw
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import android.Manifest;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.mobots.papl.AudioToneMaker;
import ch.epfl.mobots.papl.ComputerVision;
import ch.epfl.mobots.papl.Constants;
import ch.epfl.mobots.papl.MicroBit;
import ch.epfl.mobots.papl.MicroBitEvent;
import com.bluetooth.mwoolley.microbitbledemo.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import ch.epfl.mobots.papl.Settings;
import ch.epfl.mobots.papl.Utility;
import ch.epfl.mobots.papl.bluetooth.BleAdapterService;
import ch.epfl.mobots.papl.bluetooth.ConnectionStatusListener;

/**
 * Microbit Events
 *
 * Measure Temperature in micro:bit - send event when it exceeds or falls below a hard coded threshold
 *
 * Requires a custom microbit application with the BLE profile in the build
 */


import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class FreeGameActivity extends AppCompatActivity implements ConnectionStatusListener, OnTouchListener {

    private BleAdapterService bluetooth_le_adapter;
    private Vibrator vibrator;
    private boolean has_vibrator;
    private ImageView gamepad;
    private ImageView gamepad_mask;

    private int pad_1_up_colour;
    private int pad_1_down_colour;
    private int pad_1_left_colour;
    private int pad_1_right_colour;
    private int pad_2_up_colour;
    private int pad_2_down_colour;
    private int pad_2_left_colour;
    private int pad_2_right_colour;










////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////

    public static boolean computer_vision_completed = false;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE_OLD = 105;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;


    ImageView selectedImage;
    // Uri imageUri;
    Bitmap imageBitmap, croppedBitmap, croppedBitmap_resized; //, grayBitmap

    Button cameraBtn, galleryBtn, computerVisionBtn, sendCommandsBtn;
    String currentPhotoPath; // absolute path of the photo
    File imageFile; // the image file in which there will be our image from the camera or the gallery


    String[] commands;
    String[] interpreted_commands;

    private TextView textViewIdentifiedCommands;

    private ProgressBar spinner;



    // Cf.: https://developer.android.com/guide/components/activities/activity-lifecycle
    static final String STATE_CURRENT_PHOTO_PATH = "currentPhotoPath";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_CURRENT_PHOTO_PATH, currentPhotoPath);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }




    public void executeTilesIdentification() {





        // computer vision part not completed yet
        computer_vision_completed = false;


        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        // Bitmap to Mat
        Mat image = new Mat();
        Utils.bitmapToMat(imageBitmap, image);

        //int width = imageBitmap.getWidth();
        int width = selectedImage.getWidth();
        //int height = imageBitmap.getHeight();

        // Computer vision part on the newly created Mat object
        //Mat grayMat = new Mat();
        //Imgproc.cvtColor(image, grayMat, Imgproc.COLOR_RGB2GRAY);
        //Utils.matToBitmap(grayMat, grayBitmap);

        // Getting the tiles templates matrix
        //*********************
        //*********************
        Mat two = null;
        try {
            two = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_two);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat three = null;
        try {
            three = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_three);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat four = null;
        try {
            four = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_four);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat end_repeat = null;
        try {
            end_repeat = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_end_repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat go_backward = null;
        try {
            go_backward = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_go_backward);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat go_forward = null;
        try {
            go_forward = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_go_forward);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat start_repeat = null;
        try {
            start_repeat = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_start_repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat turn_back = null;
        try {
            turn_back = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_back);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat turn_left = null;
        try {
            turn_left = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_left);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        Mat turn_right = null;
        try {
            turn_right = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_right);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*********************
        //*********************

        //-----
        //String[] commands = ComputerVision.TilesIdentification(image);
        Pair<String[], Mat> result = ComputerVision.TilesIdentification(image, two, three, four, end_repeat, go_backward, go_forward, start_repeat, turn_back, turn_left, turn_right); // "How to return multiple objects from a Java method?" --> cf.: https://stackoverflow.com/questions/457629/how-to-return-multiple-objects-from-a-java-method
        commands = result.first;
        Mat cropped = result.second;
        textViewIdentifiedCommands.setText(Arrays.toString(commands));
        //-----

        //int height = 30;
        //int number_of_tiles = commands.length;
        //int width = height * (45/70) * number_of_tiles;
        //grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        //
        //Utils.matToBitmap(grayMat, grayBitmap);
        //
        //selectedImage.setImageBitmap(grayBitmap);

        croppedBitmap = Bitmap.createBitmap(cropped.cols(), cropped.rows(), Bitmap.Config.RGB_565);
        //croppedBitmap = Bitmap.createBitmap(width, (int) (cropped.rows()*(double)width/cropped.cols()), Bitmap.Config.RGB_565);
        Utils.matToBitmap(cropped, croppedBitmap);
        int height = (int) (cropped.rows()*(double)width/cropped.cols());
        croppedBitmap_resized = Bitmap.createScaledBitmap(croppedBitmap, width, height, true);
        selectedImage.setImageBitmap(croppedBitmap_resized);

        // computer vision now completed
        computer_vision_completed = true;

        // enabling the possibility to send the commands
        sendCommandsBtn.setEnabled(true);

    }



    public void interpretCommands(String[] commands) {

        interpreted_commands = commands;

    }



    public void sendCommandsToMicroBit(String[] interpreted_commands) {

        Toast.makeText(this, "The first command to send is: "+commands[0], Toast.LENGTH_LONG).show(); //visible part

        for (int i = 0; i < interpreted_commands.length; i++) {



            MicroBitEvent mb_event;
            short event_value = 0;
            Settings settings = Settings.getInstance();
            byte [] event_bytes = new byte[4];



            String current_interpreted_command = interpreted_commands[i];

            switch(current_interpreted_command) {
                case "go_backward":
                    event_value = settings.getMes_dpad_2_button_down_on(); // This is the event for go_backward
                    break;

                case "go_forward":
                    event_value = settings.getMes_dpad_2_button_up_on(); // This is the event for go_forward
                    break;

                case "turn_back":
                    event_value = settings.getMes_dpad_1_button_down_on(); // This is the event for turn_back
                    break;

                case "turn_left":
                    event_value = settings.getMes_dpad_1_button_left_on(); // This is the event for turn_left
                    break;

                case "turn_right":
                    event_value = settings.getMes_dpad_1_button_right_on(); // This is the event for turn_right
                    break;

                default:
                    Toast.makeText(this, "Error! This composition of tiles is not possible! Your error might come from a badly designed 'for loop'...", Toast.LENGTH_LONG).show(); //visible part

            }

            mb_event = new MicroBitEvent(settings.getMes_dpad_controller(), event_value);
            event_bytes = mb_event.getEventBytesForBle();
            Log.d(Constants.TAG,"Writing event bytes:"+ Utility.byteArrayAsHexString(event_bytes));
            // The line below is the very line that makes the Bit:Buggy Car move!!!
            bluetooth_le_adapter.writeCharacteristic(Utility.normaliseUUID(BleAdapterService.EVENTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.CLIENTEVENT_CHARACTERISTIC_UUID), event_bytes);

            computer_vision_completed = false; // in order for the user to not be able to resend twice the same series of commands one after the other. If he wants to resend a command, he would have to re-perform a "computer vision" step


        }

    }







    private void askCameraPermission() {
        Log.d("tag", "Entering askCameraPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            //openCamera();
            dispatchTakePictureIntent();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED) {
                //openCamera();
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission is requested to use camera...", Toast.LENGTH_SHORT).show();
            }
        }
    }




    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        Log.d("tag", "Entering createImageFile");

        //-------------------------------------------------
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_"; // creating the image file
//        // setting the storage directory
//        // cf.: https://androidpedia.net/en/tutorial/150/storing-files-in-internal-external-storage
//        //------------------
//        // ✓ The line below works (worked initially) for the tablet "NVIDIA SHIELD Tablet K1" (for both taking the picture and saving it to the folder "Pictures")
//        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        //------------------
//        File storageDir = getApplicationContext().getFilesDir();
//        //File storageDir = Environment.getExternalStorageDirectory();
//        //File storageDir = Environment.getDataDirectory();
//        //File storageDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
//        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // <-- this directory works (when we have no SD card inserted in the tablet) (but this doesn't allow to save the picture taken in the memory of the tablet...)
//
//        // checking that our directory exists:
//        //storageDir.isDirectory();
//
//        // checking if we can create the .jpg file in which we are going to put our picture
//        //File.createTempFile(imageFileName,".jpg",storageDir)
//
//        // checking the valid locations for directory:
//        //getApplicationContext().getFilesDir()
//
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",   /* suffix */
//                storageDir      /* directory */
//        );
//
//        // Save a file: path for use with ACTION_VIEW intents
//        currentPhotoPath = image.getAbsolutePath(); // getting the absolute path of the image where it is saved
        //-------------------------------------------------



        // A 1st alternative
        //-------------------------------------------------
        // Cf.: https://thetopsites.net/article/50621645.shtml
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        File image = null;
//        try {
//            image = File.createTempFile(timeStamp, ".jpg", storageDir);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        currentPhotoPath = String.valueOf(Uri.fromFile(image));
        //-------------------------------------------------


        // A 2nd alternative
        // Cf.: https://stackoverflow.com/questions/17150597/file-createtempfile-vs-new-file
        //-------------------------------------------------
//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_"+".jpg"; // creating the image file
//        //File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File storageDir = Environment.getExternalStorageDirectory();
//        //File image = new File(this.getCacheDir(), imageFileName);
//        File image = new File(storageDir, imageFileName);
//        currentPhotoPath = image.getAbsolutePath(); // getting the absolute path of the image where it is saved
        //-------------------------------------------------






        // A 3rd alternative
        //-------------------------------------------------
        // Cf.: https://stackoverflow.com/questions/42628247/android-save-picture-in-internal-storage-what-am-i-doing-wrongfile-not-found
        // External sdcard location
        // directory name to store captured images and videos
        //final String IMAGE_DIRECTORY_NAME = "PaPL";
        //final int MEDIA_TYPE_IMAGE = 1;
        //File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
//        File mediaStorageDir = new File(getBaseContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);
//        // Cf.: https://stackoverflow.com/questions/24781213/how-to-create-a-folder-in-android-external-storage-directory
//        if (!mediaStorageDir.exists()) {
//            mediaStorageDir.mkdirs();
//        }
        //File mediaStorageDir = getExternalCacheDir();
        //File mediaStorageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File mediaStorageDir = getFilesDir();
//        File f= new File(mediaStorageDir, "PaPL");
//        if (!f.exists()) {
//            f.mkdir();;
//        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFile = new File(mediaStorageDir.getPath() + File.separator
                + "IMG_" + timeStamp + ".jpg");
        currentPhotoPath = imageFile.getAbsolutePath();
        //-------------------------------------------------
        // Cf.: https://stackoverflow.com/questions/36088699/error-open-failed-enoent-no-such-file-or-directory
        //*************************************
        ActivityCompat.requestPermissions(FreeGameActivity.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        try {
            if(!mediaStorageDir.isDirectory()) {
                mediaStorageDir.mkdirs();
            }
            imageFile.createNewFile();

        } catch(Exception e) {
            e.printStackTrace();
        }
        //*************************************







        return imageFile;
    }




    // This is going to save our image file into the directory
    private void dispatchTakePictureIntent() {
        Log.d("tag", "Entering dispatchTakePictureIntent");
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) { // check if the camera is present in the device
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.d("tag", "Entering - error occured while creating the file");
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ch.epfl.mobots.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                // This very line here above ↑ launches the "camera app"

                //--------------
                // Checking that we have the WRITE_EXTERNAL_STORAGE permission
                // (otherwise, we grant it now...)
                if (ContextCompat.checkSelfPermission(FreeGameActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    Log.d("tag", "Permission for WRITE_EXTERNAL_STORAGE is NOT granted!"); // Permission is not granted
                    // Request the permission
                    // Cf.: https://developer.android.com/training/permissions/requesting.html#java
                    ActivityCompat.requestPermissions(FreeGameActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                }
                // Requesting rather this permission at any rate
//                ActivityCompat.requestPermissions(FreeGameActivity.this,
//                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
//                        MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                //--------------


            }
        }
    }










    @Override
    protected void onStart() {
        super.onStart(); // super - we want the functionality of my parent class
        //Toast.makeText(this, "onStart Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "onStop Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(this, "onRestart Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onRestart");
    }










////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////












    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(Constants.TAG, "onServiceConnected");
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(mMessageHandler);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_free_game);
//        getSupportActionBar().setTitle(R.string.screen_title_controller);









        //------------------------------------
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            currentPhotoPath = savedInstanceState.getString(STATE_CURRENT_PHOTO_PATH);
        }


        selectedImage = (ImageView) findViewById(R.id.displayImageView);

        cameraBtn = (Button) findViewById(R.id.btn_camera);
        galleryBtn = (Button) findViewById(R.id.btn_gallery);
        computerVisionBtn = (Button) findViewById(R.id.btn_computer_vision);
        computerVisionBtn.setEnabled(false);
        sendCommandsBtn = (Button) findViewById(R.id.btn_send_commands);
        sendCommandsBtn.setEnabled(false);

        textViewIdentifiedCommands = (TextView) findViewById(R.id.text_view_identified_commands);

        // Info for wheel spinner: https://www.tutorialspoint.com/android/android_loading_spinner.htm
        spinner = (ProgressBar) findViewById(R.id.progressBar);
        spinner.setVisibility(View.INVISIBLE);


        OpenCVLoader.initDebug();


        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FreeGameActivity.this, "cameraBtn is clicked", Toast.LENGTH_SHORT).show();
                askCameraPermission();
            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FreeGameActivity.this, "galleryBtn is clicked", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE_OLD);
            }
        });


        computerVisionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (selectedImage.getDrawable() != null) {
                    Toast.makeText(FreeGameActivity.this, "Entering computer vision phase...", Toast.LENGTH_SHORT).show();

                    // Changing the color of the text of the button
                    computerVisionBtn.setTextColor(Color.GREEN);

                    //spinner.setVisibility(View.VISIBLE);

                    executeTilesIdentification();

                    //spinner.setVisibility(View.INVISIBLE);

                    // Re-changing the color of the text of the button
                    computerVisionBtn.setTextColor(Color.BLACK);

                } else {
                    Toast.makeText(FreeGameActivity.this, "There is no tiles to identify!", Toast.LENGTH_SHORT).show();
                }



            }
        });


        sendCommandsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (computer_vision_completed) {
                    //Toast.makeText(FreeGameActivity.this, "Sending commands to micro:bit...", Toast.LENGTH_SHORT).show();
                    interpretCommands(commands);
                    sendCommandsToMicroBit(interpreted_commands);
                } else {
                    Toast.makeText(FreeGameActivity.this, "You can't send twice the same command! Re-identify tiles in order to send commands!", Toast.LENGTH_SHORT).show();
                }

            }
        });



        //------------------------------------







        gamepad_mask = (ImageView) FreeGameActivity.this.findViewById(R.id.gamepad_mask);
        gamepad = (ImageView) FreeGameActivity.this.findViewById(R.id.gamepad);
        gamepad.setOnTouchListener(this);

        pad_1_up_colour = getResources().getColor(R.color.pad_1_up_colour);
        pad_1_down_colour = getResources().getColor(R.color.pad_1_down_colour);
        pad_1_left_colour = getResources().getColor(R.color.pad_1_left_colour);
        pad_1_right_colour = getResources().getColor(R.color.pad_1_right_colour);

        pad_2_up_colour = getResources().getColor(R.color.pad_2_up_colour);
        pad_2_down_colour = getResources().getColor(R.color.pad_2_down_colour);
        pad_2_left_colour = getResources().getColor(R.color.pad_2_left_colour);
        pad_2_right_colour = getResources().getColor(R.color.pad_2_right_colour);

        // read intent data
        final Intent intent = getIntent();
        MicroBit.getInstance().setConnection_status_listener(this);

        // connect to the Bluetooth smart service
        Intent gattServiceIntent = new Intent(this, BleAdapterService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        has_vibrator = vibrator.hasVibrator();
    }

    @Override
    protected void onDestroy() {
        Log.d(Constants.TAG, "onDestroy");
        //Toast.makeText(this, "onDestroy Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onDestroy");
        super.onDestroy();
        try {
            // may already have unbound. No API to check state so....
            unbindService(mServiceConnection);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_controller, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.menu_gamepad_settings) {
            Intent intent = new Intent(FreeGameActivity.this, GamepadControllerSettingsActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.menu_controller_help) {
            Intent intent = new Intent(FreeGameActivity.this, HelpActivity.class);
            intent.putExtra(Constants.URI, Constants.CONTROLLER_HELP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(Constants.TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);





        //------------------------------------------------------------------------------------------
        //        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data!=null) {
//            imageUri = data.getData();
//            try{
//                // Convert Uri to Bitmap
//                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
//            } catch(IOException e) {
//                e.printStackTrace();
//            }
//
//            selectedImage.setImageBitmap(imageBitmap);
//        }



        if (requestCode == CAMERA_REQUEST_CODE) {
            // Capture the image and set it as background for the ImageView
//            Bitmap imageBit = (Bitmap) data.getExtras().get("data");
//            selectedImage.setImageBitmap(image);
            if (resultCode == Activity.RESULT_OK) {
                // /!\ "currentPhotoPath" seems to have been saved in the case of the tablet but NOT
                // for my phone!!
                File f = new File(currentPhotoPath); //File f = new File(imageFile.getAbsolutePath());
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute Url of Image is: " + Uri.fromFile(f));

                //Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);
                //Uri contentUri = data.getData();


                //**************
                try{
                    // Convert Uri to Bitmap
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                selectedImage.setImageBitmap(imageBitmap);
                //**************

                //mediaScanIntent.setData(contentUri);
                //this.sendBroadcast(mediaScanIntent);


                // Pre-processing (for image coming from camera)
                //--------------------------------------
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inDither = false;
                o.inSampleSize = 4;
                // Bitmap to Mat
                Mat image = new Mat();
                Utils.bitmapToMat(imageBitmap, image);
                boolean ok_for_tiles_identification = ComputerVision.preProcessingToCheckIfTilesIdentificationIsOK(image);
                if (ok_for_tiles_identification) {
                    Toast.makeText(getApplicationContext(), "✓ It will be ok for identifying the tiles", Toast.LENGTH_SHORT).show();
                    // Making consequently the computerVisionBtn clickable
                    computerVisionBtn.setEnabled(true);
                    //spinner.setVisibility(View.VISIBLE);
                    textViewIdentifiedCommands.setText("Waiting for the user to click on 'Computer Vision' (this might take a few second to execute then)");
                } else {
                    Toast.makeText(getApplicationContext(), "✗ Please consider using another picture (make sure we clearly distinguish a rectangle that is roughly horizontal and that doesn't touch the border of the image at all!)", Toast.LENGTH_SHORT).show();
                    // Making consequently the computerVisionBtn NOT clickable
                    computerVisionBtn.setEnabled(false);
                }
                //--------------------------------------


            }
        }

        if (requestCode == GALLERY_REQUEST_CODE_OLD) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();
                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                String imageFileName = "JPEG_" + timeStamp + "." + getFileExt(contentUri);
                Log.d("tag", "onActivityResult: Gallery Image Uri: " + imageFileName);

                //**************
                try{
                    // Convert Uri to Bitmap
                    imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentUri);
                } catch(IOException e) {
                    e.printStackTrace();
                }
                selectedImage.setImageBitmap(imageBitmap);
                //**************

                //selectedImage.setImageURI(contentUri);


                // Pre-processing for image coming from gallery
                //--------------------------------------
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inDither = false;
                o.inSampleSize = 4;
                // Bitmap to Mat
                Mat image = new Mat();
                Utils.bitmapToMat(imageBitmap, image);
                boolean ok_for_tiles_identification = ComputerVision.preProcessingToCheckIfTilesIdentificationIsOK(image);
                if (ok_for_tiles_identification) {
                    Toast.makeText(getApplicationContext(), "✓ It will be ok for identifying the tiles", Toast.LENGTH_SHORT).show();
                    // Making consequently the computerVisionBtn clickable
                    computerVisionBtn.setEnabled(true);
                    //spinner.setVisibility(View.VISIBLE);
                    textViewIdentifiedCommands.setText("Waiting for the user to click on 'Computer Vision' (this might take a few second to execute then)");
                } else {
                    Toast.makeText(getApplicationContext(), "✗ Please consider using another picture (make sure we clearly distinguish a rectangle that is roughly horizontal and that doesn't touch the border of the image at all!)", Toast.LENGTH_SHORT).show();
                    // Making consequently the computerVisionBtn NOT clickable
                    computerVisionBtn.setEnabled(false);
                }
                //--------------------------------------
            }
        }

        //------------------------------------------------------------------------------------------






    }

    // Service message handler�//////////////////
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            String descriptor_uuid = "";
            byte[] b = null;
            TextView value_text = null;

            switch (msg.what) {
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "Handler received characteristic written result");
                    bundle = msg.getData();
                    service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                    characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                    Log.d(Constants.TAG, "characteristic " + characteristic_uuid + " of service " + service_uuid + " written OK");
                    break;
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(text);
            }
        }
    };

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        // was sometimes getting android.view.WindowManager$BadTokenException: Unable to add window. This is an attempt to avoid trying to show a dialog when not in a suitable state
        if (!FreeGameActivity.this.hasWindowFocus()) {
            Log.d(Constants.TAG, "Activity not ready yet");
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(FreeGameActivity.this);
                builder.setTitle("");
                builder.setMessage(msg);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
            }
        });
    }

    @Override
    public void connectionStatusChanged(boolean connected) {
        if (!connected) {
            showMsg("Disconnected");
        }
    }

    @Override
    public void serviceDiscoveryStatusChanged(boolean new_state) {
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!MicroBit.getInstance().isMicrobit_connected()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showMsg("Currently disconnected - go back and connect again");
            }
            return true;
        }
        Log.d(Constants.TAG, "onTouch - " + event.actionToString((event.getAction())));
        Log.d(Constants.TAG, "onTouch action - " + event.getAction());
        Log.d(Constants.TAG, "onTouch action masked - " + event.getActionMasked());
        int pointer_index = 0;
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            pointer_index = event.getActionIndex();
        }

        final int evX = (int) event.getX(pointer_index);
        final int evY = (int) event.getY(pointer_index);

        int up_or_down=0; // 1 = down, 2 = up

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            // get the colour of the region touched from the gamepad mask and use this to figure out which pad was pressed
            Log.d(Constants.TAG, "onTouch - determining pad touched at "+evX+","+evY);
            int touchColor = getHotspotColor (R.id.gamepad_mask, evX, evY);
            int tolerance = 25;
            int pad_no=-1;
            if (closeMatch(pad_1_up_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_UP_VIEW_INX;
            }
            if (closeMatch(pad_1_down_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_DOWN_VIEW_INX;
            }
            if (closeMatch(pad_1_left_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_LEFT_VIEW_INX;
            }
            if (closeMatch(pad_1_right_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX;
            }
            if (closeMatch(pad_2_up_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_UP_VIEW_INX;
            }
            if (closeMatch(pad_2_down_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_DOWN_VIEW_INX;
            }
            if (closeMatch(pad_2_left_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_LEFT_VIEW_INX;
            }
            if (closeMatch(pad_2_right_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX;
            }
            Log.d(Constants.TAG, "Touched pad " + pad_no);
            if (pad_no > -1) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN ) {
                    up_or_down = Constants.PAD_DOWN;
                } else {
                    up_or_down = Constants.PAD_UP;
                }
                byte[] event_data = makeEvent(up_or_down, pad_no);
                if (event_data == null) {
                    return true;
                }
                Log.d(Constants.TAG,"Writing event bytes:"+ Utility.byteArrayAsHexString(event_data));
                // The line below is the very line that makes the Bit:Buggy Car move!!!
                bluetooth_le_adapter.writeCharacteristic(Utility.normaliseUUID(BleAdapterService.EVENTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.CLIENTEVENT_CHARACTERISTIC_UUID), event_data);
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                    if (has_vibrator) {
                        vibrator.vibrate(250);
                    } else {
                        AudioToneMaker.getInstance().playTone(getDtmfTone(pad_no));
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int getDtmfTone(int pad_no) {
        switch (pad_no) {
            case Constants.DPAD_1_BUTTON_UP_VIEW_INX: return ToneGenerator.TONE_DTMF_1;
            case Constants.DPAD_1_BUTTON_LEFT_VIEW_INX: return ToneGenerator.TONE_DTMF_2;
            case Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX: return ToneGenerator.TONE_DTMF_3;
            case Constants.DPAD_1_BUTTON_DOWN_VIEW_INX: return ToneGenerator.TONE_DTMF_A;
            case Constants.DPAD_2_BUTTON_UP_VIEW_INX: return ToneGenerator.TONE_DTMF_7;
            case Constants.DPAD_2_BUTTON_LEFT_VIEW_INX: return ToneGenerator.TONE_DTMF_8;
            case Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX: return ToneGenerator.TONE_DTMF_9;
            case Constants.DPAD_2_BUTTON_DOWN_VIEW_INX: return ToneGenerator.TONE_DTMF_A;
            default:
                Log.d(Constants.TAG,"Error: unrecognised pad no");
                return ToneGenerator.TONE_DTMF_1;
        }
    }


    // see https://blahti.wordpress.com/2012/06/26/images-with-clickable-areas/
    public int getHotspotColor (int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById(hotspotId);
        img.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
        img.setDrawingCacheEnabled(false);
        if (x >= 0 && y >= 0 && x <= hotspots.getWidth() && y <= hotspots.getHeight()) {
            return hotspots.getPixel(x, y);
        } else {
            return 0;
        }
    }

    // see https://blahti.wordpress.com/2012/06/26/images-with-clickable-areas/
    public boolean closeMatch (int color1, int color2, int tolerance) {
        int red_diff = (int) Math.abs (Color.red(color1) - Color.red (color2));
        int green_diff = (int) Math.abs (Color.green(color1) - Color.green(color2));
        int blue_diff = (int) Math.abs (Color.blue(color1) - Color.blue(color2));
        if (red_diff > tolerance ) return false;
        if (green_diff > tolerance ) return false;
        if (blue_diff > tolerance ) return false;
        return true;
    }

    private byte[] makeEvent(int action, int pad_number) {
//        struct event {
//            uint16 event_type;
//            uint16 event_value;
//        };

        MicroBitEvent mb_event;
        short event_value;
        Settings settings = Settings.getInstance();

        byte [] event_bytes = new byte[4];
        if (action == Constants.PAD_DOWN) { // on
            switch (pad_number) {
                case Constants.DPAD_1_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_1_button_up_on();
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_LEFT_VIEW_INX: event_value = settings.getMes_dpad_1_button_left_on();   // This is the event for turn_left
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_1_button_right_on(); // This is the event for turn_right
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_1_button_down_on();   // This is the event for turn_back
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_DOWN_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_2_button_up_on();       // This is the event for go_forward
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_LEFT_VIEW_INX: event_value =settings.getMes_dpad_2_button_left_on();
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_2_button_right_on();
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_2_button_down_on();   // This is the event for go_backward
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_DOWN_VIEW_INX");
                    break;
                default:
                    Log.d(Constants.TAG,"Error: unrecognised touch event / view");
                    return null;
            }
        } else { // off
            switch (pad_number) {
                case Constants.DPAD_1_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_1_button_up_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_LEFT_VIEW_INX: event_value = settings.getMes_dpad_1_button_left_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_1_button_right_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_1_button_down_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_DOWN_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_2_button_up_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_LEFT_VIEW_INX: event_value = settings.getMes_dpad_2_button_left_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_2_button_right_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_2_button_down_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_DOWN_VIEW_INX");
                    break;
                default:
                    Log.d(Constants.TAG,"Error: unrecognised touch event / view");
                    return null;
            }
        }
        mb_event = new MicroBitEvent(settings.getMes_dpad_controller(),event_value);
        event_bytes = mb_event.getEventBytesForBle();
        return event_bytes;
    }
}