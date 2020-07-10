package ch.epfl.mobots.capl_framework.ui;
/*
 * Author: Martin Woolley
 * Twitter: @bluetooth_mdw
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothGattService;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import ch.epfl.mobots.capl_framework.ComputerVision;
import ch.epfl.mobots.capl_framework.Constants;
import ch.epfl.mobots.capl_framework.MicroBit;
import ch.epfl.mobots.capl_framework.MicroBitEvent;
import ch.epfl.mobots.capl_framework.R;
//import com.bluetooth.mwoolley.microbitbledemo.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import ch.epfl.mobots.capl_framework.Settings;
import ch.epfl.mobots.capl_framework.Utility;
import ch.epfl.mobots.capl_framework.bluetooth.BleAdapterService;
import ch.epfl.mobots.capl_framework.bluetooth.ConnectionStatusListener;


import ch.epfl.mobots.capl_framework.CustomOnItemSelectedListenerForExamplePictureOfSequence;


/**
 * Microbit Events
 *
 * Measure Temperature in micro:bit - send event when it exceeds or falls below a hard coded threshold
 *
 * Requires a custom microbit application with the BLE profile in the build
 */


import static android.content.pm.PackageManager.PERMISSION_GRANTED;


public class FreeGameActivity extends AppCompatActivity implements ConnectionStatusListener, OnTouchListener, View.OnClickListener {



    public static final String EXTRA_NAME = "name";
    public static final String EXTRA_ID = "id";

    boolean DebugModeOn = false;


    private BleAdapterService bluetooth_le_adapter;
    private Vibrator vibrator;
    private boolean has_vibrator;
    private ImageView gamepad;
    private ImageView gamepad_mask;

    private int pad_1_up_colour;
    private int pad_1_down_colour;
    private int pad_1_left_colour;
    private int pad_1_right_colour;
    private int pad_2_up_colour;
    private int pad_2_down_colour;
    private int pad_2_left_colour;
    private int pad_2_right_colour;










////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////



    // Setting the duration for one movement of the Cardbot
    //++++++++++++++++++++++
    int StepDuration = 2000;
    //++++++++++++++++++++++





    // Variable for measuring execution time of computer vision
    public static final DecimalFormat df1 = new DecimalFormat( "#.###" ); // format to have 3 decimals
    double start; // the start time
    double finish; // the end time
    double timeElapsed_s; // the time difference

    public static boolean computer_vision_completed = false;

    public static final int CAMERA_PERM_CODE = 101;
    public static final int CAMERA_REQUEST_CODE = 102;
    public static final int GALLERY_REQUEST_CODE_OLD = 105;

    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 99;








    // For the floating action button
    //----------------------------------
    FloatingActionButton fabMiddleTile, fabGoForward, fabGoBackward, fabTurnRight, fabTurnLeft, fabTurnBack, fabStartRepeat, fabEndRepeat, fabTwo, fabThree, fabFour, fabClearLast, fabClearAll;
    Float translationY = 100f;

    OvershootInterpolator interpolator = new OvershootInterpolator();

    Boolean isMenuOpen = false;

    TextView textViewDigitalTiles;

    //~~~~~~
    ArrayList<String> commands_digital_ArrayList = new ArrayList<String>();
    String[] commands_digital;

    Mat sequence_of_images_concat_digital; //= new Mat();
    List<Mat> sequence_of_image_list_digital = new ArrayList<>();

    Boolean DigitalTilesOn = false;
    //----------------------------------

    List<Mat> sequence_of_image_list = new ArrayList<>();

    Timer buttonTimer;








    private String resp;









    // Handler
    Handler handler1, handler2;

    // Runnable
    Runnable runnableSendCommands, runnableSendCommands2;

    // To count the iterations in the sent commands
    int finalI = 0;





    // For the drop down list to select example pictures of sequence of programming tiles
    private Spinner spinnerSequencePictureExamples;
    Button btnValidate;
    Bitmap selectedImage_Bitmap, selectedImage_Bitmap_Resized;
    TextView textViewLoadExample;








    Mat image;

    ImageView selectedImage;
    ImageView currentExecutedCommandImageView;
    // Uri imageUri;
    Bitmap imageBitmap, croppedBitmap, croppedBitmap_resized, sequence_of_images_concat_Bitmap, sequence_of_images_concat_Bitmap_resized, sequence_of_images_concat_Bitmap_digital; //, grayBitmap

    FrameLayout gamepadFrame;

    Button cameraBtn, galleryBtn, computerVisionBtn, sendCommandsBtn, stopBtn, debugBtn;
    String currentPhotoPath; // absolute path of the photo
    File imageFile; // the image file in which there will be our image from the camera or the gallery


    String[] commands;
    String[] interpreted_commands;
    ArrayList<ArrayList<Integer>> iteration_in_loop_final;
    int number_of_for_loops = 0;
    // initializing the ArrayList containing the displacements of the red square to focus on the current executed command in the sequence of tiles
    ArrayList<Integer> red_square_disp;


    private TextView textViewIdentifiedCommands;
    TextView textViewCurrentExecutedCommand;
    TextView textViewCurrentIterationInForLoops;
    TextView textViewNumberOfForLoops;

    TextView number1;
    TextView number2;
    TextView number3;

    //private ProgressBar spinner;

    Boolean OK_for_interpreting_the_commands;
    Boolean OK_for_red_square_disp;
    Boolean OK_for_sending_commands;


    // Initialization of the shapes templates matrices for the identification of the commands
    Mat two = null;
    Mat three = null;
    Mat four = null;
    Mat end_repeat = null;
    Mat go_backward = null;
    Mat go_forward = null;
    Mat start_repeat = null;
    Mat turn_back = null;
    Mat turn_left = null;
    Mat turn_right = null;
    // Initialization of the SMALL shapes templates matrices for displaying them
    Mat two_small = null;
    Mat three_small = null;
    Mat four_small = null;
    Mat end_repeat_small = null;
    Mat go_backward_small = null;
    Mat go_forward_small = null;
    Mat start_repeat_small = null;
    Mat turn_back_small = null;
    Mat turn_left_small = null;
    Mat turn_right_small = null;



