package ch.epfl.mobots.capl.ui;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.mobots.capl.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

public class OpenCVTestActivity2 extends AppCompatActivity {

    private static final String TAG = "DEBUG";

    Mat img;

    Timer buttonTimer;




    // Cf.: https://stackoverflow.com/questions/35090838/no-implementation-found-for-long-org-opencv-core-mat-n-mat-error-using-opencv
    //-----------------------------
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCV", "OpenCV loaded successfully");
                    img = new Mat();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    public void onResume()
    {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d("OpenCV", "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback); // Displays the message "Package not found. OpenCV Manager package was not found! Try to install it?"
        } else {
            Log.d("OpenCV", "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }
    //-----------------------------






    // Cf.: https://github.com/akshika47/OpenCV-Android-Object-Detection
    //-----------------------------
//    private static final int REQUEST_PERMISSION = 100;
//    private int w, h;
//    private CameraBridgeViewBase mOpenCvCameraView;
//    TextView tvName;
//    Scalar RED = new Scalar(255, 0, 0);
//    Scalar GREEN = new Scalar(0, 255, 0);
//    FeatureDetector detector;
//    DescriptorExtractor descriptor;
//    DescriptorMatcher matcher;
//    Mat descriptors2,descriptors1;
//    Mat img1;
//    MatOfKeyPoint keypoints1,keypoints2;
//
//    static {
//        if (!OpenCVLoader.initDebug())
//            Log.d("ERROR", "Unable to load OpenCV");
//        else
//            Log.d("SUCCESS", "OpenCV loaded");
//    }
//
//    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
//        @Override
//        public void onManagerConnected(int status) {
//            switch (status) {
//                case LoaderCallbackInterface.SUCCESS: {
//                    Log.i(TAG, "OpenCV loaded successfully");
//                    mOpenCvCameraView.enableView();
//                    try {
//                        initializeOpenCVDependencies();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;
//                default: {
//                    super.onManagerConnected(status);
//                }
//                break;
//            }
//        }
//    };
//
//    private void initializeOpenCVDependencies() throws IOException {
//        img = new Mat();
////        mOpenCvCameraView.enableView();
////        detector = FeatureDetector.create(FeatureDetector.ORB);
////        descriptor = DescriptorExtractor.create(DescriptorExtractor.ORB);
////        matcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
////        img1 = new Mat();
////        AssetManager assetManager = getAssets();
////        InputStream istr = assetManager.open("a.jpeg");
////        Bitmap bitmap = BitmapFactory.decodeStream(istr);
////        Utils.bitmapToMat(bitmap, img1);
////        Imgproc.cvtColor(img1, img1, Imgproc.COLOR_RGB2GRAY);
////        img1.convertTo(img1, 0); //converting the image to match with the type of the cameras image
////        descriptors1 = new Mat();
////        keypoints1 = new MatOfKeyPoint();
////        detector.detect(img1, keypoints1);
////        descriptor.compute(img1, keypoints1, descriptors1);
//    }
    //-----------------------------











    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_c_v_test2);

        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        OpenCVLoader.initDebug();

//        buttonTimer = new Timer();
//        buttonTimer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        OpenCVLoader.initDebug();
//                    }
//                });
//            }
//        }, 5000);

    }

    public void displayToast(View view) {
        Toast.makeText(getApplicationContext(), "Preparing OpenCV for Android!", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Entering displayToast");
    }

    public void applyFilter(View view) {
        Log.i(TAG, "Entering applyFilter");

        //-----
//        Mat img = null;
//        //Mat img2 = new Mat();
//        try {
//            img = Utils.loadResource(getApplicationContext(), R.drawable.test);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        //-----

        //-----
        Bitmap selectedImage_Bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.test);
        // Scaling down the Bitmap
        Bitmap selectedImage_Bitmap_Resized = Bitmap.createScaledBitmap(selectedImage_Bitmap, 2592, 1944, false);
        // Converting the Bitmap to Mat
        img = new Mat();
        Utils.bitmapToMat(selectedImage_Bitmap_Resized, img);
        //-----


        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGRA);

        Mat img_result = img.clone();
        Imgproc.Canny(img, img_result, 80, 90);
        Bitmap img_bitmap = Bitmap.createBitmap(img_result.cols(), img_result.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_result, img_bitmap);
        ImageView imageView = (ImageView) findViewById(R.id.img);
        imageView.setImageBitmap(img_bitmap);
    }


}
