package ch.epfl.mobots.capl.ui;

import ch.epfl.mobots.capl.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class TakeAndLoadPictureActivity extends AppCompatActivity {


    private ImageView image_test_2;
    private Button btnCameraTest_2, btnGalleryTest_2;
    private final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_GALLERY = 2;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_and_load_picture);

    }








}