    // Cf.: https://developer.android.com/guide/components/activities/activity-lifecycle
    static final String STATE_CURRENT_PHOTO_PATH = "currentPhotoPath";

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the user's current game state
        savedInstanceState.putString(STATE_CURRENT_PHOTO_PATH, currentPhotoPath);

        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }




    public void executeTilesIdentification() {





        // computer vision part not completed yet
        computer_vision_completed = false;


        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;



        int width = selectedImage.getWidth();




        //-----
        // Start the stopwatch / counter for measuring execution time of computer vision
        start = System.nanoTime();

        //-----------------
        Pair<String[], Mat> result = ComputerVision.TilesIdentification(image, two, three, four, end_repeat, go_backward, go_forward, start_repeat, turn_back, turn_left, turn_right); // "How to return multiple objects from a Java method?" --> cf.: https://stackoverflow.com/questions/457629/how-to-return-multiple-objects-from-a-java-method
        // Stop the stopwatch / counter
        finish = System.nanoTime();
        // Retrieving the outputs of ComputerVision algo
        commands = result.first;
        Mat cropped = result.second;
        //-----------------


        textViewIdentifiedCommands.setText(Arrays.toString(commands));

        //-----



    }



    public void interpretCommands(String[] commands) {

        //interpreted_commands = commands;
        OK_for_interpreting_the_commands = true;
        OK_for_red_square_disp = true;
        OK_for_sending_commands = true;


        List<String> sequence = Arrays.asList(commands);
        List<Integer> depth_level = new ArrayList<Integer>(Collections.nCopies(commands.length, 0)); // The higher the depth, the deeper in nested 'for loops' the current element is located






        //**********************************************************************
        // 1) Creating numbers_index_list
        ArrayList<Integer> numbers_index_list = new ArrayList<>();


        // Getting all occurrences of "two"
        ArrayList<Integer> allIndexes_two = computeIndexList(commands, "two");
        numbers_index_list.addAll(allIndexes_two);

        // Getting all occurrences of "three"
        ArrayList<Integer> allIndexes_three = computeIndexList(commands, "three");
        numbers_index_list.addAll(allIndexes_three);

        // Getting all occurrences of "four"
        ArrayList<Integer> allIndexes_four = computeIndexList(commands, "four");
        numbers_index_list.addAll(allIndexes_four);

        // Sorting the indices in numbers_index_list
        Collections.sort(numbers_index_list);
        //**********************************************************************





        //**********************************************************************
        // 2) Creating end_repeat_index_list
        ArrayList<Integer> end_repeat_index_list = computeIndexList(commands, "end_repeat");
        //**********************************************************************






        //**********************************************************************
        //// Simple tests to ensure that we have composed a correct sequence of commands

        // Creating start_repeat_index_list
        ArrayList<Integer> start_repeat_index_list = computeIndexList(commands, "start_repeat");

        // Checking that each digit is preceded by a 'start_repeat'
        List<Boolean> digit_preceded_by_start_repeat = new ArrayList<Boolean>(Arrays.asList(new Boolean[numbers_index_list.size()]));
        Collections.fill(digit_preceded_by_start_repeat, Boolean.TRUE);
        if (!commands[0].equals("two") & !commands[0].equals("three") & !commands[0].equals("four")) {
            for (int i = 0; i < numbers_index_list.size(); i++) {
                if (!commands[numbers_index_list.get(i) - 1].equals("start_repeat")) {
                    digit_preceded_by_start_repeat.set(i, false);
                }
            }
        }
        // Checking that each 'start_repeat' is followed by a digit
        List<Boolean> start_repeat_followed_by_digit = new ArrayList<Boolean>(Arrays.asList(new Boolean[start_repeat_index_list.size()]));
        Collections.fill(start_repeat_followed_by_digit, Boolean.TRUE);
        if (!commands[commands.length-1].equals("start_repeat")) {
            for (int i = 0; i < start_repeat_index_list.size(); i++) {
                if (!commands[start_repeat_index_list.get(i) + 1].equals("two") & !commands[start_repeat_index_list.get(i) + 1].equals("three") & !commands[start_repeat_index_list.get(i) + 1].equals("four")) {
                    start_repeat_followed_by_digit.set(i, false);
                }
            }
        }


        // If the sequence presents some "start_repeat", "end_repeat" and digits, they have necessarily to appear the same number of time (this is THE basic for having correct implementations of 'for loops')
        if ((numbers_index_list.size() != end_repeat_index_list.size()) | (numbers_index_list.size() != start_repeat_index_list.size()) | (end_repeat_index_list.size() != start_repeat_index_list.size())) {
            Toast.makeText(this, "ERROR: you need to have the same number of digit tiles, 'start of loop' tiles and 'end_of_loop' tiles!", Toast.LENGTH_SHORT).show();
            OK_for_interpreting_the_commands = false;
            OK_for_red_square_disp = false;
            OK_for_sending_commands = false;

        } else {






            // Basically skipping the whole following algorithm if we have no 'for loop' at all to interpret!
            if ((numbers_index_list.size() == 0) & (end_repeat_index_list.size() == 0) & (start_repeat_index_list.size() == 0)) {

                interpreted_commands = commands;
                //OK_for_red_square_disp = true;



                // In the other case, if we have some 'for loops' in our sequence, it means that we have to interpret them!
            } else {



                //// Further tests to ensure the correct implementation of the 'for loops'

                // ---> If we begin the sequence of commands by a digit or an 'end_repeat', throw an error!
                if (commands[0].equals("two") | commands[0].equals("three") | commands[0].equals("four") | commands[0].equals("end_repeat")) {
                    Toast.makeText(this, "ERROR: you can't begin a sequence of commands with neither with a 'digit' or a 'end of loop' tile!", Toast.LENGTH_SHORT).show();
                    OK_for_interpreting_the_commands = false;
                    OK_for_red_square_disp = false;
                    OK_for_sending_commands = false;
                }
                // ---> If an 'end_repeat' appears prior to a 'start_repeat', throw an error!
                else if (Collections.min(end_repeat_index_list) < Collections.min(start_repeat_index_list)) {
                    Toast.makeText(this, "ERROR: you can't place the first 'end repeat for loop' tile before a 'start for loop' tile!", Toast.LENGTH_SHORT).show();
                    OK_for_interpreting_the_commands = false;
                    OK_for_red_square_disp = false;
                    OK_for_sending_commands = false;
                }
                // ---> If we finish the sequence of commands by a 'start_repeat', throw an error!
                else if (commands[commands.length - 1].equals("start_repeat")) {
                    Toast.makeText(this, "ERROR: you can't finish a sequence of commands with a 'start for loop' tile!", Toast.LENGTH_SHORT).show();
                    OK_for_interpreting_the_commands = false;
                    OK_for_red_square_disp = false;
                    OK_for_sending_commands = false;
                }
                // ---> If, in the end, we do NOT have the same number of 'start_repeat' and 'end_repeat', throw an error!
                else if (start_repeat_index_list.size() != end_repeat_index_list.size()) {
                    Toast.makeText(this, "ERROR: you need to have the same number of 'start for loop' tile and 'end for loop' tile!", Toast.LENGTH_SHORT).show();
                    OK_for_interpreting_the_commands = false;
                    OK_for_red_square_disp = false;
                    OK_for_sending_commands = false;
                }
                // ---> If a digit is NOT preceded by a 'start_repeat', throw an error!
                else if (digit_preceded_by_start_repeat.contains(false)) {
                    Toast.makeText(this, "ERROR: each digit needs to be preceded by a 'start for loop' tile!", Toast.LENGTH_SHORT).show();
                    OK_for_interpreting_the_commands = false;
                    OK_for_red_square_disp = false;
                    OK_for_sending_commands = false;
                }
                // ---> If a 'start_repeat' is NOT followed by a digit, throw an error!
                else if (start_repeat_followed_by_digit.contains(false)) {
                    Toast.makeText(this, "ERROR: each 'start for loop' tile needs to be followed a digit!", Toast.LENGTH_SHORT).show();
                    OK_for_interpreting_the_commands = false;
                    OK_for_red_square_disp = false;
                    OK_for_sending_commands = false;
                }
                // ---> else (if all tests have been passed), we grant the access to the interpretation of the commands
                else {
                    OK_for_interpreting_the_commands = true;
                    OK_for_red_square_disp = true;
                    //OK_for_sending_commands = true;
                }

                //**********************************************************************





                if (OK_for_interpreting_the_commands) {


                    number_of_for_loops = numbers_index_list.size();


                    ArrayList<String> interpreted_commands_ArrayList = new ArrayList<String>();
                    for (int i = 0; i < commands.length; i++) {
                        interpreted_commands_ArrayList.add(commands[i]);
                    }


                    //// Initialization of the "matrix" containing the reference "iteration in loop" (aiming at indicating in which iteration of each 'for loops' the current executed command is situated)
                    ArrayList<ArrayList<Integer>> iteration_in_loop = new ArrayList<>(number_of_for_loops);
                    // Setup up the array of arrays
                    for (int i = 0; i < number_of_for_loops; i++) {
                        iteration_in_loop.add(new ArrayList());
                    }
                    // Filling up the array of arrays with '0'
                    ArrayList<Integer> row_of_zeros = new ArrayList<Integer>(Collections.nCopies(commands.length, 0));
                    for (int i = 0; i < number_of_for_loops; i++) {
                        iteration_in_loop.set(i, row_of_zeros);
                        ;
                    }


                    int count = 0;
                    int end_for_loop_temp;
                    int begin_for_loop_temp;
                    int loop_length;
                    int repetition;
                    //**********************************************************************
                    // 3) Main while loop
                    String[] interpreted_commands_array = new String[0];
                    while (!end_repeat_index_list.isEmpty()) {

                        int interpreted_commands_ArrayList_size_at_top_of_loop = interpreted_commands_ArrayList.size();

                        //---------------------
                        // Adjusting the size of current iteration_in_loop.get(count) (padding with '0's at the end)
                        while (iteration_in_loop.get(count).size() < interpreted_commands_ArrayList_size_at_top_of_loop) {
                            iteration_in_loop.get(count).add(0);
                        }
                        //---------------------

                        // Finding the end index of the current 'for loop'
                        end_for_loop_temp = Collections.min(end_repeat_index_list);
                        ArrayList<Integer> numbers_index_list_smaller_than_min_of_end_repeat_index_list = new ArrayList<>();
                        for (int i = 0; i < numbers_index_list.size(); i++) {
                            if (numbers_index_list.get(i) < Collections.min(end_repeat_index_list)) {
                                numbers_index_list_smaller_than_min_of_end_repeat_index_list.add(numbers_index_list.get(i));
                            }
                        }
                        // Finding the beginning index of the current 'for loop'
                        begin_for_loop_temp = Collections.max(numbers_index_list_smaller_than_min_of_end_repeat_index_list) + 1;
                        // Length of current 'for loop'
                        loop_length = end_for_loop_temp - begin_for_loop_temp;
                        // Determining the number of repetitions of the current 'for loop'
                        if (interpreted_commands_ArrayList.get(begin_for_loop_temp - 1).equals("two")) {
                            repetition = 2;
                        } else if (interpreted_commands_ArrayList.get(begin_for_loop_temp - 1).equals("three")) {
                            repetition = 3;
                        } else {
                            repetition = 4;
                        }
                        ArrayList<Integer> addition_depth_level = new ArrayList<>();
                        for (int i = 0; i < loop_length; i++) {
                            addition_depth_level.add(depth_level.get(begin_for_loop_temp + i) + 1);
                        }
                        ArrayList<String> total_commands_in_for_loop = new ArrayList<String>();
                        ArrayList<Integer> total_addition_depth_level = new ArrayList<>();
                        ArrayList<Integer> total_iteration_temp = new ArrayList<>(loop_length);
                        // /!\/!\/!\ This is the part where we replicate "repetition" times the whole content of the current 'for loop'
                        for (int i = 0; i < repetition; i++) {
                            total_commands_in_for_loop.addAll(interpreted_commands_ArrayList.subList(begin_for_loop_temp, end_for_loop_temp));
                            total_addition_depth_level.addAll(addition_depth_level);
                            ArrayList<Integer> vector_current_for_loop_iteration = new ArrayList<Integer>(loop_length);
                            for (int j = 0; j < loop_length; j++) {
                                vector_current_for_loop_iteration.add(i + 1);
                            }
                            total_iteration_temp.addAll(vector_current_for_loop_iteration);
                        }
                        // Replicating accordingly all the preceding rows of iteration_in_loop
                        if (count > 0) {
                            for (int j = 0; j < count; j++) {
                                ArrayList<Integer> total_values_of_iteration = new ArrayList<>();
                                for (int i = 0; i < repetition; i++) {
                                    // Computing what comes in the MIDDLE
                                    total_values_of_iteration.addAll(iteration_in_loop.get(j).subList(begin_for_loop_temp, end_for_loop_temp));
                                }
                                // Computing what comes BEFORE
                                ArrayList<Integer> first_part_of_iteration_in_loop = new ArrayList<>();
                                first_part_of_iteration_in_loop.addAll(iteration_in_loop.get(j).subList(0, begin_for_loop_temp - 2));
                                // Computing what comes AFTER
                                ArrayList<Integer> last_part_of_iteration_in_loop = new ArrayList<>();
                                last_part_of_iteration_in_loop.addAll(iteration_in_loop.get(j).subList(end_for_loop_temp + 1, iteration_in_loop.get(j).size()));

                                // Combining "BEFORE", "MIDDLE" and "AFTER" in a single new row
                                ArrayList<Integer> new_row = new ArrayList<Integer>();
                                new_row.addAll(first_part_of_iteration_in_loop);
                                new_row.addAll(total_values_of_iteration);
                                new_row.addAll(last_part_of_iteration_in_loop);

                                // Setting the new row in the corresponding current previous line of iteration_in_loop
                                iteration_in_loop.set(j, new_row);
                            }
                        }


                        // Replacing the previous "coded style" 'for loop' by the content of the deployed current 'for loops'
                        ArrayList<String> first_part_of_interpreted_commands_ArrayList = new ArrayList<String>();
                        first_part_of_interpreted_commands_ArrayList.addAll(interpreted_commands_ArrayList.subList(0, begin_for_loop_temp - 2));
                        ArrayList<String> last_part_of_interpreted_commands_ArrayList = new ArrayList<String>();
                        last_part_of_interpreted_commands_ArrayList.addAll(interpreted_commands_ArrayList.subList(end_for_loop_temp + 1, interpreted_commands_ArrayList.size()));

                        interpreted_commands_ArrayList = new ArrayList<String>();
                        interpreted_commands_ArrayList.addAll(first_part_of_interpreted_commands_ArrayList);
                        interpreted_commands_ArrayList.addAll(total_commands_in_for_loop);
                        interpreted_commands_ArrayList.addAll(last_part_of_interpreted_commands_ArrayList);

                        // Updating the depth_level list (tracking at which level of 'for loop' we are)
                        ArrayList<Integer> first_part_of_depth_level = new ArrayList<>();
                        first_part_of_depth_level.addAll(depth_level.subList(0, begin_for_loop_temp - 2));
                        ArrayList<Integer> last_part_of_depth_level = new ArrayList<>();
                        last_part_of_depth_level.addAll(depth_level.subList(end_for_loop_temp + 1, depth_level.size()));

                        depth_level = new ArrayList<Integer>();
                        depth_level.addAll(first_part_of_depth_level);
                        depth_level.addAll(total_addition_depth_level);
                        depth_level.addAll(last_part_of_depth_level);

                        // Updating the current row of the ArrayList of ArrayLists "iteration_in_loop"
                        ArrayList<Integer> first_part_of_iteration_in_loop = new ArrayList<>();
                        first_part_of_iteration_in_loop.addAll(iteration_in_loop.get(count).subList(0, begin_for_loop_temp - 2));
                        ArrayList<Integer> last_part_of_iteration_in_loop = new ArrayList<>();
                        // If we end with an "end_repeat", we have to do following manipulation ↴ (i.e. we add nothing to "last_part_of_iteration_in_loop")
                        if (end_for_loop_temp == (interpreted_commands_ArrayList_size_at_top_of_loop - 1)) {
                            last_part_of_iteration_in_loop.addAll(iteration_in_loop.get(count).subList(0, 0));
                            ;
                        } else {
                            last_part_of_iteration_in_loop.addAll(iteration_in_loop.get(count).subList(end_for_loop_temp + 1, interpreted_commands_ArrayList_size_at_top_of_loop)); // previous_length));
                        }

                        ArrayList<Integer> new_row = new ArrayList<Integer>();
                        new_row.addAll(first_part_of_iteration_in_loop);
                        new_row.addAll(total_iteration_temp);
                        new_row.addAll(last_part_of_iteration_in_loop);

                        iteration_in_loop.set(count, new_row);


                        // Creating an array interpreted_commands_array based on interpreted_commands_ArrayList
                        interpreted_commands_array = new String[interpreted_commands_ArrayList.size()];
                        for (int i = 0; i < interpreted_commands_ArrayList.size(); i++) {
                            interpreted_commands_array[i] = interpreted_commands_ArrayList.get(i);
                        }


                        //--------------------------------------------
                        // Adjusting numbers_index_list
                        numbers_index_list = new ArrayList<>();
                        allIndexes_two = computeIndexList(interpreted_commands_array, "two");
                        numbers_index_list.addAll(allIndexes_two);
                        allIndexes_three = computeIndexList(interpreted_commands_array, "three");
                        numbers_index_list.addAll(allIndexes_three);
                        allIndexes_four = computeIndexList(interpreted_commands_array, "four");
                        numbers_index_list.addAll(allIndexes_four);
                        Collections.sort(numbers_index_list);
                        //--------------------------------------------


                        //--------------------------------------------
                        // Adjusting end_repeat_index_list
                        //end_repeat_index_list = new ArrayList<>();
                        end_repeat_index_list = computeIndexList(interpreted_commands_array, "end_repeat");


                        count += 1;

                    }
                    //**********************************************************************


                    interpreted_commands = interpreted_commands_array;

                    textViewIdentifiedCommands.setText(Arrays.toString(interpreted_commands));


                    iteration_in_loop_final = iteration_in_loop;


                }




            }




            // Getting red_square_disp (the ArrayList containing the values of the locations of the red square across all the interpreted commands)
            //------------------------------------------------------------------------------------------
            //------------------------------------------------------------------------------------------

            if (OK_for_red_square_disp) {

                // Resetting red_square_disp for the current sequence to analyse
                red_square_disp = new ArrayList<>();
                // initializing the current index stating on which tile we are currently sitting
                int i = 0;
                // initializing the ArrayList containing the index of the start_repeat tiles
                ArrayList<Integer> start_repeat_idx_temp_list = new ArrayList<>();
                // initializing the ArrayList containing the repetitions of the 'for loops'
                ArrayList<Integer> repetitions_temp_list = new ArrayList<>();
                while (i < commands.length) {

                    if (commands[i].equals("go_forward") | commands[i].equals("go_backward") | commands[i].equals("turn_left") | commands[i].equals("turn_right") | commands[i].equals("turn_back")) {

                        red_square_disp.add(i); // jump forward of 1 hop
                        i += 1;

                    } else if (commands[i].equals("start_repeat")) {

                        start_repeat_idx_temp_list.add(i);

                        if (commands[i+1].equals("two")) {
                            repetitions_temp_list.add(2-1);
                        } else if (commands[i+1].equals("three")) {
                            repetitions_temp_list.add(3-1);
                        } else { // case where commands[i+1] == "four"
                            repetitions_temp_list.add(4-1);
                        }

                        i += 2;

                    } else { // case where commands[i] == "end_repeat"

                        if (repetitions_temp_list.get(repetitions_temp_list.size() - 1) != 0) {// (!start_repeat_idx_temp_list.isEmpty()) { // checking if the start_repeat_idx_temp_list is NOT empty --> in this case, we enter what follows directly here below:

                            int idx_beginning_of_last_for_loop = start_repeat_idx_temp_list.get(start_repeat_idx_temp_list.size() - 1) + 2;
                            //red_square_disp.add(idx_beginning_of_last_for_loop); // jump backward to the beginning the 'for loop'
                            repetitions_temp_list.set(repetitions_temp_list.size() - 1, repetitions_temp_list.get(repetitions_temp_list.size() - 1) - 1); // decreasing the value of the last element in repetitions_temp_list by '1'
                            i = idx_beginning_of_last_for_loop;

                        } else {
                            // Removing the last element of repetitions_temp_list
                            repetitions_temp_list.remove(repetitions_temp_list.size() - 1);
                            // Removing the last element of start_repeat_idx_temp_list
                            start_repeat_idx_temp_list.remove(start_repeat_idx_temp_list.size() - 1);
                            i += 1;
                        }

                    }



                }

                Log.d(Constants.TAG,"red_square_disp:"+ red_square_disp);

            }

            //------------------------------------------------------------------------------------------
            //------------------------------------------------------------------------------------------





        }




        Log.d(Constants.TAG,"interpreted_commands:"+ interpreted_commands);

    }


    public static ArrayList<Integer> computeIndexList(String[] commands, String command) {
        ArrayList<Integer> index_list = new ArrayList<>();
        for (int i = 0; i < commands.length; i++) {
            if (commands[i].equals(command)) {
                index_list.add(i);
            }
        }
        return index_list;
    }



    //@RequiresApi(api = Build.VERSION_CODES.Q)
    public void sendCommandsToMicroBit(final String[] interpreted_commands) throws InterruptedException {


        // Disabling the buttons to get a new sequence of tiles, the fab and the sendCommandsBtn during the execution of all the commands
        cameraBtn.setEnabled(false);
        galleryBtn.setEnabled(false);
        btnValidate.setEnabled(false);
        closeMenu();
        fabMiddleTile.setEnabled(false);
        sendCommandsBtn.setEnabled(false);
        debugBtn.setEnabled(false);
        debugBtn.setTextColor(Color.rgb(110,190,152));
        buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        cameraBtn.setEnabled(true);
                        galleryBtn.setEnabled(true);
                        btnValidate.setEnabled(true);
                        fabMiddleTile.setEnabled(true);
                        sendCommandsBtn.setEnabled(true);
                        debugBtn.setEnabled(true);
                        debugBtn.setTextColor(Color.rgb(15,129,74));
                    }
                });
            }
        }, StepDuration*(commands.length));



        //Toast.makeText(this, "The first command to send is: "+interpreted_commands[0], Toast.LENGTH_SHORT).show(); //visible part

        // Enabling the possibility to stop the robot
        stopBtn.setTextColor(Color.RED);
        stopBtn.setEnabled(true);

        // Cf.: https://stackoverflow.com/questions/20896245/how-to-delay-a-loop-in-android-without-using-thread-sleep
        handler1 = new Handler();




        // Cf.: https://stackoverflow.com/questions/20165274/interrupt-a-postdelayed-from-a-handler-inside-a-oncompletionlistener
        runnableSendCommands = new Runnable() {

            @Override
            public void run() {




                //------------------------------------------------------------------------------

                MicroBitEvent mb_event;
                short event_value = 0;
                Settings settings = Settings.getInstance();
                byte [] event_bytes = new byte[4];


                String current_interpreted_command = interpreted_commands[finalI];


                if (number_of_for_loops > 0) {
                    // Printing the number of for loops
                    String numberOfForLoopsForTextView = "Nb. of 'for loops': " + number_of_for_loops;
                    textViewNumberOfForLoops.setText(numberOfForLoopsForTextView);
                    // Printing the current iteration at which we locate in each for loop (to have an additional kind of feedback)
                    String current_iteration_in_each_for_loop = "";
                    for (int m=0; m<number_of_for_loops; m++) {
                        current_iteration_in_each_for_loop = current_iteration_in_each_for_loop + "f.l. " + Integer.toString(m+1) + ", it.: " + iteration_in_loop_final.get(m).get(finalI) + "\n";
                    }
                    textViewCurrentIterationInForLoops.setText(current_iteration_in_each_for_loop);
                }

                switch (current_interpreted_command) {
                    case "go_backward":
                        event_value = settings.getMes_dpad_2_button_down_on(); // This is the event for go_backward
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_go_backward_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: go backward");
                        break;

                    case "go_forward":
                        event_value = settings.getMes_dpad_2_button_up_on(); // This is the event for go_forward
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_go_forward_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: go forward");
                        break;

                    case "turn_back":
                        event_value = settings.getMes_dpad_1_button_down_on(); // This is the event for turn_back
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_turn_back_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: turn back");
                        break;

                    case "turn_left":
                        event_value = settings.getMes_dpad_1_button_left_on(); // This is the event for turn_left
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_turn_left_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: turn left");
                        break;

                    case "turn_right":
                        event_value = settings.getMes_dpad_1_button_right_on(); // This is the event for turn_right
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_turn_right_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: turn right");
                        break;

                    default:
                        Toast.makeText(FreeGameActivity.this, "Error! This composition of tiles is not possible! Your error might come from a badly designed 'for loop'...", Toast.LENGTH_SHORT).show(); //visible part

                }






                // Creating the concatenated image of the commands with the current command highlighted in a red square
                //------------------------------------------------------------------------------
                //------------------------------------------------------------------------------


                Mat sequence_of_images_concat = new Mat();
                sequence_of_image_list = new ArrayList<>(); // = Arrays.asList(img_left, img_right);

                // Looping through all the shapes of the sequence
                for (int j=0; j<commands.length; j++) {
                    String current_command = commands[j];
                    switch(current_command) {
                        case "two":
                            sequence_of_image_list.add(two_small);
                            break;

                        case "three":
                            sequence_of_image_list.add(three_small);
                            break;

                        case "four":
                            sequence_of_image_list.add(four_small);
                            break;

                        case "end_repeat":
                            sequence_of_image_list.add(end_repeat_small);
                            break;

                        case "go_backward":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat go_backward_red_border = setRedBorder(go_backward_small);
                                sequence_of_image_list.add(go_backward_red_border);
                            } else {
                                sequence_of_image_list.add(go_backward_small);
                            }
                            break;

                        case "go_forward":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat go_forward_red_border = setRedBorder(go_forward_small);
                                sequence_of_image_list.add(go_forward_red_border);
                            } else {
                                sequence_of_image_list.add(go_forward_small);
                            }
                            break;

                        case "start_repeat":
                            sequence_of_image_list.add(start_repeat_small);
                            break;

                        case "turn_back":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat turn_back_red_border = setRedBorder(turn_back_small);
                                sequence_of_image_list.add(turn_back_red_border);
                            } else {
                                sequence_of_image_list.add(turn_back_small);
                            }
                            break;

                        case "turn_left":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat turn_left_red_border = setRedBorder(turn_left_small);
                                sequence_of_image_list.add(turn_left_red_border);
                            } else {
                                sequence_of_image_list.add(turn_left_small);
                            }
                            break;

                        case "turn_right":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat turn_right_red_border = setRedBorder(turn_right_small);
                                sequence_of_image_list.add(turn_right_red_border);
                            } else {
                                sequence_of_image_list.add(turn_right_small);
                            }
                            break;

                        default:
                            Log.d(Constants.TAG,"Error: problem in the creation of the sequence of commands with highlighted current command");
                    }


                }
                Core.hconcat(sequence_of_image_list, sequence_of_images_concat);


                // Setting the current concatenated picture in the ImageView presenting the commands
                sequence_of_images_concat_Bitmap = Bitmap.createBitmap(sequence_of_images_concat.cols(), sequence_of_images_concat.rows(), Bitmap.Config.RGB_565); // Bitmap.Config.RGB_565
                Utils.matToBitmap(sequence_of_images_concat, sequence_of_images_concat_Bitmap);
                int width = selectedImage.getWidth();
                int height = (int) (sequence_of_images_concat.rows()*(double)width/sequence_of_images_concat.cols());
                sequence_of_images_concat_Bitmap_resized = Bitmap.createScaledBitmap(sequence_of_images_concat_Bitmap, width, height, true);
                selectedImage.setImageBitmap(sequence_of_images_concat_Bitmap_resized);


                //------------------------------------------------------------------------------
                //------------------------------------------------------------------------------






                // Sending the event to the micro:bit
                mb_event = new MicroBitEvent(settings.getMes_dpad_controller(), event_value);
                event_bytes = mb_event.getEventBytesForBle();
                Log.d(Constants.TAG,"Writing event bytes:"+ Utility.byteArrayAsHexString(event_bytes));
                // The line below is the very line that makes the Bit:Buggy Car move!!!
                bluetooth_le_adapter.writeCharacteristic(Utility.normaliseUUID(BleAdapterService.EVENTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.CLIENTEVENT_CHARACTERISTIC_UUID), event_bytes);



                //------------------------------------------------------------------------------


                finalI += 1;


            }
        };


        // Cf.: https://developer.android.com/reference/android/os/Handler
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            handler1.hasCallbacks(runnableSendCommands);
        }



        // Looping through all the interpreted commands
        //for (int i = 0; i < interpreted_commands.length & !stopExecutionNOW; i++) {

        int i = 0;
        finalI = 0;
        while (i < interpreted_commands.length) {


            //finalI = i;
            handler1.postDelayed(runnableSendCommands, StepDuration * i); // Delay of StepDuration[ms]




            i += 1;

        }

        // Resetting the numbering of the commands
        finalI = 0;





    }







    // Cf.: java.lang.OutOfMemoryError while taking picture from camera using Base64 in android, https://stackoverflow.com/questions/13471387/java-lang-outofmemoryerror-while-taking-picture-from-camera-using-base64-in-andr
    public static Bitmap decodeFile(File f,int WIDTH,int HIGHT){
        try {
            //Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //The new size we want to scale to
            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HIGHT;
            //Find the correct scale value. It should be the power of 2.
            int scale=1;
            while(o.outWidth/scale/2>=REQUIRED_WIDTH && o.outHeight/scale/2>=REQUIRED_HIGHT)
                scale*=2;

            //Decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }



    public String getPathFromURI(Uri contentUri)
    {
        try
        {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(contentUri, proj, null, null, null);
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        }
        catch (Exception e)
        {
            return contentUri.getPath();
        }
    }




    public static Mat setRedBorder(Mat img) {
        // This function adds a red border to an image by preserving its original dimensions

        // Declare the variables
        Mat dst = new Mat(); // declaring the destination image
        int top, bottom, left, right;
        int borderType = Core.BORDER_CONSTANT; // int borderType = Imgproc.BORDER_CONSTANT;

        // Initialize arguments for the filter
        top = (int) (0.1*img.rows()); bottom = top; // the value by which we multiply defines the size of the border
        left = (int) (0.1*img.cols()); right = left;

        // Setting the color to red (in RGB format)
        Scalar value = new Scalar(255,0,0);

        // Cf.: https://docs.opencv.org/3.4/dc/da3/tutorial_copyMakeBorder.html
        Core.copyMakeBorder(img, dst, top, bottom, left, right, borderType, value);// Imgproc.copyMakeBorder(img, dst, top, bottom, left, right, borderType, value);

        // Resizing the new image
        Mat dst_resized = new Mat();
        Size sz = new Size(img.cols(),img.width());
        Imgproc.resize(dst, dst_resized, sz);


        return dst_resized;

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




    private String getFileExt(Uri contentUri) {
        ContentResolver c = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(c.getType(contentUri));
    }


    private File createImageFile() throws IOException {
        Log.d("tag", "Entering createImageFile");



        File mediaStorageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

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
//                Uri photoURI = FileProvider.getUriForFile(this,
//                        "com.bluetooth.mwoolley.android.fileprovider",
//                        photoFile);
                Uri photoURI = FileProvider.getUriForFile(this,
                        "ch.epfl.mobots.capl_framework.fileprovider",
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
                //--------------


            }
        }
    }










    @Override
    protected void onStart() {
        super.onStart(); // super - we want the functionality of my parent class
        //Toast.makeText(this, "onStart Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Toast.makeText(this, "onResume Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onResume");
        if (MicroBit.getInstance().isMicrobit_connected()) {
            showMsg2(Utility.htmlColorGreen("Connected"));
        } else {
            showMsg2(Utility.htmlColorRed("Disconnected"));
        }
    }





    @Override
    protected void onPause() {
        super.onPause();
        //Toast.makeText(this, "onPause Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Toast.makeText(this, "onStop Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        //Toast.makeText(this, "onRestart Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onRestart");
    }







    public void stopExecutionOfCommands(View view) {

        handler1.removeCallbacks(runnableSendCommands);
        stopBtn.setTextColor(Color.rgb(255,190,201));

        // Re-enabling the possibility to send commands beforehand (not waiting the end of the timer that disables the sendCommandsBtn)
        sendCommandsBtn.setEnabled(true);
        // Re-enabling the possibility to debug the program
        debugBtn.setEnabled(true);
        debugBtn.setTextColor(Color.rgb(15,129,74));
        // Re-enabling the possibility to compose a program based on a picture
        cameraBtn.setEnabled(true);
        galleryBtn.setEnabled(true);
        btnValidate.setEnabled(true);
        // Re-enabling the possibility to digitally compose a program
        fabMiddleTile.setEnabled(true);

    }






    public void launchTilesProgramDebug(View view) {


        if (computer_vision_completed | DigitalTilesOn) {
            //Toast.makeText(FreeGameActivity.this, "Sending commands to micro:bit...", Toast.LENGTH_SHORT).show();

            // 1) interpret the commands
            interpretCommands(commands);
            // 2) send the commands to the micro:bit one by one! (This is the purpose of the "Debug" function of the FreeGameActivity)
            if ( (interpreted_commands != null) & OK_for_sending_commands ) {

                if (finalI < interpreted_commands.length) {
                    try {
                        sendCommandsToMicroBitOneByOne(interpreted_commands);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(FreeGameActivity.this, "You went through your entire program of tiles. If you press on 'Debug' again, you will run through your program from the beginning again", Toast.LENGTH_SHORT).show();
                    finalI = 0;

                    // Disabling the button debugBtn (during the time that the short toast is displayed)
                    debugBtn.setEnabled(false);
                    debugBtn.setTextColor(Color.rgb(110,190,152));
                    buttonTimer = new Timer();
                    buttonTimer.schedule(new TimerTask() {

                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {
                                    debugBtn.setEnabled(true);
                                    debugBtn.setTextColor(Color.rgb(15,129,74));
                                }
                            });
                        }
                    }, 2000);

                }

            }
        } else {
            Toast.makeText(FreeGameActivity.this, "You have to perform tiles recognition beforehand!", Toast.LENGTH_SHORT).show();
        }


    }





    public void sendCommandsToMicroBitOneByOne(final String[] interpreted_commands) throws InterruptedException {


        // Disabling the buttons to get a new sequence of tiles, the fab and the sendCommandsBtn during the execution of all the commands
        cameraBtn.setEnabled(false);
        galleryBtn.setEnabled(false);
        btnValidate.setEnabled(false);
        closeMenu();
        fabMiddleTile.setEnabled(false);
        sendCommandsBtn.setEnabled(false);
        debugBtn.setEnabled(false);
        debugBtn.setTextColor(Color.rgb(110,190,152));
        Timer buttonTimer = new Timer();
        buttonTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        cameraBtn.setEnabled(true);
                        galleryBtn.setEnabled(true);
                        btnValidate.setEnabled(true);
                        fabMiddleTile.setEnabled(true);
                        sendCommandsBtn.setEnabled(true);
                        debugBtn.setEnabled(true);
                        debugBtn.setTextColor(Color.rgb(15,129,74));
                    }
                });
            }
        }, StepDuration);


        handler2 = new Handler();


        runnableSendCommands2 = new Runnable() {

            @Override
            public void run() {




                //------------------------------------------------------------------------------

                MicroBitEvent mb_event;
                short event_value = 0;
                Settings settings = Settings.getInstance();
                byte [] event_bytes = new byte[4];


                String current_interpreted_command = interpreted_commands[finalI];


                if (number_of_for_loops > 0) {
                    // Printing the number of for loops
                    String numberOfForLoopsForTextView = "Nb. of 'for loops': " + number_of_for_loops;
                    textViewNumberOfForLoops.setText(numberOfForLoopsForTextView);
                    // Printing the current iteration at which we locate in each for loop (to have an additional kind of feedback)
                    String current_iteration_in_each_for_loop = "";
                    for (int m=0; m<number_of_for_loops; m++) {
                        current_iteration_in_each_for_loop = current_iteration_in_each_for_loop + "f.l. " + Integer.toString(m+1) + ", it.: " + iteration_in_loop_final.get(m).get(finalI) + "\n";
                    }
                    textViewCurrentIterationInForLoops.setText(current_iteration_in_each_for_loop);
                }

                switch(current_interpreted_command) {
                    case "go_backward":
                        event_value = settings.getMes_dpad_2_button_down_on(); // This is the event for go_backward
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_go_backward_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: go backward");
                        break;

                    case "go_forward":
                        event_value = settings.getMes_dpad_2_button_up_on(); // This is the event for go_forward
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_go_forward_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: go forward");
                        break;

                    case "turn_back":
                        event_value = settings.getMes_dpad_1_button_down_on(); // This is the event for turn_back
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_turn_back_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: turn back");
                        break;

                    case "turn_left":
                        event_value = settings.getMes_dpad_1_button_left_on(); // This is the event for turn_left
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_turn_left_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: turn left");
                        break;

                    case "turn_right":
                        event_value = settings.getMes_dpad_1_button_right_on(); // This is the event for turn_right
                        // Adjusting the ImageView and the TextView accordingly (to have feedback)
                        currentExecutedCommandImageView.setImageResource(R.drawable.tile_template_turn_right_red_square);
                        textViewCurrentExecutedCommand.setText("Current command: turn right");
                        break;

                    default:
                        Toast.makeText(FreeGameActivity.this, "Error! This composition of tiles is not possible! Your error might come from a badly designed 'for loop'...", Toast.LENGTH_SHORT).show(); //visible part

                }






                // Creating the concatenated image of the commands with the current command highlighted in a red square
                //------------------------------------------------------------------------------
                //------------------------------------------------------------------------------


                Mat sequence_of_images_concat = new Mat();
                sequence_of_image_list = new ArrayList<>(); // = Arrays.asList(img_left, img_right);

                // Looping through all the shapes of the sequence
                for (int j=0; j<commands.length; j++) {
                    String current_command = commands[j];
                    switch(current_command) {
                        case "two":
                            sequence_of_image_list.add(two_small);
                            break;

                        case "three":
                            sequence_of_image_list.add(three_small);
                            break;

                        case "four":
                            sequence_of_image_list.add(four_small);
                            break;

                        case "end_repeat":
                            sequence_of_image_list.add(end_repeat_small);
                            break;

                        case "go_backward":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat go_backward_red_border = setRedBorder(go_backward_small);
                                sequence_of_image_list.add(go_backward_red_border);
                            } else {
                                sequence_of_image_list.add(go_backward_small);
                            }
                            break;

                        case "go_forward":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat go_forward_red_border = setRedBorder(go_forward_small);
                                sequence_of_image_list.add(go_forward_red_border);
                            } else {
                                sequence_of_image_list.add(go_forward_small);
                            }
                            break;

                        case "start_repeat":
                            sequence_of_image_list.add(start_repeat_small);
                            break;

                        case "turn_back":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat turn_back_red_border = setRedBorder(turn_back_small);
                                sequence_of_image_list.add(turn_back_red_border);
                            } else {
                                sequence_of_image_list.add(turn_back_small);
                            }
                            break;

                        case "turn_left":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat turn_left_red_border = setRedBorder(turn_left_small);
                                sequence_of_image_list.add(turn_left_red_border);
                            } else {
                                sequence_of_image_list.add(turn_left_small);
                            }
                            break;

                        case "turn_right":
                            // Detecting if it is this shape that has to have a red border
                            if (j == red_square_disp.get(finalI)) {
                                Mat turn_right_red_border = setRedBorder(turn_right_small);
                                sequence_of_image_list.add(turn_right_red_border);
                            } else {
                                sequence_of_image_list.add(turn_right_small);
                            }
                            break;

                        default:
                            Log.d(Constants.TAG,"Error: problem in the creation of the sequence of commands with highlighted current command");
                    }


                }
                Core.hconcat(sequence_of_image_list, sequence_of_images_concat);


                // Setting the current concatenated picture in the ImageView presenting the commands
                sequence_of_images_concat_Bitmap = Bitmap.createBitmap(sequence_of_images_concat.cols(), sequence_of_images_concat.rows(), Bitmap.Config.RGB_565); // Bitmap.Config.RGB_565
                Utils.matToBitmap(sequence_of_images_concat, sequence_of_images_concat_Bitmap);
                int width = selectedImage.getWidth();
                int height = (int) (sequence_of_images_concat.rows()*(double)width/sequence_of_images_concat.cols());
                sequence_of_images_concat_Bitmap_resized = Bitmap.createScaledBitmap(sequence_of_images_concat_Bitmap, width, height, true);
                selectedImage.setImageBitmap(sequence_of_images_concat_Bitmap_resized);


                //------------------------------------------------------------------------------
                //------------------------------------------------------------------------------






                // Sending the event to the micro:bit
                mb_event = new MicroBitEvent(settings.getMes_dpad_controller(), event_value);
                event_bytes = mb_event.getEventBytesForBle();
                Log.d(Constants.TAG,"Writing event bytes:"+ Utility.byteArrayAsHexString(event_bytes));
                // The line below is the very line that makes the Bit:Buggy Car move!!!
                bluetooth_le_adapter.writeCharacteristic(Utility.normaliseUUID(BleAdapterService.EVENTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.CLIENTEVENT_CHARACTERISTIC_UUID), event_bytes);

                //------------------------------------------------------------------------------


                finalI += 1;


            }
        };


        handler2.postDelayed(runnableSendCommands2, 0); // Delay of 0[sec]








    }








    private class AsyncTaskRunnerWait extends AsyncTask<String, String, String> {

        //private String resp;
        ProgressDialog progressDialog;

        @Override
        protected String doInBackground(String... params) {
            publishProgress("Sleeping..."); // Calls onProgressUpdate()

            try {

                executeTilesIdentification();

                int time = Integer.parseInt(params[0])*1000;

                Thread.sleep(time);
                resp = "Slept for " + params[0] + " seconds";
            } catch (InterruptedException e) {
                e.printStackTrace();
                resp = e.getMessage();
            } catch (Exception e) {
                e.printStackTrace();
                resp = e.getMessage();
            }
            return resp;
        }


        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            progressDialog.dismiss();


            // If we got NO error at all (i.e. " resp.substring(0, 5) == "Slept" ") and we were able to obtain our "commands" String[], we can go to the rest of the process
            if (!resp.substring(0,13).equals("cv::Exception") & !resp.substring(0,13).equals("Attempt to re")) { // Handling OpenCV errors and "Attempt to read from null array"
            //if (resp.substring(0,5).equals("Slept") | resp.substring(0,5).equals("Only ")) {


                // Compute elapsed time for computer vision
                timeElapsed_s = (finish - start)/Math.pow(10,9); // [s] ou "/1000000000"
                textViewCurrentExecutedCommand.setText("C.v. exec. time: "+df1.format(timeElapsed_s)+"[s]");


                // computer vision now completed
                computer_vision_completed = true;

                // enabling the possibility to send the commands
                sendCommandsBtn.setEnabled(true);
                debugBtn.setEnabled(true);
                debugBtn.setTextColor(Color.rgb(15,129,74));


                // Changing the ImageView with the corresponding sequence made of the little tiles
                // templates --> this is a concatenation of these little images
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
                Mat sequence_of_images_concat = new Mat();
                sequence_of_image_list = new ArrayList<>(); // = Arrays.asList(img_left, img_right);
                // Looping through all the shapes of the sequence
                for (int j=0; j<commands.length; j++) {
                    String current_command = commands[j];
                    switch(current_command) {
                        case "two":
                            sequence_of_image_list.add(two_small);
                            break;

                        case "three":
                            sequence_of_image_list.add(three_small);
                            break;

                        case "four":
                            sequence_of_image_list.add(four_small);
                            break;

                        case "end_repeat":
                            sequence_of_image_list.add(end_repeat_small);
                            break;

                        case "go_backward":
                            sequence_of_image_list.add(go_backward_small);
                            break;

                        case "go_forward":
                            sequence_of_image_list.add(go_forward_small);
                            break;

                        case "start_repeat":
                            sequence_of_image_list.add(start_repeat_small);
                            break;

                        case "turn_back":
                            sequence_of_image_list.add(turn_back_small);
                            break;

                        case "turn_left":
                            sequence_of_image_list.add(turn_left_small);
                            break;

                        case "turn_right":
                            sequence_of_image_list.add(turn_right_small);
                            break;

                        default:
                            Log.d(Constants.TAG,"Error: problem in the creation of the sequence of commands");
                    }
                }
                Core.hconcat(sequence_of_image_list, sequence_of_images_concat);
                // Setting the current concatenated picture in the ImageView presenting the commands
                sequence_of_images_concat_Bitmap = Bitmap.createBitmap(sequence_of_images_concat.cols(), sequence_of_images_concat.rows(), Bitmap.Config.RGB_565); // Bitmap.Config.RGB_565
                Utils.matToBitmap(sequence_of_images_concat, sequence_of_images_concat_Bitmap);
                int width = selectedImage.getWidth();
                int height = (int) (sequence_of_images_concat.rows()*(double)width/sequence_of_images_concat.cols());
                sequence_of_images_concat_Bitmap_resized = Bitmap.createScaledBitmap(sequence_of_images_concat_Bitmap, width, height, true);
                selectedImage.setImageBitmap(sequence_of_images_concat_Bitmap_resized);
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~


            // Else in the case we met a problem during the computer vision algorithm
            } else {

                // computer vision NOT completed
                computer_vision_completed = false;

                // disabling the possibility to send the commands
                sendCommandsBtn.setEnabled(false);
                debugBtn.setEnabled(false);
                debugBtn.setTextColor(Color.rgb(110,190,152));

                // Displaying an error message
                Toast.makeText(FreeGameActivity.this, "‼️⚠️‼️ Tiles identification failed ‼️⚠️‼️\n\nError message:\n" + resp, Toast.LENGTH_LONG).show();

            }


        }


        @Override
        protected void onPreExecute() {
            progressDialog = ProgressDialog.show(FreeGameActivity.this,
                    "Computer Vision",
                    "Tiles identification might last a few seconds...");
        }


        @Override
        protected void onProgressUpdate(String... text) {
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }





