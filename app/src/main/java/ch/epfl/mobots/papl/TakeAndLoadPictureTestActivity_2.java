package ch.epfl.mobots.papl;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;

// Cf.: https://www.youtube.com/watch?v=c2s2yNXPw2g
public class TakeAndLoadPictureTestActivity_2 extends AppCompatActivity implements View.OnClickListener {

    private ImageView image_test_2;
    private Button btnCameraTest_2, btnGalleryTest_2;
    private final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_and_load_picture_test_2);
        image_test_2 = (ImageView) findViewById(R.id.image_test_2);
        btnCameraTest_2 = (Button) findViewById(R.id.btn_camera_test_2);
        btnCameraTest_2.setOnClickListener(this);
        btnGalleryTest_2 = (Button) findViewById(R.id.btn_gallery_test_2);
        btnGalleryTest_2.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera_test_2:
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (iCamera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(iCamera, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case R.id.btn_gallery_test_2:
                Intent iGallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                iGallery.setType("image/*");
                startActivityForResult(iGallery, REQUEST_IMAGE_GALLERY);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bitmap bitmap = (Bitmap) data.getExtras().get("data");
                image_test_2.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    image_test_2.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
