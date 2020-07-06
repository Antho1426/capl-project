package ch.epfl.mobots.capl.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.OvershootInterpolator;

import ch.epfl.mobots.capl.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;

public class FloatingActionButtonActivity2 extends AppCompatActivity implements View.OnClickListener {


    FloatingActionButton fabMain, fabOne, fabTwo, fabThree;
    Float translationY = 100f;

    OvershootInterpolator interpolator = new OvershootInterpolator();

    private static final String TAG = "MainActivity";

    Boolean isMenuOpen = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating_action_button2);

        initFabMenu();

    }

    private void initFabMenu() {
        fabMain = (FloatingActionButton) findViewById(R.id.fabMain_o);
        fabOne = (FloatingActionButton) findViewById(R.id.fabOne_o);
        fabTwo = (FloatingActionButton) findViewById(R.id.fabTwo_o);
        fabThree = (FloatingActionButton) findViewById(R.id.fabThree_o);

        fabOne.setAlpha(0f);
        fabTwo.setAlpha(0f);
        fabThree.setAlpha(0f);

        fabOne.setTranslationY(translationY);
        fabTwo.setTranslationY(translationY);
        fabThree.setTranslationY(translationY);

        fabMain.setOnClickListener(this);
        fabOne.setOnClickListener(this);
        fabTwo.setOnClickListener(this);
        fabThree.setOnClickListener(this);
    }

    private void openMenu() {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fabOne.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();


    }

    private void closeMenu() {
        isMenuOpen = !isMenuOpen;

        fabMain.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fabOne.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();

    }

    private void handleFabOne() {
        Log.i(TAG, "handleFabOne: ");
    }


    @Override
    public void onClick(View view) {



        switch (view.getId()) {
            case R.id.fabMain_o:
                Log.i(TAG, "onClick: fab main");
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.fabOne_o:
                Log.i(TAG, "onClick: fab one");
                handleFabOne();
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                }
                break;
            case R.id.fabTwo_o:
                Log.i(TAG, "onClick: fab two");
                break;
            case R.id.fabThree_o:
                Log.i(TAG, "onClick: fab three");
                break;
        }

    }



}
