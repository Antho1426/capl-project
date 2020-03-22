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

    public static final int GALLERY_REQUEST_CODE = 100;

    ImageView selectedImage;
    Uri imageUri;
    Bitmap grayBitmap, imageBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("tag", "Entering onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_game);
        selectedImage = findViewById(R.id.displayImageView);
        OpenCVLoader.initDebug();
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






    public void openCameraByIntent(View view) {
        Intent intent_open_camera = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA); // Cf.: https://stackoverflow.com/questions/18599421/launch-default-camera-app-no-return
        startActivity(intent_open_camera);
    }




    public void openGallery(View view) {
        Intent open_gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(open_gallery, GALLERY_REQUEST_CODE);
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
    }



}
