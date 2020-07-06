package ch.epfl.mobots.capl.ui;

import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import ch.epfl.mobots.capl.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;

import java.util.Timer;
import java.util.TimerTask;

// Cf.: https://youtu.be/rBVU9KBRRe8
public class ControlledImageView extends AppCompatActivity {

    // ImageViews
    private ImageView cardBot;

    private FrameLayout mapFrame;
    //private Drawable cardBot_Init, cardBot_GoForward, cardBot_GoBackward, cardBot_TurnRight, cardBot_TurnLeft, cardBot_TurnBack, cardBot_Blocked;
    private float boxX, boxY, boxX_Finish, boxY_Finish;
    private boolean action_up, action_down, action_right, action_left, action_back;

    private Button btnGoForward, btnGoBackward, btnTurnRight, btnTurnLeft, btnTurnBack;

    // TextViews
    private TextView textViewNumberOfPlayers, textViewCardbotPose, textViewXPosition, textViewYPosition, textViewOrientation, boxStart, boxFinish;

    // EditText
    private EditText editTextNumberOfPlayers;

    private GridLayout gridButtons;

    private String orientation;

    private int period = 100;
    private int totalTime = 1500;

    // Go forward, go backward
    private int displacementUnit = 1;
    private int dispRepetition = 97;   // --> 1*95 = 95 = displacement unit of the robot on this map
    private int dispDelay = (int) totalTime/dispRepetition;

    // Turn right, turn left
    private int degreeIncrement = 1;
    private int degreeRepetition = 90;
    private int degreeDelay = (int) totalTime/degreeRepetition;

    // Turn back
    private int degreeIncrementBack = 2;
    private int degreeRepetitionBack = 90;
    private int degreeDelayBack = (int) totalTime/degreeRepetition;

    // Timer and Handlers
    private Timer timerMap = new Timer();
    private Handler handlerMap = new Handler();
    private Handler handlerMoveDelay = new Handler();

    // Dimensions of the map
    private int nRows = 9;
    private int nCols = 13;

