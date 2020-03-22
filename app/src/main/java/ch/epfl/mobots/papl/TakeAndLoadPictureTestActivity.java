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
public class TakeAndLoadPictureTestActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView image;
    private Button btnCameraTest, btnGalleryTest;
    private final int REQUEST_IMAGE_CAPTURE = 1, REQUEST_IMAGE_GALLERY = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_and_load_picture_test);
        image = (ImageView) findViewById(R.id.image);
        btnCameraTest = (Button) findViewById(R.id.btn_camera_test);
        btnCameraTest.setOnClickListener(this);
        btnGalleryTest = (Button) findViewById(R.id.btn_gallery_test);
        btnGalleryTest.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_camera_test:
                Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (iCamera.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(iCamera, REQUEST_IMAGE_CAPTURE);
                }
                break;
            case R.id.btn_gallery_test:
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
                image.setImageBitmap(bitmap);
            } else if (requestCode == REQUEST_IMAGE_GALLERY) {
                Uri uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    image.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