////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////












    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            Log.d(Constants.TAG, "onServiceConnected");
            bluetooth_le_adapter = ((BleAdapterService.LocalBinder) service).getService();
            bluetooth_le_adapter.setActivityHandler(mMessageHandler);
            connectToDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetooth_le_adapter = null;
        }
    };


    private void connectToDevice() {
        showMsg2(Utility.htmlColorBlue("Connecting to micro:bit"));
        if (bluetooth_le_adapter.connect(MicroBit.getInstance().getMicrobit_address())) {
        } else {
            showMsg2(Utility.htmlColorRed("onConnect: failed to connect"));
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        setContentView(R.layout.activity_free_game);


        // Retrieving the boolean DebugModeOn from the DeviceListActivity
        DebugModeOn = getIntent().getExtras().getBoolean("MyBoolean");

        // Set the title of "Free Game" on the top action bar ("decoration bar")
        getSupportActionBar().setTitle(R.string.screen_title_free_game);


        // Init the Floating Action Menu (for the digital tiles feature)
        initFabMenu();



        // Listener for the drop down list to select examples of picture from within the app itself
        addListenerOnSpinnerItemSelection();
        // Listener for the button to validate the choice of the example picture
        addListenerOnValidateBtn();











        // Requesting the permission to write external storage if not accorded yet
        if (ContextCompat.checkSelfPermission(FreeGameActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(FreeGameActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
        }








        //------------------------------------
        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            currentPhotoPath = savedInstanceState.getString(STATE_CURRENT_PHOTO_PATH);
        }


        selectedImage = (ImageView) findViewById(R.id.displayImageView);


        cameraBtn = (Button) findViewById(R.id.btn_camera);
        galleryBtn = (Button) findViewById(R.id.btn_gallery);

        computerVisionBtn = (Button) findViewById(R.id.btn_computer_vision);
        computerVisionBtn.setEnabled(false);
        sendCommandsBtn = (Button) findViewById(R.id.btn_send_commands);
        sendCommandsBtn.setEnabled(false);
        stopBtn = (Button) findViewById(R.id.btn_stop);
        stopBtn.setTextColor(Color.rgb(255,190,201));
        stopBtn.setEnabled(false);
        debugBtn = (Button) findViewById(R.id.btn_debug);
        debugBtn.setEnabled(false);
        debugBtn.setTextColor(Color.rgb(110,190,152));



        // Setting the chalkboard se font to the main buttons
        Typeface type = Typeface.createFromAsset(getAssets(),"fonts/chalkboardseregular.ttf");

        cameraBtn.setTypeface(type);
        galleryBtn.setTypeface(type);
        computerVisionBtn.setTypeface(type);
        sendCommandsBtn.setTypeface(type);
        stopBtn.setTypeface(type);
        debugBtn.setTypeface(type);
        btnValidate.setTypeface(type);
        // Setting the chalkboard se font to the visible TextViews
        textViewCurrentExecutedCommand = (TextView) findViewById(R.id.current_executed_command_text_view);
        textViewCurrentIterationInForLoops = (TextView) findViewById(R.id.current_iteration_in_for_loops_text_view);
        textViewNumberOfForLoops = (TextView) findViewById(R.id.number_of_for_loops_text_view);
        textViewDigitalTiles = (TextView) findViewById(R.id.text_view_digital_tiles);
        textViewLoadExample = (TextView) findViewById(R.id.text_view_load_example);

        textViewCurrentExecutedCommand.setTypeface(type);
        textViewCurrentIterationInForLoops.setTypeface(type);
        textViewNumberOfForLoops.setTypeface(type);
        textViewDigitalTiles.setTypeface(type);
        textViewLoadExample.setTypeface(type);
        // Setting the chalkboard se font to the numbers presenting the steps
        number1 = (TextView) findViewById(R.id.number_1);
        number2 = (TextView) findViewById(R.id.number_2);
        number3 = (TextView) findViewById(R.id.number_3);
        number1.setTypeface(type);
        number2.setTypeface(type);
        number3.setTypeface(type);




        textViewIdentifiedCommands = (TextView) findViewById(R.id.text_view_identified_commands);
        textViewCurrentExecutedCommand = (TextView) findViewById(R.id.current_executed_command_text_view);
        textViewCurrentIterationInForLoops = (TextView) findViewById(R.id.current_iteration_in_for_loops_text_view);
        textViewNumberOfForLoops = (TextView) findViewById(R.id.number_of_for_loops_text_view);


        gamepadFrame = (FrameLayout) findViewById(R.id.gamepad_frame);

        currentExecutedCommandImageView = (ImageView) findViewById(R.id.current_executed_command_image_view);


        textViewIdentifiedCommands.setVisibility(View.VISIBLE);// textViewIdentifiedCommands.setText("");
        gamepadFrame.setVisibility(View.VISIBLE);



        OpenCVLoader.initDebug();


        // Getting the shapes templates matrices from the Drawable folder
        //*********************
        //*********************
        try {
            two = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_two);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            three = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_three);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            four = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_four);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            end_repeat = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_end_repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            go_backward = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_go_backward);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            go_forward = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_go_forward);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            start_repeat = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_start_repeat);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            turn_back = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_back);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            turn_left = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_left);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            turn_right = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_right);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*********************
        //*********************




        // Getting the SMALL version of the shapes templates matrices from the Drawable folder
        //*********************
        //*********************
        try {
            two_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_two_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            three_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_three_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            four_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_four_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            end_repeat_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_end_repeat_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            go_backward_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_go_backward_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            go_forward_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_go_forward_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            start_repeat_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_start_repeat_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            turn_back_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_back_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            turn_left_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_left_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //--------
        try {
            turn_right_small = Utils.loadResource(getApplicationContext(), R.drawable.tile_template_turn_right_small);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //*********************
        //*********************





        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FreeGameActivity.this, "cameraBtn is clicked", Toast.LENGTH_SHORT).show();
                askCameraPermission();

                // Resetting finalI to 0 (so that we can debug a new sequence from scratch)
                finalI = 0;

                // As soon as the user want to consider a new picture, the sendCommandsBtn is
                // disabled (since it will be re-activated only after the new tiles identification will be finished)
                computerVisionBtn.setEnabled(false);
                sendCommandsBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                stopBtn.setTextColor(Color.rgb(255,190,201));
                debugBtn.setEnabled(false);
                debugBtn.setTextColor(Color.rgb(110,190,152));

                // Clearing the feedback zone when going to take a picture
                textViewCurrentExecutedCommand.setText("");
                textViewNumberOfForLoops.setText("");
                textViewCurrentIterationInForLoops.setText("");
                currentExecutedCommandImageView.setImageDrawable(getResources().getDrawable(R.drawable.white));
                // And clearing the composition of virtual tiles
                handleFabClearAll();

            }
        });

        galleryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(FreeGameActivity.this, "galleryBtn is clicked", Toast.LENGTH_SHORT).show();
                Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(gallery, GALLERY_REQUEST_CODE_OLD);

                // Resetting finalI to 0 (so that we can debug a new sequence from scratch)
                finalI = 0;

                // As soon as the user want to consider a new picture, the sendCommandsBtn is
                // disabled (since it will be re-activated only after the new tiles identification will be finished)
                computerVisionBtn.setEnabled(false);
                sendCommandsBtn.setEnabled(false);
                stopBtn.setEnabled(false);
                stopBtn.setTextColor(Color.rgb(255,190,201));
                debugBtn.setEnabled(false);
                debugBtn.setTextColor(Color.rgb(110,190,152));

                // Clearing the feedback zone when going to the gallery
                textViewCurrentExecutedCommand.setText("");
                textViewNumberOfForLoops.setText("");
                textViewCurrentIterationInForLoops.setText("");
                currentExecutedCommandImageView.setImageDrawable(getResources().getDrawable(R.drawable.white));
                // And clearing the composition of virtual tiles
                handleFabClearAll();

            }
        });


        computerVisionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (selectedImage.getDrawable() != null) {
                    //Toast.makeText(FreeGameActivity.this, "Entering computer vision phase...", Toast.LENGTH_SHORT).show();


                    AsyncTaskRunnerWait runnerWait = new AsyncTaskRunnerWait();
                    String sleepTime = "0.1"; // 0.1[s]
                    runnerWait.execute(sleepTime);


                } else {
                    Toast.makeText(FreeGameActivity.this, "There is no tiles to identify!", Toast.LENGTH_SHORT).show();
                }



            }
        });


        sendCommandsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (computer_vision_completed | DigitalTilesOn) {
                    // 0) Displaying the commands if they come from the digital composition feature
                    textViewIdentifiedCommands.setText(Arrays.toString(commands));

                    //Toast.makeText(FreeGameActivity.this, "Sending commands to micro:bit...", Toast.LENGTH_SHORT).show();
                    // 1) interpret the commands
                    interpretCommands(commands);
                    // 2) send the commands to the micro:bit (and pause a little bit between each command)
                    if ( interpreted_commands != null & OK_for_sending_commands ) {
                        try {
                            sendCommandsToMicroBit(interpreted_commands);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }



                } else {
                    Toast.makeText(FreeGameActivity.this, "You have to perform tiles recognition beforehand!", Toast.LENGTH_SHORT).show();
                }

            }
        });





        //------------------------------------







        gamepad_mask = (ImageView) FreeGameActivity.this.findViewById(R.id.gamepad_mask);
        gamepad = (ImageView) FreeGameActivity.this.findViewById(R.id.gamepad);
        gamepad.setOnTouchListener(this);

        pad_1_up_colour = getResources().getColor(R.color.pad_1_up_colour);
        pad_1_down_colour = getResources().getColor(R.color.pad_1_down_colour);
        pad_1_left_colour = getResources().getColor(R.color.pad_1_left_colour);
        pad_1_right_colour = getResources().getColor(R.color.pad_1_right_colour);

        pad_2_up_colour = getResources().getColor(R.color.pad_2_up_colour);
        pad_2_down_colour = getResources().getColor(R.color.pad_2_down_colour);
        pad_2_left_colour = getResources().getColor(R.color.pad_2_left_colour);
        pad_2_right_colour = getResources().getColor(R.color.pad_2_right_colour);

        // Original lines
