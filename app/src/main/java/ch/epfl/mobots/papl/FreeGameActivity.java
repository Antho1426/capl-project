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
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.os.Environment.getExternalStoragePublicDirectory;

public class FreeGameActivity extends AppCompatActivity {

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE_OLD = 105;
    public static final int GALLERY_REQUEST_CODE = 100;

    ImageView selectedImage;
    Uri imageUri;
    Bitmap grayBitmap, imageBitmap;

    Button cameraBtn, galleryBtn;
    String currentPhotoPath; // absolute path of the photo



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "Entering onCreate");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_game);

        selectedImage = findViewById(R.id.displayImageView);
        cameraBtn = findViewById(R.id.btn_camera);
        galleryBtn = findViewById(R.id.btn_gallery);

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



    }




    public void computerVision(View view) {
        if (selectedImage.getDrawable() != null) {
            Mat Rgba = new Mat();
            Mat grayMat = new Mat();

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inDither = false;
            o.inSampleSize = 4;

            int width = imageBitmap.getWidth();
            int height = imageBitmap.getHeight();

            grayBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

            // Bitmap to Mat
            Utils.bitmapToMat(imageBitmap, Rgba);

            Imgproc.cvtColor(Rgba, grayMat, Imgproc.COLOR_RGB2GRAY);

            Utils.matToBitmap(grayMat, grayBitmap);

            selectedImage.setImageBitmap(grayBitmap);
        }
    }








    private void askCameraPermission() {
        Log.d("tag", "Entering askCameraPermission");
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERM_CODE);
        } else {
            //openCamera();
            dispatchTakePictureIntent();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERM_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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


        if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK && data!=null) {
            imageUri = data.getData();
            try{
                // Convert Uri to Bitmap
                imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            } catch(IOException e) {
                e.printStackTrace();
            }

            selectedImage.setImageBitmap(imageBitmap);
        }



        if (requestCode == CAMERA_REQUEST_CODE) {
            // Capture the image and set it as background for the ImageView
//            Bitmap image = (Bitmap) data.getExtras().get("data");
//            selectedImage.setImageBitmap(image);
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(currentPhotoPath);
                selectedImage.setImageURI(Uri.fromFile(f));
                Log.d("tag", "Absolute Url of Image is: " + Uri.fromFile(f));

                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(f);

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
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_"; // creating the image file
        // setting the storage directory
        // cf.: https://androidpedia.net/en/tutorial/150/storing-files-in-internal-external-storage
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        //File storageDir = Environment.getExternalStorageDirectory();
        //File storageDir = Environment.getDataDirectory();
        //File storageDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        //File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // <-- this directory works (when we have no SD card inserted in the tablet) (but this doesn't allow to save the picture taken in the memory of the tablet...)

        // checking that our directory exists:
        storageDir.isDirectory();

        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",   /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath(); // getting the absolute path of the image where it is saved
        return image;
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
            }
        }
    }






}