    // For TextViews of pose of the robot
    private int digit;
    private char letter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_controlled_image_view);


        cardBot = (ImageView) findViewById(R.id.virtual_cardbot);

        // TextViews
        boxStart = (TextView) findViewById(R.id.box_start);
        boxFinish = (TextView) findViewById(R.id.box_finish);

        mapFrame = (FrameLayout) findViewById(R.id.map_frame);

        //cardBot_Init = getResources().getDrawable(R.drawable.virtual_cardbot_init);
        // cardBot_GoForward = getResources().getDrawable(R.drawable.virtual_cardbot_go_forward);
        // cardBot_GoBackward = getResources().getDrawable(R.drawable.virtual_cardbot_go_backward);
        // cardBot_TurnRight = getResources().getDrawable(R.drawable.virtual_cardbot_turn_right);
        // cardBot_TurnLeft = getResources().getDrawable(R.drawable.virtual_cardbot_turn_left);
        // cardBot_TurnBack = getResources().getDrawable(R.drawable.virtual_cardbot_turn_back);
        //cardBot_Blocked = getResources().getDrawable(R.drawable.virtual_cardbot_blocked);

        btnGoForward = (Button) findViewById(R.id.btn_go_forward);
        btnGoBackward = (Button) findViewById(R.id.btn_go_backward);
        btnTurnRight = (Button) findViewById(R.id.btn_turn_right);
        btnTurnLeft = (Button) findViewById(R.id.btn_turn_left);
        btnTurnBack = (Button) findViewById(R.id.btn_turn_back);

        gridButtons = (GridLayout) findViewById(R.id.grid_buttons);


        // Setting the chalkboard se font to some TextViews and the EditText
        textViewNumberOfPlayers = (TextView) findViewById(R.id.text_view_number_of_players);
        textViewCardbotPose = (TextView) findViewById(R.id.text_view_cardbot_pose);
        textViewXPosition = (TextView) findViewById(R.id.text_view_x_position);
        textViewYPosition = (TextView) findViewById(R.id.text_view_y_position);
        textViewOrientation = (TextView) findViewById(R.id.text_view_orientation);
        editTextNumberOfPlayers = (EditText) findViewById(R.id.edit_text_number_of_players);
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/chalkboardseregular.ttf");
        textViewNumberOfPlayers.setTypeface(type);
        textViewCardbotPose.setTypeface(type);
        textViewXPosition.setTypeface(type);
        textViewYPosition.setTypeface(type);
        textViewOrientation.setTypeface(type);
        editTextNumberOfPlayers.setTypeface(type);




        // Random initialization of the position of the Cardbot on the map
        // Random along x (cf.: https://javarevisited.blogspot.com/2013/05/how-to-generate-random-numbers-in-java-between-range.html)
        int randomInitialX = (int) (nCols * Math.random());
        // Random along y
        int randomInitialY = (int) (nRows * Math.random());
        // Random orientation
        int randomNumberOrientation = (int) (4 * Math.random());
        String randomInitialOrientation = null;
        switch (randomNumberOrientation) {
            case 0:
                randomInitialOrientation = "North";
                break;
            case 1:
                randomInitialOrientation = "East";
                break;
            case 2:
                randomInitialOrientation = "South";
                break;
            case 3:
                randomInitialOrientation = "West";
                break;
        }
        // Setting the virtual Cardbot to its initial pose
        boxX = randomInitialX*dispRepetition;
        cardBot.setX(boxX);
        boxStart.setX(boxX);
        boxY = randomInitialY*dispRepetition;
        cardBot.setY(boxY);
        boxStart.setY(boxY);
        assert randomInitialOrientation != null;
        switch (randomInitialOrientation) {
            case "North":
                cardBot.setRotation(cardBot.getRotation() + 0); // The Cardbot is facing North by default
                orientation = "North";
                break;
            case "East":
                cardBot.setRotation(cardBot.getRotation() + 90);
                orientation = "East";
                break;
            case "South":
                cardBot.setRotation(cardBot.getRotation() + 180);
                orientation = "South";
                break;
            case "West":
                cardBot.setRotation(cardBot.getRotation() - 90);
                orientation = "West";
                break;
        }

        // Adjusting the TextViews presenting the initial pose of the Cardbot
        // 1. X
        textViewXPosition.setText(Integer.toString(Math.round(boxX/dispRepetition)+1));
        // 2. Y
        //----
        digit = Math.round(boxY/dispRepetition)+1;
        letter = mapDigitToLetter(digit);
        textViewYPosition.setText(String.valueOf(letter));
        //----
        // 3. Orientation
        textViewOrientation.setText(orientation);



        // Setting random initial position of the destination
        // Random along x (cf.: https://javarevisited.blogspot.com/2013/05/how-to-generate-random-numbers-in-java-between-range.html)
        int randomInitialX_Finish = (int) (nCols * Math.random());
        // Random along y
        int randomInitialY_Finish = (int) (nRows * Math.random());
        boxX_Finish = randomInitialX_Finish*dispRepetition;
        boxY_Finish = randomInitialY_Finish*dispRepetition;
        boxFinish.setX(boxX_Finish);
        boxFinish.setY(boxY_Finish);


//        findViewById(R.id.btn_up).setOnTouchListener(this);
//        findViewById(R.id.btn_down).setOnTouchListener(this);
//        findViewById(R.id.btn_right).setOnTouchListener(this);
//        findViewById(R.id.btn_left).setOnTouchListener(this);
//        findViewById(R.id.btn_back).setOnTouchListener(this);

        // Start the timer
//        timerMap.schedule(new TimerTask() {
//           @Override
//           public void run() {
//               handlerMap.post(new Runnable() {
//                   @Override
//                   public void run() {
//                       changePos();
//                   }
//               });
//           }
//        }, 0, 20);


    }












    // Rotate ImageView
    // Cf.: https://www.tutorialspoint.com/how-to-rotate-image-in-image-view-by-an-angle-in-android
    // Rotate ImageView to the right by 90°
