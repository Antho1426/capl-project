package ch.epfl.mobots.papl;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ProgressBar;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.CopyOnWriteArrayList;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class FreeGameActivity extends AppCompatActivity {


    // Cf.: https://developer.android.com/guide/components/activities/activity-lifecycle
    static final String STATE_CURRENT_PHOTO_PATH = "currentPhotoPath";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_CURRENT_PHOTO_PATH, currentPhotoPath);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }











    public static boolean computer_vision_completed = false;


    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE_OLD = 105;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;



    ImageView selectedImage;
    // Uri imageUri;
    Bitmap imageBitmap, croppedBitmap, croppedBitmap_resized; //, grayBitmap

    Button cameraBtn, galleryBtn, computerVisionBtn;
    String currentPhotoPath; // absolute path of the photo
    File imageFile; // the image file in which there will be our image from the camera or the gallery

    private TextView textViewIdentifiedCommands;

    private ProgressBar spinner;









    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "Entering onCreate");

        super.onCreate(savedInstanceState); // Always call the superclass first

        setContentView(R.layout.activity_free_game);






        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            currentPhotoPath = savedInstanceState.getString(STATE_CURRENT_PHOTO_PATH);
        }







        selectedImage = findViewById(R.id.displayImageView);

        cameraBtn = findViewById(R.id.btn_camera);
        galleryBtn = findViewById(R.id.btn_gallery);
        computerVisionBtn = findViewById(R.id.btn_computer_vision);

        textViewIdentifiedCommands = findViewById(R.id.text_view_identified_commands);

        // Info for wheel spinner: https://www.tutorialspoint.com/android/android_loading_spinner.htm
        spinner = findViewById(R.id.progressBar);
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


                Toast.makeText(FreeGameActivity.this, "Entering computer vision phase...", Toast.LENGTH_SHORT).show();


                spinner.setVisibility(View.VISIBLE);


                executeTilesIdentification();


                spinner.setVisibility(View.INVISIBLE);



            }
        });



    }





















    public void executeTilesIdentification() {

        if (selectedImage.getDrawable() != null) {



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

            //-----
            //String[] commands = ComputerVision.TilesIdentification(image);
            Pair<String[], Mat> result = ComputerVision.TilesIdentification(image); // "How to return multiple objects from a Java method?" --> cf.: https://stackoverflow.com/questions/457629/how-to-return-multiple-objects-from-a-java-method
            String[] commands = result.first;
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




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


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
        Toast.makeText(this, "onStart Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "onResume Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Toast.makeText(this, "onPause Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Toast.makeText(this, "onStop Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(this, "onRestart Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onRestart");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "onDestroy Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onDestroy");
    }






}
