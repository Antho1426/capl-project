package ch.epfl.mobots.papl;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

import android.os.Bundle;




public class OpenCVTestActivity extends AppCompatActivity {

    private static final String TAG = "DEBUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_c_v_test);
        OpenCVLoader.initDebug();
    }

    public void displayToast(View view) {
        Toast.makeText(getApplicationContext(), "Preparing OpenCV for Android!", Toast.LENGTH_LONG).show();
        Log.i(TAG, "Entering displayToast");
    }

    public void applyFilter(View view) {
        Log.i(TAG, "Entering applyFilter");
        Mat img = null;

        try {
            img = Utils.loadResource(getApplicationContext(), R.drawable.test);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGRA);

        Mat img_result = img.clone();
        Imgproc.Canny(img, img_result, 80, 90);
        Bitmap img_bitmap = Bitmap.createBitmap(img_result.cols(), img_result.rows(),Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_result, img_bitmap);
        ImageView imageView = findViewById(R.id.img);
        imageView.setImageBitmap(img_bitmap);
    }


}