//    private void rotateRight(ImageView imageView) {
//        Matrix matrix = new Matrix();
//        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
//        matrix.postRotate((float) 90, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
//        imageView.setImageMatrix(matrix);
//    }
//    // Rotate ImageView to the left by 90°
//    private void rotateLeft(ImageView imageView) {
//        Matrix matrix = new Matrix();
//        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
//        matrix.postRotate((float) -90, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
//        imageView.setImageMatrix(matrix);
//    }
//    // Rotate ImageView to the right by 180°
//    private void rotateBack(ImageView imageView) {
//        Matrix matrix = new Matrix();
//        imageView.setScaleType(ImageView.ScaleType.MATRIX); //required
//        matrix.postRotate((float) 180, imageView.getDrawable().getBounds().width()/2,    imageView.getDrawable().getBounds().height()/2);
//        imageView.setImageMatrix(matrix);
//    }


















    public void adaptPosOnScreen() {
        boxX = cardBot.getX();
        boxY = cardBot.getY();

        // Up
        if (action_up) {

            switch (orientation) {
                case "North":
                    boxY -= displacementUnit;
                    cardBot.setY(boxY);
                    //----
                    digit = Math.round(boxY/dispRepetition)+1;
                    letter = mapDigitToLetter(digit);
                    textViewYPosition.setText(String.valueOf(letter)); // "You can "convert" a character into a String with the method String.valueOf(char)", cf.: https://stackoverflow.com/questions/13501540/how-to-assign-a-single-char-to-a-textview
                    //----
                    break;
                case "East":
                    boxX += displacementUnit;
                    cardBot.setX(boxX);
                    textViewXPosition.setText(Integer.toString(Math.round(boxX/dispRepetition)+1));
                    break;
                case "West":
                    boxX -= displacementUnit;
                    cardBot.setX(boxX);
                    textViewXPosition.setText(Integer.toString(Math.round(boxX/dispRepetition)+1));
                    break;
                case "South":
                    boxY += displacementUnit;
                    cardBot.setY(boxY);
                    //----
                    digit = Math.round(boxY/dispRepetition)+1;
                    letter = mapDigitToLetter(digit);
                    textViewYPosition.setText(String.valueOf(letter));
                    //----
                    break;
            }
        }

        // Down
        if (action_down) {

            switch (orientation) {
                case "North":
                    boxY += displacementUnit;
                    cardBot.setY(boxY);
                    //----
                    digit = Math.round(boxY/dispRepetition)+1;
                    letter = mapDigitToLetter(digit);
                    textViewYPosition.setText(String.valueOf(letter));
                    //----
                    break;
                case "East":
                    boxX -= displacementUnit;
                    cardBot.setX(boxX);
                    textViewXPosition.setText(Integer.toString(Math.round(boxX/dispRepetition)+1));
                    break;
                case "West":
                    boxX += displacementUnit;
                    cardBot.setX(boxX);
                    textViewXPosition.setText(Integer.toString(Math.round(boxX/dispRepetition)+1));
                    break;
                case "South":
                    boxY -= displacementUnit;
                    cardBot.setY(boxY);
                    //----
                    digit = Math.round(boxY/dispRepetition)+1;
                    letter = mapDigitToLetter(digit);
                    textViewYPosition.setText(String.valueOf(letter));
                    //----
                    break;
            }
        }

        // Right
        if (action_right) {

            //cardBot_TurnRight = getResources().getDrawable(R.drawable.virtual_cardbot_turn_right);
            //cardBot.setImageDrawable(cardBot_TurnRight);
            cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_turn_right));
            //rotateRight(cardBot);
            cardBot.setRotation(cardBot.getRotation() + degreeIncrement); // Cf.: https://stackoverflow.com/questions/28259534/how-to-rotate-image-in-imageview-on-button-click-each-time

        }

        // Left
        if (action_left) {

            //cardBot_TurnLeft = getResources().getDrawable(R.drawable.virtual_cardbot_turn_left);
            //cardBot.setImageDrawable(cardBot_TurnLeft);
            cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_turn_left));
            //rotateLeft(cardBot);
            cardBot.setRotation(cardBot.getRotation() - degreeIncrement);

        }

        // Back
        if (action_back) {

            //cardBot_TurnBack = getResources().getDrawable(R.drawable.virtual_cardbot_turn_back);
            //cardBot.setImageDrawable(cardBot_TurnBack);
            cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_turn_back));
            //rotateBack(cardBot);
            cardBot.setRotation(cardBot.getRotation() + degreeIncrementBack);

        }


        // Set boundaries to the Map
        // Vertical boundaries

        // cardBot_Blocked = getResources().getDrawable(R.drawable.virtual_cardbot_blocked);

        if ( boxY < 0 ) {
            cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_blocked));
            boxY = 0;
            cardBot.setY(boxY);
        }
        if ( boxY > (mapFrame.getHeight() - cardBot.getHeight()) ) {
            cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_blocked));
            boxY = mapFrame.getHeight() - cardBot.getHeight();
            cardBot.setY(boxY);
        }
        // Horizontal boundaries
        if ( boxX < 0 ) {
            cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_blocked));
            boxX = 0;
            cardBot.setX(boxX);
        }
        if ( boxX > (mapFrame.getWidth() - cardBot.getWidth()) ) {
            cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_blocked));
            boxX = mapFrame.getWidth() - cardBot.getWidth();
            cardBot.setX(boxX);
        }

    }