//        // read intent data
//        final Intent intent = getIntent();
//        MicroBit.getInstance().setConnection_status_listener(this);
//
//        // connect to the Bluetooth smart service
//        Intent gattServiceIntent = new Intent(this, BleAdapterService.class);
//        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
        // Adapted lines as in MenuActivity.java
        // read intent data
        final Intent intent = getIntent();
        MicroBit.getInstance().setMicrobit_name(intent.getStringExtra(EXTRA_NAME));
        MicroBit.getInstance().setMicrobit_address(intent.getStringExtra(EXTRA_ID));
        MicroBit.getInstance().setConnection_status_listener(this);

        // connect to the Bluetooth service
        Intent gattServiceIntent = new Intent(this, BleAdapterService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);

        vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        has_vibrator = vibrator.hasVibrator();
    }













    @Override
    protected void onDestroy() {
        Log.d(Constants.TAG, "onDestroy");
        //Toast.makeText(this, "onDestroy Finished", Toast.LENGTH_SHORT).show(); //visible part
        Log.i("tag","onDestroy");
        super.onDestroy();
        try {
            // may already have unbound. No API to check state so....
            unbindService(mServiceConnection);
        } catch (Exception e) {
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_free_game, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.free_game_action_info) {
            Intent intent = new Intent(FreeGameActivity.this, HelpActivity.class);
            intent.putExtra(Constants.URI, Constants.FREE_GAME_HELP);
            startActivity(intent);
            return true;
        }


        if (id == R.id.menu_menu_services) {
            Intent intent = new Intent(FreeGameActivity.this, ServicesPresentActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.menu_menu_refresh) {
            refreshBluetoothServices();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }




    private void refreshBluetoothServices() {
        if (MicroBit.getInstance().isMicrobit_connected()) {
            Toast toast = Toast.makeText(this, "Refreshing GATT services", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            MicroBit.getInstance().resetAttributeTables();
            bluetooth_le_adapter.refreshDeviceCache();
            bluetooth_le_adapter.discoverServices();
        } else {
            Toast toast = Toast.makeText(this, "Request Ignored - Not Connected", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }




    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        Log.d(Constants.TAG, "onActivityResult");
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == CAMERA_REQUEST_CODE) {

            if (resultCode == Activity.RESULT_OK) {


                // Cf.: java.lang.OutOfMemoryError while taking picture from camera using Base64 in android, https://stackoverflow.com/questions/13471387/java-lang-outofmemoryerror-while-taking-picture-from-camera-using-base64-in-andr
                //-----------
                File out = new File(currentPhotoPath); // = new File(Environment.getExternalStorageDirectory(), "newImage.jpg");
                if(!out.exists())
                {
                    Log.v("log", "file not found");
                    Toast.makeText(getBaseContext(),
                            "Error while capturing image", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                Log.v("log", "file "+out.getAbsolutePath());
                File f = new File(out.getAbsolutePath());

                try {
                    ExifInterface exif = new ExifInterface(out.getAbsolutePath());
                    int orientation_exif = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    //Toast.makeText(getApplicationContext(), ""+orientation, 1).show();
                    Log.v("log", "ort is "+orientation_exif);

                } catch (IOException e)
                {
                    e.printStackTrace();
                }
                imageBitmap = decodeFile(f,2592,1944);

                selectedImage.setImageBitmap(imageBitmap);
                //-----------



                // Pre-processing (for image coming from camera)
                //--------------------------------------
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inDither = false;
                o.inSampleSize = 4;
                // Bitmap to Mat
                image = new Mat();
                Utils.bitmapToMat(imageBitmap, image);

                imageBitmap = null;

                computerVisionBtn.setEnabled(true);
                textViewIdentifiedCommands.setText("Waiting for the user to click on 'Identify tiles' (this might take a few seconds to execute then)");

            }
        }






        if (requestCode == GALLERY_REQUEST_CODE_OLD) {
            if (resultCode == Activity.RESULT_OK) {
                Uri contentUri = data.getData();


                //----------- Cf.: https://gitlab.rd.tut.fi/manandhs/slidermenu/commit/b2ece7184039d4e07b840c8aaa6d65d36fd01624
                String imagePath = getPathFromURI(contentUri);
                Log.v("IMAGE PATH ========== ", imagePath);
                //-----------



                // Cf.: java.lang.OutOfMemoryError while taking picture from camera using Base64 in android, https://stackoverflow.com/questions/13471387/java-lang-outofmemoryerror-while-taking-picture-from-camera-using-base64-in-andr
                //-----------
                File out = new File(imagePath); // = new File(Environment.getExternalStorageDirectory(), "newImage.jpg");
                if(!out.exists())
                {
                    Log.v("log", "file not found");
                    Toast.makeText(getBaseContext(),
                            "Error while capturing image", Toast.LENGTH_LONG)
                            .show();
                    return;
                }
                Log.v("log", "file "+out.getAbsolutePath());
                File f = new File(out.getAbsolutePath());

                try {
                    ExifInterface exif = new ExifInterface(out.getAbsolutePath());
                    int orientation_exif = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    //Toast.makeText(getApplicationContext(), ""+orientation, 1).show();
                    Log.v("log", "ort is "+orientation_exif);

                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                imageBitmap = decodeFile(f,2592,1944);


                selectedImage.setImageBitmap(imageBitmap);

                //-----------

                // Pre-processing for image coming from gallery
                //--------------------------------------
                BitmapFactory.Options o = new BitmapFactory.Options();
                o.inDither = false;
                o.inSampleSize = 4;
                // Bitmap to Mat
                image = new Mat();
                Utils.bitmapToMat(imageBitmap, image);

                // Making the Bitmap take less place in memory
                imageBitmap = null;

                computerVisionBtn.setEnabled(true);
                textViewIdentifiedCommands.setText("Waiting for the user to click on 'Identify tiles' (this might take a few seconds to execute then)");

            }
        }

        //------------------------------------------------------------------------------------------




    }

    // Service message handler�//////////////////
    private Handler mMessageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle;
            String service_uuid = "";
            String characteristic_uuid = "";
            String descriptor_uuid = "";
            byte[] b = null;
            TextView value_text = null;

            switch (msg.what) {
                case BleAdapterService.GATT_CHARACTERISTIC_WRITTEN:
                    Log.d(Constants.TAG, "Handler received characteristic written result");
                    bundle = msg.getData();
                    service_uuid = bundle.getString(BleAdapterService.PARCEL_SERVICE_UUID);
                    characteristic_uuid = bundle.getString(BleAdapterService.PARCEL_CHARACTERISTIC_UUID);
                    Log.d(Constants.TAG, "characteristic " + characteristic_uuid + " of service " + service_uuid + " written OK");
                    break;
                case BleAdapterService.MESSAGE:
                    bundle = msg.getData();
                    String text = bundle.getString(BleAdapterService.PARCEL_TEXT);
                    showMsg(text);
                case BleAdapterService.GATT_CONNECTED:
                    showMsg2(Utility.htmlColorGreen("Connected"));
                    showMsg2(Utility.htmlColorGreen("Discovering services..."));
                    if (!DebugModeOn) {bluetooth_le_adapter.discoverServices();}
                    break;
                case BleAdapterService.GATT_DISCONNECT:
                    showMsg2(Utility.htmlColorRed("Disconnected"));
                    break;
                case BleAdapterService.GATT_SERVICES_DISCOVERED:
                    Log.d(Constants.TAG, "XXXX Services discovered");
                    showMsg2(Utility.htmlColorGreen("Ready"));
                    List<BluetoothGattService> slist = bluetooth_le_adapter.getSupportedGattServices();
                    for (BluetoothGattService svc : slist) {
                        Log.d(Constants.TAG, "UUID=" + svc.getUuid().toString().toUpperCase() + " INSTANCE=" + svc.getInstanceId());
                        MicroBit.getInstance().addService(svc);
                    }
                    MicroBit.getInstance().setMicrobit_services_discovered(true);
                    break;
            }
        }
    };

    private void showMsg(final String msg) {
        Log.d(Constants.TAG, msg);
        // was sometimes getting android.view.WindowManager$BadTokenException: Unable to add window. This is an attempt to avoid trying to show a dialog when not in a suitable state
        if (!FreeGameActivity.this.hasWindowFocus()) {
            Log.d(Constants.TAG, "Activity not ready yet");
            return;
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final AlertDialog.Builder builder = new AlertDialog.Builder(FreeGameActivity.this);
                builder.setTitle("");
                builder.setMessage(msg);
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
            }
        });
    }


    private void showMsg2(final String msg) {
        Log.d(Constants.TAG, msg);
        runOnUiThread(new Runnable() {
                          @Override
                          public void run() {
                              runOnUiThread(new Runnable() {
                                  @Override
                                  public void run() {
                                      ((TextView) FreeGameActivity.this.findViewById(R.id.text_view_identified_commands)).setText(Html.fromHtml(msg));
                                  }
                              });
                          }
                      }
        );
    }


    @Override
    public void connectionStatusChanged(boolean connected) {
//        if (!connected) {
//            showMsg("Disconnected");
//        }
        if (connected) {
            showMsg2(Utility.htmlColorGreen("Connected"));
        } else {
            showMsg2(Utility.htmlColorRed("Disconnected"));
        }
    }

    @Override
    public void serviceDiscoveryStatusChanged(boolean new_state) {
    }


    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!MicroBit.getInstance().isMicrobit_connected()) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                showMsg("Currently disconnected - go back and connect again");
            }
            return true;
        }
        Log.d(Constants.TAG, "onTouch - " + event.actionToString((event.getAction())));
        Log.d(Constants.TAG, "onTouch action - " + event.getAction());
        Log.d(Constants.TAG, "onTouch action masked - " + event.getActionMasked());
        int pointer_index = 0;
        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            pointer_index = event.getActionIndex();
        }

        final int evX = (int) event.getX(pointer_index);
        final int evY = (int) event.getY(pointer_index);

        int up_or_down=0; // 1 = down, 2 = up

        if (event.getAction() == MotionEvent.ACTION_DOWN || event.getAction() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {
            // get the colour of the region touched from the gamepad mask and use this to figure out which pad was pressed
            Log.d(Constants.TAG, "onTouch - determining pad touched at "+evX+","+evY);
            int touchColor = getHotspotColor (R.id.gamepad_mask, evX, evY);
            int tolerance = 25;
            int pad_no=-1;
            if (closeMatch(pad_1_up_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_UP_VIEW_INX;
            }
            if (closeMatch(pad_1_down_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_DOWN_VIEW_INX;
            }
            if (closeMatch(pad_1_left_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_LEFT_VIEW_INX;
            }
            if (closeMatch(pad_1_right_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX;
            }
            if (closeMatch(pad_2_up_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_UP_VIEW_INX;
            }
            if (closeMatch(pad_2_down_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_DOWN_VIEW_INX;
            }
            if (closeMatch(pad_2_left_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_LEFT_VIEW_INX;
            }
            if (closeMatch(pad_2_right_colour, touchColor, tolerance)) {
                pad_no = Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX;
            }
            Log.d(Constants.TAG, "Touched pad " + pad_no);
            if (pad_no > -1) {
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN ) {
                    up_or_down = Constants.PAD_DOWN;
                } else {
                    up_or_down = Constants.PAD_UP;
                }
                byte[] event_data = makeEvent(up_or_down, pad_no);
                if (event_data == null) {
                    return true;
                }
                Log.d(Constants.TAG,"Writing event bytes:"+ Utility.byteArrayAsHexString(event_data));
                // The line below is the very line that makes the Bit:Buggy Car move!!!
                bluetooth_le_adapter.writeCharacteristic(Utility.normaliseUUID(BleAdapterService.EVENTSERVICE_SERVICE_UUID), Utility.normaliseUUID(BleAdapterService.CLIENTEVENT_CHARACTERISTIC_UUID), event_data);
                if (event.getAction() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                    if (has_vibrator) {
                        vibrator.vibrate(250);
                    }
                }
                return true;
            }
        }
        return false;
    }

    private int getDtmfTone(int pad_no) {
        switch (pad_no) {
            case Constants.DPAD_1_BUTTON_UP_VIEW_INX: return ToneGenerator.TONE_DTMF_1;
            case Constants.DPAD_1_BUTTON_LEFT_VIEW_INX: return ToneGenerator.TONE_DTMF_2;
            case Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX: return ToneGenerator.TONE_DTMF_3;
            case Constants.DPAD_1_BUTTON_DOWN_VIEW_INX: return ToneGenerator.TONE_DTMF_A;
            case Constants.DPAD_2_BUTTON_UP_VIEW_INX: return ToneGenerator.TONE_DTMF_7;
            case Constants.DPAD_2_BUTTON_LEFT_VIEW_INX: return ToneGenerator.TONE_DTMF_8;
            case Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX: return ToneGenerator.TONE_DTMF_9;
            case Constants.DPAD_2_BUTTON_DOWN_VIEW_INX: return ToneGenerator.TONE_DTMF_A;
            default:
                Log.d(Constants.TAG,"Error: unrecognised pad no");
                return ToneGenerator.TONE_DTMF_1;
        }
    }


    // see https://blahti.wordpress.com/2012/06/26/images-with-clickable-areas/
    public int getHotspotColor (int hotspotId, int x, int y) {
        ImageView img = (ImageView) findViewById(hotspotId);
        img.setDrawingCacheEnabled(true);
        Bitmap hotspots = Bitmap.createBitmap(img.getDrawingCache());
        img.setDrawingCacheEnabled(false);
        if (x >= 0 && y >= 0 && x <= hotspots.getWidth() && y <= hotspots.getHeight()) {
            return hotspots.getPixel(x, y);
        } else {
            return 0;
        }
    }

    // see https://blahti.wordpress.com/2012/06/26/images-with-clickable-areas/
    public boolean closeMatch (int color1, int color2, int tolerance) {
        int red_diff = (int) Math.abs (Color.red(color1) - Color.red (color2));
        int green_diff = (int) Math.abs (Color.green(color1) - Color.green(color2));
        int blue_diff = (int) Math.abs (Color.blue(color1) - Color.blue(color2));
        if (red_diff > tolerance ) return false;
        if (green_diff > tolerance ) return false;
        if (blue_diff > tolerance ) return false;
        return true;
    }

    private byte[] makeEvent(int action, int pad_number) {
//        struct event {
//            uint16 event_type;
//            uint16 event_value;
//        };

        MicroBitEvent mb_event;
        short event_value;
        Settings settings = Settings.getInstance();

        byte [] event_bytes = new byte[4];
        if (action == Constants.PAD_DOWN) { // on
            switch (pad_number) {
                case Constants.DPAD_1_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_1_button_up_on();
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_LEFT_VIEW_INX: event_value = settings.getMes_dpad_1_button_left_on();   // This is the event for turn_left
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_1_button_right_on(); // This is the event for turn_right
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_1_button_down_on();   // This is the event for turn_back
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_1_BUTTON_DOWN_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_2_button_up_on();       // This is the event for go_forward
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_LEFT_VIEW_INX: event_value =settings.getMes_dpad_2_button_left_on();
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_2_button_right_on();
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_2_button_down_on();   // This is the event for go_backward
                    Log.d(Constants.TAG, "PAD_DOWN - DPAD_2_BUTTON_DOWN_VIEW_INX");
                    break;
                default:
                    Log.d(Constants.TAG,"Error: unrecognised touch event / view");
                    return null;
            }
        } else { // off
            switch (pad_number) {
                case Constants.DPAD_1_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_1_button_up_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_LEFT_VIEW_INX: event_value = settings.getMes_dpad_1_button_left_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_1_button_right_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_1_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_1_button_down_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_1_BUTTON_DOWN_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_UP_VIEW_INX: event_value = settings.getMes_dpad_2_button_up_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_UP_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_LEFT_VIEW_INX: event_value = settings.getMes_dpad_2_button_left_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_LEFT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_RIGHT_VIEW_INX: event_value = settings.getMes_dpad_2_button_right_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_RIGHT_VIEW_INX");
                    break;
                case Constants.DPAD_2_BUTTON_DOWN_VIEW_INX: event_value = settings.getMes_dpad_2_button_down_off();
                    Log.d(Constants.TAG, "PAD_UP - DPAD_2_BUTTON_DOWN_VIEW_INX");
                    break;
                default:
                    Log.d(Constants.TAG,"Error: unrecognised touch event / view");
                    return null;
            }
        }
        mb_event = new MicroBitEvent(settings.getMes_dpad_controller(),event_value);
        event_bytes = mb_event.getEventBytesForBle();
        return event_bytes;
    }












    private void initFabMenu() {
        fabMiddleTile = (FloatingActionButton) findViewById(R.id.fab_middle_tile);
        fabGoForward = (FloatingActionButton) findViewById(R.id.fab_go_forward);
        fabGoBackward = (FloatingActionButton) findViewById(R.id.fab_go_backward);
        fabTurnRight = (FloatingActionButton) findViewById(R.id.fab_turn_right);
        fabTurnLeft = (FloatingActionButton) findViewById(R.id.fab_turn_left);
        fabTurnBack = (FloatingActionButton) findViewById(R.id.fab_turn_back);
        fabStartRepeat = (FloatingActionButton) findViewById(R.id.fab_start_repeat);
        fabEndRepeat = (FloatingActionButton) findViewById(R.id.fab_end_repeat);
        fabTwo = (FloatingActionButton) findViewById(R.id.fab_two);
        fabThree = (FloatingActionButton) findViewById(R.id.fab_three);
        fabFour = (FloatingActionButton) findViewById(R.id.fab_four);
        fabClearLast = (FloatingActionButton) findViewById(R.id.fab_clear_last);
        fabClearAll = (FloatingActionButton) findViewById(R.id.fab_clear_all);

        fabGoForward.setAlpha(0f);
        fabGoBackward.setAlpha(0f);
        fabTurnRight.setAlpha(0f);
        fabTurnLeft.setAlpha(0f);
        fabTurnBack.setAlpha(0f);
        fabStartRepeat.setAlpha(0f);
        fabEndRepeat.setAlpha(0f);
        fabTwo.setAlpha(0f);
        fabThree.setAlpha(0f);
        fabFour.setAlpha(0f);
        fabClearLast.setAlpha(0f);
        fabClearAll.setAlpha(0f);

        fabGoForward.setTranslationY(translationY);
        fabGoBackward.setTranslationY(translationY);
        fabTurnRight.setTranslationY(translationY);
        fabTurnLeft.setTranslationY(translationY);
        fabTurnBack.setTranslationY(translationY);
        fabStartRepeat.setTranslationY(translationY);
        fabEndRepeat.setTranslationY(translationY);
        fabTwo.setTranslationY(translationY);
        fabThree.setTranslationY(translationY);
        fabFour.setTranslationY(translationY);
        fabClearLast.setTranslationY(translationY);
        fabClearAll.setTranslationY(translationY);

        fabMiddleTile.setOnClickListener(this);
        fabGoForward.setOnClickListener(this);
        fabGoBackward.setOnClickListener(this);
        fabTurnRight.setOnClickListener(this);
        fabTurnLeft.setOnClickListener(this);
        fabTurnBack.setOnClickListener(this);
        fabStartRepeat.setOnClickListener(this);
        fabEndRepeat.setOnClickListener(this);
        fabTwo.setOnClickListener(this);
        fabThree.setOnClickListener(this);
        fabFour.setOnClickListener(this);
        fabClearLast.setOnClickListener(this);
        fabClearAll.setOnClickListener(this);


    }



    private void openMenu() {
        isMenuOpen = !isMenuOpen;

        fabMiddleTile.animate().setInterpolator(interpolator).rotation(45f).setDuration(300).start();

        fabGoForward.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabGoBackward.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTurnRight.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTurnLeft.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTurnBack.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabStartRepeat.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabEndRepeat.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabFour.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabClearLast.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
        fabClearAll.animate().translationY(0f).alpha(1f).setInterpolator(interpolator).setDuration(300).start();
    }





    private void closeMenu() {
        isMenuOpen = !isMenuOpen;

        fabMiddleTile.animate().setInterpolator(interpolator).rotation(0f).setDuration(300).start();

        fabGoForward.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabGoBackward.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTurnRight.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTurnLeft.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTurnBack.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabStartRepeat.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabEndRepeat.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabTwo.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabThree.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabFour.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabClearLast.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
        fabClearAll.animate().translationY(translationY).alpha(0f).setInterpolator(interpolator).setDuration(300).start();
    }







    private void handleFabCommand(String digital_command, Mat mini_picture) {
        Log.i("tag", "handleFabGoForward");

        // Resetting finalI to 0 (so that we can debug a new sequence from scratch)
        finalI = 0;

        // 0)
        // turning on the digital tile composition feature
        DigitalTilesOn = true;
        // disabling the computer vision when using virtual tiles
        computerVisionBtn.setEnabled(false);
        // clearing the feedback zone when dealing with the FAB
        textViewCurrentExecutedCommand.setText("");
        textViewNumberOfForLoops.setText("");
        textViewCurrentIterationInForLoops.setText("");
        currentExecutedCommandImageView.setImageDrawable(getResources().getDrawable(R.drawable.white));
        // enabling the debug feature
        debugBtn.setEnabled(true);
        debugBtn.setTextColor(Color.rgb(15,129,74));

        // 1) Changing the list of strings (adding the new command), appending the new command to "commands_digital"
        // adding the requested command to the ArrayList of strings
        //if (commands.length == 0) {
        if (commands == null) {
            commands_digital_ArrayList.add(digital_command);
            commands_digital = new String[commands_digital_ArrayList.size()];
            // updating the commands_digital list of strings
            for (int i=0; i<commands_digital_ArrayList.size(); i++) {
                commands_digital[i] = commands_digital_ArrayList.get(i);
            }

            // 2) --> COMMAND Saving the list of strings into the usual "commands"
            // overwriting the existing "commands" list of strings
            commands = commands_digital;
        } else { // else we consider the already existing "commands" and add some command to it!
            commands_digital_ArrayList.add(digital_command);
            String[] commands_temp = new String[commands.length+1];
            for (int i=0; i < commands.length; i++) {
                commands_temp[i] = commands[i];
            }
            commands_temp[commands.length] = digital_command;
            commands = commands_temp;
        }

        // 3) --> SERIES OF PICTURE
        // Creating the new figure by concatenation
        // adding the small image to the list of digital images
        sequence_of_image_list.add(mini_picture);
        // concatenating the list of images into a single matrix
        sequence_of_images_concat_digital = new Mat();
        Core.hconcat(sequence_of_image_list, sequence_of_images_concat_digital);

        // Displaying the newly built figure in the ImageView "selectedImage"
        // setting the current concatenated picture in the ImageView presenting the commands
        sequence_of_images_concat_Bitmap_digital = Bitmap.createBitmap(sequence_of_images_concat_digital.cols(), sequence_of_images_concat_digital.rows(), Bitmap.Config.RGB_565); // Bitmap.Config.RGB_565
        Utils.matToBitmap(sequence_of_images_concat_digital, sequence_of_images_concat_Bitmap_digital);
        int width = selectedImage.getWidth();
        int height = (int) (sequence_of_images_concat_digital.rows()*(double)width/sequence_of_images_concat_digital.cols());
        sequence_of_images_concat_Bitmap_resized = Bitmap.createScaledBitmap(sequence_of_images_concat_Bitmap_digital, width, height, true);
        selectedImage.setImageBitmap(sequence_of_images_concat_Bitmap_resized);

        // Enabling the access to the "Identify tiles" feature makes no sense since the tiles
        // don't need to be identified using computer vision in this case
        // --> simply directly enable the access to "Send commands"
        sendCommandsBtn.setEnabled(true);

    }



    private void handleFabClearLast() {
        // We can execute this only if the Cardbot is NOT moving

        if (sequence_of_image_list.size() > 0) {
            // 0)
            // disabling the computer vision when using virtual tiles
            computerVisionBtn.setEnabled(false);
            // clearing the feedback zone when dealing with the FAB
            textViewCurrentExecutedCommand.setText("");
            textViewNumberOfForLoops.setText("");
            textViewCurrentIterationInForLoops.setText("");
            currentExecutedCommandImageView.setImageDrawable(getResources().getDrawable(R.drawable.white));
            // 1) --> COMMAND
            // Removing the last command in commands_digital_ArrayList
            if (commands_digital_ArrayList.size() > 0){
                commands_digital_ArrayList.remove(commands_digital_ArrayList.size() - 1);
                // Updating the list of strings "commands" accordingly
                commands_digital = new String[commands_digital_ArrayList.size()];
                // updating the commands_digital list of strings
                for (int i=0; i<commands_digital_ArrayList.size(); i++) {
                    commands_digital[i] = commands_digital_ArrayList.get(i);
                }
                // overwriting the existing "commands" list of strings
                commands = commands_digital;
            } else { // we manually remove the last element of "commands"
                if (commands.length > 0) {

                    String[] commands_temp = new String[commands.length-1];
                    for (int i=0; i < commands.length-1; i++) {
                        commands_temp[i] = commands[i];
                    }
                    commands = commands_temp;
                }

            }


            // 2) --> SERIES OF PICTURES
            // Removing the last element of the list containing the matrices with the commands shapes
            sequence_of_image_list.remove(sequence_of_image_list.size() - 1);
            // Recreating the picture presenting the sequence of digital tiles
            // concatenating the list of images into a single matrix
            sequence_of_images_concat_digital = new Mat();
            Core.hconcat(sequence_of_image_list, sequence_of_images_concat_digital);
            // Displaying the newly built figure in the ImageView "selectedImage"
            // setting the current concatenated picture in the ImageView presenting the commands
            if (commands.length > 0) {
                sequence_of_images_concat_Bitmap_digital = Bitmap.createBitmap(sequence_of_images_concat_digital.cols(), sequence_of_images_concat_digital.rows(), Bitmap.Config.RGB_565); // Bitmap.Config.RGB_565
                Utils.matToBitmap(sequence_of_images_concat_digital, sequence_of_images_concat_Bitmap_digital);
                int width = selectedImage.getWidth();
                int height = (int) (sequence_of_images_concat_digital.rows()*(double)width/sequence_of_images_concat_digital.cols());
                sequence_of_images_concat_Bitmap_resized = Bitmap.createScaledBitmap(sequence_of_images_concat_Bitmap_digital, width, height, true);
                selectedImage.setImageBitmap(sequence_of_images_concat_Bitmap_resized);
            } else { // In the case where there are no commands left at all
                selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.white));
                // 3) Disabling the access to "Send commands"
                sendCommandsBtn.setEnabled(false);
                // disabling the debug feature
                debugBtn.setEnabled(false);
                debugBtn.setTextColor(Color.rgb(110,190,152));
            }

        }



    }




    private void handleFabClearAll() {

        // We can execute this only if the Cardbot is NOT moving

        // 0)
        // disabling the computer vision when using virtual tiles
        computerVisionBtn.setEnabled(false);
        // clearing the feedback zone when dealing with the FAB
        textViewCurrentExecutedCommand.setText("");
        textViewNumberOfForLoops.setText("");
        textViewCurrentIterationInForLoops.setText("");
        currentExecutedCommandImageView.setImageDrawable(getResources().getDrawable(R.drawable.white));
        // 1) --> COMMAND
        // clearing all the commands one by one (going from the end)
        while (commands_digital_ArrayList.size() > 0) {
            commands_digital_ArrayList.remove(commands_digital_ArrayList.size() - 1);
            sequence_of_image_list.remove(sequence_of_image_list.size() - 1);
        }

        commands_digital = new String[commands_digital_ArrayList.size()];
        commands = commands_digital;

        // 2) --> SERIES OF PICTURES
        //sequence_of_image_list.clear();
        // Cf. while loop above
        // clearing all pictures
        selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.white));

        // 3) Disabling the access to "Send commands"
        sendCommandsBtn.setEnabled(false);
        // disabling the debug feature
        debugBtn.setEnabled(false);
        debugBtn.setTextColor(Color.rgb(110,190,152));


        //--------
        sequence_of_image_list.removeAll(sequence_of_image_list);
        //--------

    }




    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.fab_middle_tile:
                Log.i("tag", "onClick: fab middle tile");
                // Turning on the digital tile composition feature
                DigitalTilesOn = true;
                // Disabling the computer vision when using virtual tiles
                computerVisionBtn.setEnabled(false);
                if (isMenuOpen) {
                    closeMenu();
                } else {
                    openMenu();
                    // Clearing the feedback zone when opening the FAB
                    textViewCurrentExecutedCommand.setText("");
                    textViewNumberOfForLoops.setText("");
                    textViewCurrentIterationInForLoops.setText("");
                    currentExecutedCommandImageView.setImageDrawable(getResources().getDrawable(R.drawable.white));
                }
                break;
            case R.id.fab_go_forward:
                Log.i("tag", "onClick: fab go forward");
                handleFabCommand("go_forward", go_forward_small);
                break;
            case R.id.fab_go_backward:
                Log.i("tag", "onClick: fab go backward");
                handleFabCommand("go_backward", go_backward_small);
                break;
            case R.id.fab_turn_right:
                Log.i("tag", "onClick: fab turn right");
                handleFabCommand("turn_right", turn_right_small);
                break;
            case R.id.fab_turn_left:
                Log.i("tag", "onClick: fab turn left");
                handleFabCommand("turn_left", turn_left_small);
                break;
            case R.id.fab_turn_back:
                Log.i("tag", "onClick: fab turn back");
                handleFabCommand("turn_back", turn_back_small);
                break;
            case R.id.fab_start_repeat:
                Log.i("tag", "onClick: fab start repeat");
                handleFabCommand("start_repeat", start_repeat_small);
                break;
            case R.id.fab_end_repeat:
                Log.i("tag", "onClick: fab end repeat");
                handleFabCommand("end_repeat", end_repeat_small);
                break;
            case R.id.fab_two:
                Log.i("tag", "onClick: fab two");
                handleFabCommand("two", two_small);
                break;
            case R.id.fab_three:
                Log.i("tag", "onClick: fab three");
                handleFabCommand("three", three_small);
                break;
            case R.id.fab_four:
                Log.i("tag", "onClick: fab four");
                handleFabCommand("four", four_small);
                break;
            case R.id.fab_clear_last:
                Log.i("tag", "onClick: fab clear last");
                handleFabClearLast();
                break;
            case R.id.fab_clear_all:
                Log.i("tag", "onClick: fab clear all");
                handleFabClearAll();
                break;
        }

    }





















    // Listener for the drop down list to select example pictures of sequence of programming tiles
    public void addListenerOnSpinnerItemSelection(){
        spinnerSequencePictureExamples = (Spinner) findViewById(R.id.spinner_sequence_picture_examples);

        // Customize the look of the text in the spinner
        // Cf.: https://www.tutorialspoint.com/how-to-change-the-spinner-textsize-and-textcolor-in-android-app
        String[] Examples = new String[]{"Example 1\n  short sequence", "Example 2\n  long sequence", "Example 3\n  sequence with 1 ’for loop’", "Example 4\n  sequence with 2 separated ’for loops’", "Example 5\n  sequence with 1 ’for loop’ nested in another"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, R.layout.spinner_list_example_pictures, Examples);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_list_example_pictures);
        spinnerSequencePictureExamples.setAdapter(arrayAdapter);

        spinnerSequencePictureExamples.setOnItemSelectedListener(new CustomOnItemSelectedListenerForExamplePictureOfSequence());
    }





    // Get the selected dropdown list value
    public void addListenerOnValidateBtn() {

        spinnerSequencePictureExamples = (Spinner) findViewById(R.id.spinner_sequence_picture_examples);

        btnValidate = (Button) findViewById(R.id.btn_validate);

        btnValidate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Toast.makeText(FreeGameActivity.this, "OnClickListener : " + "\nSpinner value : " + spinnerSequencePictureExamples.getSelectedItem() + "\nId: " + spinnerSequencePictureExamples.getSelectedItemId(), Toast.LENGTH_SHORT).show();

                // Resetting the whole sequence of tiles
                handleFabClearAll();

                // Resetting finalI to 0 (so that we can debug a new sequence from scratch)
                finalI = 0;

                switch ((int) spinnerSequencePictureExamples.getSelectedItemId()) {
                    case 0:
                        selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.sequence_picture_example_1_short));
                        // "How to convert a Drawable to a Bitmap?", cf.: https://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
                        selectedImage_Bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.sequence_picture_example_1_short);
                        setExamplePicture();
                        break;
                    case 1:
                        selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.sequence_picture_example_2_longer_no_for_loop));
                        selectedImage_Bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.sequence_picture_example_2_longer_no_for_loop);
                        setExamplePicture();
                        break;
                    case 2:
                        selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.sequence_picture_example_3_one_for_loop));
                        selectedImage_Bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.sequence_picture_example_3_one_for_loop);
                        setExamplePicture();
                        break;
                    case 3:
                        selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.sequence_picture_example_4_two_separated_for_loops));
                        selectedImage_Bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.sequence_picture_example_4_two_separated_for_loops);
                        setExamplePicture();
                        break;
                    case 4:
                        selectedImage.setImageDrawable(getResources().getDrawable(R.drawable.sequence_picture_example_5_one_for_loop_nested_in_another));
                        selectedImage_Bitmap = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.sequence_picture_example_5_one_for_loop_nested_in_another);
                        setExamplePicture();
                        break;
                }


            }


        });

    }




    private void setExamplePicture() {

        // Scaling down the Bitmap
        selectedImage_Bitmap_Resized = Bitmap.createScaledBitmap(selectedImage_Bitmap, 2592, 1944, false);
        // Converting the Bitmap to Mat
        image = new Mat();
        Utils.bitmapToMat(selectedImage_Bitmap_Resized, image);

        // Making the Bitmap take less place in memory
        selectedImage_Bitmap = null;

        computerVisionBtn.setEnabled(true);

    }










}