//    @Override
//    public boolean onTouch(View view, MotionEvent motionEvent) {
//
//        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
//            switch (view.getId()) {
//                case R.id.btn_up:
//                    action_up = true;
//                    break;
//                case R.id.btn_down:
//                    action_down = true;
//                    break;
//                case R.id.btn_right:
//                    action_right = true;
//                    break;
//                case R.id.btn_left:
//                    action_left = true;
//                    break;
//                case R.id.btn_back:
//                    action_back = true;
//                    break;
//            }
//        } else {
//            action_up = false;
//            action_down = false;
//            action_right = false;
//            action_left = false;
//            action_back = false;
//        }
//
//        return true;
//    }

















    public void moveVirtualCardbotForward(View view) {
        action_up = true;
        action_down = false;
        action_right = false;
        action_left = false;
        action_back = false;

        //cardBot_GoForward = getResources().getDrawable(R.drawable.virtual_cardbot_go_forward);
        //cardBot.setImageDrawable(cardBot_GoForward);
        cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_go_forward));

        for (int i = 0; i < dispRepetition; i++) {
            handlerMoveDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    adaptPosOnScreen();
                }
            }, dispDelay * i); // Delay of "delay" [ms]
        }



        // Disabling the buttons while the robot is moving (i.e. while 1.5[s])
        // Cf.: https://stackoverflow.com/questions/45136552/disable-button-after-a-click-for-2-seconds-and-resume-back
        btnGoForward.setEnabled(false);
        btnGoBackward.setEnabled(false);
        btnTurnRight.setEnabled(false);
        btnTurnLeft.setEnabled(false);
        btnTurnBack.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btnGoForward.setEnabled(true);
                        btnGoBackward.setEnabled(true);
                        btnTurnRight.setEnabled(true);
                        btnTurnLeft.setEnabled(true);
                        btnTurnBack.setEnabled(true);
                    }
                });
            }
        }, 1500);



        // Start the timer
//        timerMap.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handlerMap.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        changePos();
//                    }
//                });
//            }
//        }, 0, period);
    }


    public void moveVirtualCardbotBackward(View view) {
        action_up = false;
        action_down = true;
        action_right = false;
        action_left = false;
        action_back = false;

        //cardBot_GoBackward = getResources().getDrawable(R.drawable.virtual_cardbot_go_backward);
        //cardBot.setImageDrawable(cardBot_GoBackward);
        cardBot.setImageDrawable(getResources().getDrawable(R.drawable.virtual_cardbot_go_backward));

        for (int i = 0; i < dispRepetition; i++) {
            handlerMoveDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adaptPosOnScreen();
                }
            }, dispDelay * i); // Delay of "delay" [ms] (i.e. total time to execute the movement)
        }


        // Disabling the buttons while the robot is moving (i.e. while 1.5[s])
        btnGoForward.setEnabled(false);
        btnGoBackward.setEnabled(false);
        btnTurnRight.setEnabled(false);
        btnTurnLeft.setEnabled(false);
        btnTurnBack.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btnGoForward.setEnabled(true);
                        btnGoBackward.setEnabled(true);
                        btnTurnRight.setEnabled(true);
                        btnTurnLeft.setEnabled(true);
                        btnTurnBack.setEnabled(true);
                    }
                });
            }
        }, 1500);


//        for (int i = 0; i < 100; i++) {
//            changePos();
//        }
        // Start the timer
//        timerMap.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handlerMap.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        changePos();
//                    }
//                });
//            }
//        }, 0, period);
    }


    public void moveVirtualCardbotRight(View view) {
        action_up = false;
        action_down = false;
        action_right = true;
        action_left = false;
        action_back = false;

        // updating the orientation of the robot when turning on the right
        switch (orientation) {
            case "North":
                orientation = "East";
                break;
            case "East":
                orientation = "South";
                break;
            case "South":
                orientation = "West";
                break;
            case "West":
                orientation = "North";
                break;
        }

        textViewOrientation.setText(orientation);

        for (int i = 0; i < degreeRepetition; i++) {
            handlerMoveDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adaptPosOnScreen();
                }
            }, degreeDelay * i); // Delay of "delay" [ms] (i.e. total time to execute the movement)
        }


        // Disabling the buttons while the robot is moving (i.e. while 1.5[s])
        btnGoForward.setEnabled(false);
        btnGoBackward.setEnabled(false);
        btnTurnRight.setEnabled(false);
        btnTurnLeft.setEnabled(false);
        btnTurnBack.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btnGoForward.setEnabled(true);
                        btnGoBackward.setEnabled(true);
                        btnTurnRight.setEnabled(true);
                        btnTurnLeft.setEnabled(true);
                        btnTurnBack.setEnabled(true);
                    }
                });
            }
        }, 1500);


        // Start the timer
//        timerMap.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handlerMap.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        changePos();
//                    }
//                });
//            }
//        }, 0, period);
    }


    public void moveVirtualCardbotLeft(View view) {
        action_up = false;
        action_down = false;
        action_right = false;
        action_left = true;
        action_back = false;

        // updating the orientation of the robot
        switch (orientation) {
            case "North":
                orientation = "West";
                break;
            case "West":
                orientation = "South";
                break;
            case "South":
                orientation = "East";
                break;
            case "East":
                orientation = "North";
                break;
        }

        textViewOrientation.setText(orientation);

        for (int i = 0; i < degreeRepetition; i++) {
            handlerMoveDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adaptPosOnScreen();
                }
            }, degreeDelay * i); // Delay of "delay" [ms] (i.e. total time to execute the movement)
        }


        // Disabling the buttons while the robot is moving (i.e. while 1.5[s])
        btnGoForward.setEnabled(false);
        btnGoBackward.setEnabled(false);
        btnTurnRight.setEnabled(false);
        btnTurnLeft.setEnabled(false);
        btnTurnBack.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btnGoForward.setEnabled(true);
                        btnGoBackward.setEnabled(true);
                        btnTurnRight.setEnabled(true);
                        btnTurnLeft.setEnabled(true);
                        btnTurnBack.setEnabled(true);
                    }
                });
            }
        }, 1500);


        // Start the timer
//        timerMap.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handlerMap.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        changePos();
//                    }
//                });
//            }
//        }, 0, period);
    }


    public void moveVirtualCardbotBack(View view) {
        action_up = false;
        action_down = false;
        action_right = false;
        action_left = false;
        action_back = true;

        // updating the orientation of the robot when turning back
        switch (orientation) {
            case "North":
                orientation = "South";
                break;
            case "West":
                orientation = "East";
                break;
            case "South":
                orientation = "North";
                break;
            case "East":
                orientation = "West";
                break;
        }

        textViewOrientation.setText(orientation);

        for (int i = 0; i < degreeRepetitionBack; i++) {
            handlerMoveDelay.postDelayed(new Runnable() {
                @Override
                public void run() {
                    adaptPosOnScreen();
                }
            }, degreeDelayBack * i); // Delay of "delay" [ms] (i.e. total time to execute the movement)
        }


        // Disabling the buttons while the robot is moving (i.e. while 1.5[s])
        btnGoForward.setEnabled(false);
        btnGoBackward.setEnabled(false);
        btnTurnRight.setEnabled(false);
        btnTurnLeft.setEnabled(false);
        btnTurnBack.setEnabled(false);

        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        btnGoForward.setEnabled(true);
                        btnGoBackward.setEnabled(true);
                        btnTurnRight.setEnabled(true);
                        btnTurnLeft.setEnabled(true);
                        btnTurnBack.setEnabled(true);
                    }
                });
            }
        }, 1500);


        // Start the timer
//        timerMap.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                handlerMap.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        changePos();
//                    }
//                });
//            }
//        }, 0, period);
    }



    public char mapDigitToLetter(int digit) {

        char letter = 'O';

        switch (digit) {
            case 1:
                letter = 'A';
                break;
            case 2:
                letter = 'B';
                break;
            case 3:
                letter = 'C';
                break;
            case 4:
                letter = 'D';
                break;
            case 5:
                letter = 'E';
                break;
            case 6:
                letter = 'F';
                break;
            case 7:
                letter = 'G';
                break;
            case 8:
                letter = 'H';
                break;
            case 9:
                letter = 'I';
                break;
        }

        return letter;
    }




}
