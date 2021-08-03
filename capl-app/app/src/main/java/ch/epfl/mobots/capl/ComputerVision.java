package ch.epfl.mobots.capl;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.opencv.core.Core.extractChannel;


public class ComputerVision extends Application {


//    private static Context context;
//
//    public ComputerVision(Context context) {
//        this.context = context;
//    }

//    // Cf.: https://stackoverflow.com/questions/21818905/get-application-context-from-non-activity-singleton-class
//    private static ComputerVision mContext;
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mContext = this;
//    }
//    public static ComputerVision getContext() {
//        return mContext;
//    }







//    @SuppressLint("StaticFieldLeak")
//    private static Context context;
//    // Cf.: https://stackoverflow.com/questions/6214386/how-to-access-drawable-from-non-activity-class
//    public ComputerVision(Context context) {
//        ComputerVision.context = context;
//    }
//    //public final Drawable two = context.getResources().getDrawable(tile_template_two);







//    // Cf.: https://stackoverflow.com/questions/6214386/how-to-access-drawable-from-non-activity-class
//    private static Context context;
//
//    public ComputerVision(Context context) {
//        this.context = context;
//    }


    private static Random rng = new Random(12345);

    // The method getApplicationContext() is undefined (cf.: https://stackoverflow.com/questions/16678763/the-method-getapplicationcontext-is-undefined)
    //private static Context c;




    public static int TokensDetection(Mat image) {




        int nrows = image.rows(); // 1944
        int ncols = image.cols(); // 2592





        // 1) Grayscaling
        Mat img_gray = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.cvtColor(image, img_gray, Imgproc.COLOR_BGR2GRAY);


        // 2) Median blur
        Mat blur = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.medianBlur(img_gray, blur, 3);


        // 3) Threshold Otsu
        Mat thresh = new Mat(nrows, ncols, CvType.CV_8UC3);
        Imgproc.threshold(blur, thresh, 225, 250, Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);


        // 4) Morphology opening (to remove noise)
        // Python equivalence:
        //kernel = np.ones((3,3),np.uint8)
        //opening = cv2.morphologyEx(thresh, cv2.MORPH_OPEN, kernel, iterations = 2)
        Mat kernel = new Mat(new Size(9, 9), CvType.CV_8UC1, new Scalar(255));
        Mat opening = new Mat();
        Imgproc.morphologyEx(thresh, opening, Imgproc.MORPH_OPEN, kernel);
        int opening_iteration = 3;
        for (int i=0; i<opening_iteration; i++) {
            Imgproc.morphologyEx(opening, opening, Imgproc.MORPH_OPEN, kernel);
        }


        // 5) Sure background area
        // Python equivalence
        //sure_bg = cv2.dilate(opening,kernel,iterations=3)
        Mat sure_bg = new Mat();
        Imgproc.dilate(opening, sure_bg, kernel);
        int dilation_iteration = 3;
        for (int i=0; i<dilation_iteration; i++) {
            Imgproc.dilate(sure_bg, sure_bg, kernel);
        }


        // 6) Finding sure foreground area
        // Python equivalence
        //dist_transform = cv2.distanceTransform(opening,cv2.DIST_L2,5)
        //ret, sure_fg = cv2.threshold(dist_transform,0.7*dist_transform.max(),255,0)
        Mat dist_transform = new Mat();
        Imgproc.distanceTransform(opening, dist_transform, Imgproc.CV_DIST_L2, 5);

        Mat sure_fg = new Mat();
        Imgproc.threshold(dist_transform, sure_fg, 0.7*Core.minMaxLoc(dist_transform).maxVal, 255, 0);



        // 7) Finding the contours
        sure_fg.convertTo(sure_fg, CvType.CV_8UC1); // Converting the type of sure_fg in order for findContours to support it
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(sure_fg, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // Drawing all the contours
//        Mat all_contours_drawing = Mat.zeros(sure_fg.size(), CvType.CV_8UC3);
//        for (int i = 0; i < contours.size(); i++) {
//            Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
//            Imgproc.drawContours(all_contours_drawing, contours, i, color, 10, Core.LINE_8, hierarchy, 0, new Point());
//        }
//        if (showPictures == true) {
//            showResult(all_contours_drawing, "7) all_contours", (int) ncols/8, (int) nrows/8);
//        }



        // Retrieving the number of tokens
        int number_of_tokens;
        number_of_tokens = contours.size();




        return number_of_tokens;
    }




    // Pre-processing method to make sure we have a good rectangle (4 points well organized)
    // to begin to work with to do the rest of the TilesIdentification
    public static boolean preProcessingToCheckIfTilesIdentificationIsOK(Mat image) {
        boolean ok_for_tiles_identification = false;

        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□


        int nrows = image.rows(); // 1944
        int ncols = image.cols(); // 2592
        //showResult(image, "original image", 0, 0);

        // あ) Rectification
        //*******************************************************************************
        //*******************************************************************************
        //*******************************************************************************
        //System.out.println("\n*********");
        Log.d("tag", "\n*********");
        //System.out.println("あ) Rectification");
        Log.d("tag", "あ) Rectification");




        // Converting from BGR to RGB
        Mat img_rgb = new Mat(nrows, ncols, CvType.CV_8UC3);
        Imgproc.cvtColor(image, img_rgb, Imgproc.COLOR_BGR2RGB); // "Why does the opencv cvtColor modify image sizes?" (cf.: https://stackoverflow.com/questions/46366865/why-does-the-opencv-cvtcolor-modify-image-sizes)
        //showResult(img_rgb, "img_rgb", 0, 0);














        // A) Pre-processing to find main_contour
        ////////////////////////////////////////////////////////////////////////
        // ## Find contours
        // 1) Grayscale
        Mat img_gray = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.cvtColor(img_rgb, img_gray, Imgproc.COLOR_RGB2GRAY);
        //showResult(img_gray, "あ.A.1) img_gray", 0, 0);

        // 2) Threshold
        Mat blur = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.medianBlur(img_gray, blur, 5);
        //showResult(blur, "あ.A.2.a) blur", 0, 0);
        Mat thresh = new Mat(nrows, ncols, CvType.CV_8UC3);
        // For Test4.jpg
        //Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        // For Test8.jpg
        // /!\ For tests with thick black surrounding line
        // --> invert the threshold!! (at least in Java...)
        // --> "THRESH_BINARY_INV"
        // Cf. 1): https://answers.opencv.org/question/2885/findcontours-gives-me-the-border-of-the-image/
        //     2): https://stackoverflow.com/questions/26137051/opencv-threshold-and-invert-an-image
        Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY_INV+ Imgproc.THRESH_OTSU);
        //showResult(thresh, "あ.A.2.b) thresh", 0, 0);

        // 3) Median filter clears small details
        Mat med = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.medianBlur(thresh, med, 5);
        //showResult(thresh, "あ.A.3) med", 0, 0);

        // 4) Finding the contours
        // Cf.: https://docs.opencv.org/3.4/df/d0d/tutorial_find_contours.html
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(med, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // Drawing all the contours
        //Mat all_contours_drawing = Mat.zeros(med.size(), CvType.CV_8UC3);
        //for (int i = 0; i < contours.size(); i++) {
        //    Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
        //    Imgproc.drawContours(all_contours_drawing, contours, i, color, 1, Core.LINE_8, hierarchy, 0, new Point());
        //}
        //showResult(all_contours_drawing, "あ.A.4.a) all_contours", 0, 0);
        // Main contour
        // Cf.: https://stackoverflow.com/questions/38759925/how-to-find-largest-contour-in-java-opencv
        //Mat main_contour_drawing = Mat.zeros(med.size(), CvType.CV_8UC3);
        //----------------------------------------------------------------------
        // Finding the largest contour
        double maxVal_cnt1 = 0;
        int maxValIdx_cnt1 = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal_cnt1 < contourArea)
            {
                maxVal_cnt1 = contourArea;
                maxValIdx_cnt1 = contourIdx;
            }
        }
        // Finding the second largest contour
        double maxVal_cnt2 = 0;
        int maxValIdx_cnt2 = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal_cnt2 < contourArea && contourIdx!=maxValIdx_cnt1)
            {
                maxVal_cnt2 = contourArea;
                maxValIdx_cnt2 = contourIdx;
            }
        }
        //----------------------------------------------------------------------
        int maxValIdx = maxValIdx_cnt2;
        //Imgproc.drawContours(main_contour_drawing, contours, maxValIdx, new Scalar(0,255,0), 1);
        MatOfPoint cnt = contours.get(maxValIdx); // cnt is our main_contour
        //showResult(main_contour_drawing, "あ.A.4.b) main_contour", 0, 0);
        ////////////////////////////////////////////////////////////////////////














        // B) Working with goodFeaturesToTrack on Canny of the convex Hull
        ////////////////////////////////////////////////////////////////////////
        // 1) Finding the convex hull
        // Cf.: https://docs.opencv.org/3.4/d7/d1d/tutorial_hull.html
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(cnt, hull);
        List<MatOfPoint> hullList = new ArrayList<>();
        Point[] contourArray = cnt.toArray();
        Point[] hullPoints = new Point[hull.rows()];
        List<Integer> hullContourIdxList = hull.toList();
        for (int i = 0; i < hullContourIdxList.size(); i++) {
            hullPoints[i] = contourArray[hullContourIdxList.get(i)];
        }
        hullList.add(new MatOfPoint(hullPoints));
        // drawing the convex hull
        Mat hull_drawing = Mat.zeros(med.size(), CvType.CV_8UC3); // med.size = 640x480
        //Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
        //---/!\---
        Scalar color = new Scalar( 0, 0, 255 ); // Red (Cf.: https://stackoverflow.com/questions/29301903/how-to-specify-a-color-using-scalar-class)
        Imgproc.drawContours(hull_drawing, hullList, 0, color );
        //---/!\---
        //showResult(hull_drawing, "あ.B.1) hull", 0, 0);

        // 2) goodFeaturesToTrack with Canny of the hull
        Mat hull_drawing_1_channel = new Mat(nrows, ncols, CvType.CV_8UC1);
        // Cf.: https://stackoverflow.com/questions/52122152/java-open-cv-3-4-2-how-to-extract-different-channels-from-mat-object
        extractChannel(hull_drawing, hull_drawing_1_channel, 2); // Getting the 3rd channel, the red channel
        //showResult(hull_drawing_1_channel, "あ.B.2.a) hull_drawing_1_channel", 0, 0);
        Mat hull_canny = new Mat();
        Imgproc.Canny(hull_drawing_1_channel, hull_canny, 50, 50, 3, false);
        //showResult(hull_canny, "あ.B.2.b) hull_canny", 0, 0);

        /// Parameters for Shi-Tomasi algorithm
        int maxCorners = 4;
        MatOfPoint corners = new MatOfPoint();
        double qualityLevel = 0.2; // 0.2
        double minDistance = 50;   // 50

        /// Apply corner detection --> cf.: https://github.com/opencv/opencv/blob/master/samples/java/tutorial_code/TrackingMotion/good_features_to_track/GoodFeaturesToTrackDemo.java
        Imgproc.goodFeaturesToTrack(hull_canny, corners, maxCorners, qualityLevel, minDistance);

//        // Draw the corners detected
//        // Copy the source image
//        //Mat copy = img_gray.clone();
//        //System.out.println("Number of corners detected: " + corners.rows());
//        Log.d("tag", "Number of corners detected: " + corners.rows());
//        int[] cornersData = new int[(int) (corners.total() * corners.channels())];
//        corners.get(0, 0, cornersData);
//        int radius = 4;
//        for (int i = 0; i < corners.rows(); i++) {
//            Core.circle(copy, new Point(cornersData[i * 2], cornersData[i * 2 + 1]), 3, new Scalar( 255, 0, 0 ), 20 );
//        }
//
//        showResult(copy, "あ.B.2.c) corners", 0, 0);
        ////////////////////////////////////////////////////////////////////////








        // C) Performing the actual rectification
        ////////////////////////////////////////////////////////////////////////
        // 1) Sorting the corners and creating the src_mat (containing the 4 "sortedSourcePoints")
        // Sorting corners according to:
        // top-left, bot-left, bot-right, top-right

        int[] col1 = {(int) corners.get(0,0)[0], (int) corners.get(1,0)[0], (int) corners.get(2,0)[0], (int) corners.get(3,0)[0]};
        // (606, 594, 54, 42)
        int[] col2 = {(int) corners.get(0,0)[1], (int) corners.get(1,0)[1], (int) corners.get(2,0)[1], (int) corners.get(3,0)[1]};
        // (275, 195, 351, 260)

        // Calculating the difference col2 - col2
        int[] diff_col2_col1 = diffArray(col2, col1);
        //System.out.println("diff_col2_col1: " + diff_col2_col1[0] + " " + diff_col2_col1[1] + " " + diff_col2_col1[2] + " " + diff_col2_col1[3]);
        Log.d("tag", "diff_col2_col1: " + diff_col2_col1[0] + " " + diff_col2_col1[1] + " " + diff_col2_col1[2] + " " + diff_col2_col1[3]);
        // (-331, -399, 297, 218)

        int [] sum_col2_col1 = sumArray(col2, col1);
        //System.out.println("sum_col2_col1: " + sum_col2_col1[0] + " " + sum_col2_col1[1] + " " + sum_col2_col1[2] + " " + sum_col2_col1[3]);
        Log.d("tag", "sum_col2_col1: " + sum_col2_col1[0] + " " + sum_col2_col1[1] + " " + sum_col2_col1[2] + " " + sum_col2_col1[3]);
        // (881, 789, 405, 302)

        int top_left_index = indexOfSmallestNumberInArray(sum_col2_col1);
        //System.out.println("top_left_index: " + top_left_index); // 3
        Log.d("tag", "top_left_index: " + top_left_index);

        int bot_left_index = indexOfLargestNumberInArray(diff_col2_col1);
        //System.out.println("bot_left_index: " + bot_left_index); // 2
        Log.d("tag", "bot_left_index: " + bot_left_index);

        int bot_right_index = indexOfLargestNumberInArray(sum_col2_col1);
        //System.out.println("bot_right_index: " + bot_right_index); // 0
        Log.d("tag", "bot_right_index: " + bot_right_index);

        int top_right_index = indexOfSmallestNumberInArray(diff_col2_col1);
        //System.out.println("top_right_index: " + top_right_index); // 1
        Log.d("tag", "top_right_index: " + top_right_index);

        Point[] sortedSourcePoints = new Point[4];
        sortedSourcePoints[0] = new Point(corners.get(top_left_index,0)[0],corners.get(top_left_index,0)[1]);
        sortedSourcePoints[1] = new Point(corners.get(bot_left_index,0)[0],corners.get(bot_left_index,0)[1]);
        sortedSourcePoints[2] = new Point(corners.get(bot_right_index,0)[0],corners.get(bot_right_index,0)[1]);
        sortedSourcePoints[3] = new Point(corners.get(top_right_index,0)[0],corners.get(top_right_index,0)[1]);

        Mat src_mat = new Mat(4,1, CvType.CV_32FC2);
        src_mat.put(0,0,sortedSourcePoints[0].x,sortedSourcePoints[0].y,sortedSourcePoints[1].x,sortedSourcePoints[1].y,sortedSourcePoints[2].x,sortedSourcePoints[2].y,sortedSourcePoints[3].x,sortedSourcePoints[3].y);


        // 2) Computing dst_mat (containing the target points)
        // Calculate maximum height (maximal length of vertical edges) and width
        int[] diff_0_1 = diffPoint(sortedSourcePoints[0], sortedSourcePoints[1]);
        int[] diff_2_3 = diffPoint(sortedSourcePoints[2], sortedSourcePoints[3]);
        int height_rectified = Math.max(normMethod(diff_0_1), normMethod(diff_2_3));
        //System.out.println("height_cropped: " + height_rectified); // 91
        Log.d("tag", "height_rectified: " + height_rectified);
        int[] diff_1_2 = diffPoint(sortedSourcePoints[1], sortedSourcePoints[2]);
        int[] diff_3_0 = diffPoint(sortedSourcePoints[3], sortedSourcePoints[0]);
        int width_rectified = Math.max(normMethod(diff_1_2), normMethod(diff_3_0));
        //System.out.println("width_cropped: " + width_rectified); // 557
        Log.d("tag", "width_rectified: " + width_rectified);



        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□


        // Checking that we have roughly a good rectangle to do the following (i.e. roughly the same
        // width at the bottom and at the top and the same height on the right and on the left of
        // our expected sequence of tiles --> very little difference between these two quantities)
        int bottom_width = normMethod(diff_1_2);
        int top_width = normMethod(diff_3_0);
        int left_height = normMethod(diff_0_1);
        int right_height = normMethod(diff_2_3);
        if ( Math.abs(bottom_width - top_width) < 200 && Math.abs(left_height - right_height) < 200 ) {
            ok_for_tiles_identification = true;
        }

        return ok_for_tiles_identification;

    }





















    public static Pair<String[], Mat> TilesIdentification(Mat image, Mat two, Mat three, Mat four, Mat end_repeat, Mat go_backward, Mat go_forward, Mat start_repeat, Mat turn_back, Mat turn_left, Mat turn_right) {



        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□
        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□
        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□

        //Load native opencv library (in case using Java outside of the Android frame)
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);



        int nrows = image.rows(); // 1944
        int ncols = image.cols(); // 2592
        //showResult(image, "original image", 0, 0);

        // TODO: あ) Rectification
        //*******************************************************************************
        //*******************************************************************************
        //*******************************************************************************
        //System.out.println("\n*********");
        Log.d("tag", "\n*********");
        //System.out.println("あ) Rectification");
        Log.d("tag", "あ) Rectification");




        // Converting from BGR to RGB
        Mat img_rgb = new Mat(nrows, ncols, CvType.CV_8UC3);
        Imgproc.cvtColor(image, img_rgb, Imgproc.COLOR_BGR2RGB); // "Why does the opencv cvtColor modify image sizes?" (cf.: https://stackoverflow.com/questions/46366865/why-does-the-opencv-cvtcolor-modify-image-sizes)
        //showResult(img_rgb, "img_rgb", 0, 0);














        // todo: A) Pre-processing to find main_contour
        ////////////////////////////////////////////////////////////////////////
        // ## Find contours
        // 1) Grayscale
        Mat img_gray = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.cvtColor(img_rgb, img_gray, Imgproc.COLOR_RGB2GRAY);
        //showResult(img_gray, "あ.A.1) img_gray", 0, 0);

        // 2) Threshold
        Mat blur = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.medianBlur(img_gray, blur, 5);
        //showResult(blur, "あ.A.2.a) blur", 0, 0);
        Mat thresh = new Mat(nrows, ncols, CvType.CV_8UC3);
        // For Test4.jpg
        //Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
        // For Test8.jpg
        // /!\ For tests with thick black surrounding line
        // --> invert the threshold!! (at least in Java...)
        // --> "THRESH_BINARY_INV"
        // Cf. 1): https://answers.opencv.org/question/2885/findcontours-gives-me-the-border-of-the-image/
        //     2): https://stackoverflow.com/questions/26137051/opencv-threshold-and-invert-an-image
        Imgproc.threshold(blur, thresh, 0, 255, Imgproc.THRESH_BINARY_INV+ Imgproc.THRESH_OTSU);
        //showResult(thresh, "あ.A.2.b) thresh", 0, 0);

        // 3) Median filter clears small details
        Mat med = new Mat(nrows, ncols, CvType.CV_8UC1);
        Imgproc.medianBlur(thresh, med, 5);
        //showResult(thresh, "あ.A.3) med", 0, 0);

        // 4) Finding the contours
        // Cf.: https://docs.opencv.org/3.4/df/d0d/tutorial_find_contours.html
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(med, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // Drawing all the contours
        //Mat all_contours_drawing = Mat.zeros(med.size(), CvType.CV_8UC3);
        //for (int i = 0; i < contours.size(); i++) {
        //    Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
        //    Imgproc.drawContours(all_contours_drawing, contours, i, color, 1, Core.LINE_8, hierarchy, 0, new Point());
        //}
        //showResult(all_contours_drawing, "あ.A.4.a) all_contours", 0, 0);
        // Main contour
        // Cf.: https://stackoverflow.com/questions/38759925/how-to-find-largest-contour-in-java-opencv
        //Mat main_contour_drawing = Mat.zeros(med.size(), CvType.CV_8UC3);
        //----------------------------------------------------------------------
        // Finding the largest contour
        double maxVal_cnt1 = 0;
        int maxValIdx_cnt1 = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal_cnt1 < contourArea)
            {
                maxVal_cnt1 = contourArea;
                maxValIdx_cnt1 = contourIdx;
            }
        }
        // Finding the second largest contour
        double maxVal_cnt2 = 0;
        int maxValIdx_cnt2 = 0;
        for (int contourIdx = 0; contourIdx < contours.size(); contourIdx++)
        {
            double contourArea = Imgproc.contourArea(contours.get(contourIdx));
            if (maxVal_cnt2 < contourArea && contourIdx!=maxValIdx_cnt1)
            {
                maxVal_cnt2 = contourArea;
                maxValIdx_cnt2 = contourIdx;
            }
        }
        //----------------------------------------------------------------------
        int maxValIdx = maxValIdx_cnt2;
        //Imgproc.drawContours(main_contour_drawing, contours, maxValIdx, new Scalar(0,255,0), 1);
        MatOfPoint cnt = contours.get(maxValIdx); // cnt is our main_contour
        //showResult(main_contour_drawing, "あ.A.4.b) main_contour", 0, 0);
        ////////////////////////////////////////////////////////////////////////














        // todo: B) Working with goodFeaturesToTrack on Canny of the convex Hull
        ////////////////////////////////////////////////////////////////////////
        // 1) Finding the convex hull
        // Cf.: https://docs.opencv.org/3.4/d7/d1d/tutorial_hull.html
        MatOfInt hull = new MatOfInt();
        Imgproc.convexHull(cnt, hull);
        List<MatOfPoint> hullList = new ArrayList<>();
        Point[] contourArray = cnt.toArray();
        Point[] hullPoints = new Point[hull.rows()];
        List<Integer> hullContourIdxList = hull.toList();
        for (int i = 0; i < hullContourIdxList.size(); i++) {
            hullPoints[i] = contourArray[hullContourIdxList.get(i)];
        }
        hullList.add(new MatOfPoint(hullPoints));
        // drawing the convex hull
        Mat hull_drawing = Mat.zeros(med.size(), CvType.CV_8UC3); // med.size = 640x480
        //Scalar color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
        //---/!\---
        Scalar color = new Scalar( 0, 0, 255 ); // Red (Cf.: https://stackoverflow.com/questions/29301903/how-to-specify-a-color-using-scalar-class)
        Imgproc.drawContours(hull_drawing, hullList, 0, color );
        //---/!\---
        //showResult(hull_drawing, "あ.B.1) hull", 0, 0);

        // 2) goodFeaturesToTrack with Canny of the hull
        Mat hull_drawing_1_channel = new Mat(nrows, ncols, CvType.CV_8UC1);
        // Cf.: https://stackoverflow.com/questions/52122152/java-open-cv-3-4-2-how-to-extract-different-channels-from-mat-object
        extractChannel(hull_drawing, hull_drawing_1_channel, 2); // Getting the 3rd channel, the red channel
        //showResult(hull_drawing_1_channel, "あ.B.2.a) hull_drawing_1_channel", 0, 0);
        Mat hull_canny = new Mat();
        Imgproc.Canny(hull_drawing_1_channel, hull_canny, 50, 50, 3, false);
        //showResult(hull_canny, "あ.B.2.b) hull_canny", 0, 0);

        /// Parameters for Shi-Tomasi algorithm
        int maxCorners = 4;
        MatOfPoint corners = new MatOfPoint();
        double qualityLevel = 0.2; // 0.2
        double minDistance = 50;   // 50

        /// Apply corner detection --> cf.: https://github.com/opencv/opencv/blob/master/samples/java/tutorial_code/TrackingMotion/good_features_to_track/GoodFeaturesToTrackDemo.java
        Imgproc.goodFeaturesToTrack(hull_canny, corners, maxCorners, qualityLevel, minDistance);

//        // Draw the corners detected
//        // Copy the source image
//        //Mat copy = img_gray.clone();
//        //System.out.println("Number of corners detected: " + corners.rows());
//        Log.d("tag", "Number of corners detected: " + corners.rows());
//        int[] cornersData = new int[(int) (corners.total() * corners.channels())];
//        corners.get(0, 0, cornersData);
//        int radius = 4;
//        for (int i = 0; i < corners.rows(); i++) {
//            Core.circle(copy, new Point(cornersData[i * 2], cornersData[i * 2 + 1]), 3, new Scalar( 255, 0, 0 ), 20 );
//        }
//
//        showResult(copy, "あ.B.2.c) corners", 0, 0);
        ////////////////////////////////////////////////////////////////////////















        // todo: C) Performing the actual rectification
        ////////////////////////////////////////////////////////////////////////
        // 1) Sorting the corners and creating the src_mat (containing the 4 "sortedSourcePoints")
        // Sorting corners according to:
        // top-left, bot-left, bot-right, top-right

        int[] col1 = {(int) corners.get(0,0)[0], (int) corners.get(1,0)[0], (int) corners.get(2,0)[0], (int) corners.get(3,0)[0]};
        // (606, 594, 54, 42)
        int[] col2 = {(int) corners.get(0,0)[1], (int) corners.get(1,0)[1], (int) corners.get(2,0)[1], (int) corners.get(3,0)[1]};
        // (275, 195, 351, 260)

        // Calculating the difference col2 - col2
        int[] diff_col2_col1 = diffArray(col2, col1);
        //System.out.println("diff_col2_col1: " + diff_col2_col1[0] + " " + diff_col2_col1[1] + " " + diff_col2_col1[2] + " " + diff_col2_col1[3]);
        Log.d("tag", "diff_col2_col1: " + diff_col2_col1[0] + " " + diff_col2_col1[1] + " " + diff_col2_col1[2] + " " + diff_col2_col1[3]);
        // (-331, -399, 297, 218)

        int [] sum_col2_col1 = sumArray(col2, col1);
        //System.out.println("sum_col2_col1: " + sum_col2_col1[0] + " " + sum_col2_col1[1] + " " + sum_col2_col1[2] + " " + sum_col2_col1[3]);
        Log.d("tag", "sum_col2_col1: " + sum_col2_col1[0] + " " + sum_col2_col1[1] + " " + sum_col2_col1[2] + " " + sum_col2_col1[3]);
        // (881, 789, 405, 302)

        int top_left_index = indexOfSmallestNumberInArray(sum_col2_col1);
        //System.out.println("top_left_index: " + top_left_index); // 3
        Log.d("tag", "top_left_index: " + top_left_index);

        int bot_left_index = indexOfLargestNumberInArray(diff_col2_col1);
        //System.out.println("bot_left_index: " + bot_left_index); // 2
        Log.d("tag", "bot_left_index: " + bot_left_index);

        int bot_right_index = indexOfLargestNumberInArray(sum_col2_col1);
        //System.out.println("bot_right_index: " + bot_right_index); // 0
        Log.d("tag", "bot_right_index: " + bot_right_index);

        int top_right_index = indexOfSmallestNumberInArray(diff_col2_col1);
        //System.out.println("top_right_index: " + top_right_index); // 1
        Log.d("tag", "top_right_index: " + top_right_index);

        Point[] sortedSourcePoints = new Point[4];
        sortedSourcePoints[0] = new Point(corners.get(top_left_index,0)[0],corners.get(top_left_index,0)[1]);
        sortedSourcePoints[1] = new Point(corners.get(bot_left_index,0)[0],corners.get(bot_left_index,0)[1]);
        sortedSourcePoints[2] = new Point(corners.get(bot_right_index,0)[0],corners.get(bot_right_index,0)[1]);
        sortedSourcePoints[3] = new Point(corners.get(top_right_index,0)[0],corners.get(top_right_index,0)[1]);

        Mat src_mat = new Mat(4,1, CvType.CV_32FC2);
        src_mat.put(0,0,sortedSourcePoints[0].x,sortedSourcePoints[0].y,sortedSourcePoints[1].x,sortedSourcePoints[1].y,sortedSourcePoints[2].x,sortedSourcePoints[2].y,sortedSourcePoints[3].x,sortedSourcePoints[3].y);


        // 2) Computing dst_mat (containing the target points)
        // Calculate maximum height (maximal length of vertical edges) and width
        int[] diff_0_1 = diffPoint(sortedSourcePoints[0], sortedSourcePoints[1]);
        int[] diff_2_3 = diffPoint(sortedSourcePoints[2], sortedSourcePoints[3]);
        int height_rectified = Math.max(normMethod(diff_0_1), normMethod(diff_2_3));
        //System.out.println("height_cropped: " + height_rectified); // 91
        Log.d("tag", "height_rectified: " + height_rectified);
        int[] diff_1_2 = diffPoint(sortedSourcePoints[1], sortedSourcePoints[2]);
        int[] diff_3_0 = diffPoint(sortedSourcePoints[3], sortedSourcePoints[0]);
        int width_rectified = Math.max(normMethod(diff_1_2), normMethod(diff_3_0));
        //System.out.println("width_cropped: " + width_rectified); // 557
        Log.d("tag", "width_rectified: " + width_rectified);

        // Calculating the target points
        Mat dst_mat = new Mat(4,1, CvType.CV_32FC2);
        dst_mat.put(0,0,0,0, 0,height_rectified, width_rectified,height_rectified, width_rectified,0);

        Mat perspectiveTransform = Imgproc.getPerspectiveTransform(src_mat, dst_mat);
        Mat rectified = image.clone();
        Imgproc.warpPerspective(image, rectified, perspectiveTransform, new Size(width_rectified,height_rectified));
        //showResult(rectified, "あ.C) rectified", (int) width_rectified/3, (int) height_rectified/3);
        //System.out.println("rectified size --> rows: " + rectified.rows() + " cols: " + rectified.cols()); // 392, 2288
        Log.d("tag", "rectified size --> rows: " + rectified.rows() + " cols: " + rectified.cols());
        //System.out.println("rectified Mat:");
        //Log.d("tag", "rectified Mat:");
        //System.out.println(rectified.dump()); // "make IDEA keep everything in the Output window" --> cf.: https://stackoverflow.com/questions/11763996/output-window-of-intellij-idea-cuts-output
        //Log.d("tag", rectified.dump());

        ////////////////////////////////////////////////////////////////////////





        //*******************************************************************************
        //*******************************************************************************
        //*******************************************************************************






















        // TODO: う) Cropping and Template Matching
        //*******************************************************************************
        //*******************************************************************************
        //*******************************************************************************
        //System.out.println("\n*********");
        Log.d("tag", "\n*********");
        //System.out.println("う) Cropping and Template Matching");
        Log.d("tag", "う) Cropping and Template Matching");





        // todo: A) Setting up the dimensions
        ////////////////////////////////////////////////////////////////////////

        int h = rectified.rows(); // 392
        int W = rectified.cols(); // 2288

        // Real dimensions of the tiles (in [mm])
        //***********************
        float one_millimeter = 1;     // 1[mm] reference
        float height = 70;            // height
        float width_extremity = 35;   // width of start and end tiles
        float width_tiles = 45;       // width of the command tiles
        //float square = 40;            // 34 // length of the side of the square of interest
        float left_margin = (float) 1;   // 0.5 // length of the margin on the left up to the square of interest
        float right_margin = (float) 12.5; // length of the margin on the right up to the square of interest
        float top_and_bottom_margin = (height-width_tiles)/2; //float top_and_bottom_margin = (height-square)/2; // = 20[mm]
        //***********************

        // Ratios and corresponding pixel size of objects (in [pxl])
        float ratio_one_millimeter = height/one_millimeter;
        int o_m = (int) Math.floor(h/ratio_one_millimeter); //int o_m = Math.round(h/ratio_one_millimeter);
        //System.out.println("ratio_one_millimeter - " + ratio_one_millimeter);
        Log.d("tag", "ratio_one_millimeter - " + ratio_one_millimeter);
        //System.out.println("o_m: " + o_m + " [pxl]");
        Log.d("tag", "o_m: " + o_m + " [pxl]");
        float ratio_extremity = height/width_extremity;
        int w_e = (int) Math.floor(h/ratio_extremity); //int w_e = Math.round(h/ratio_extremity);
        //System.out.println("ratio_extremity - " + ratio_extremity);
        Log.d("tag", "ratio_extremity - " + ratio_extremity);
        //System.out.println("w_e: " + w_e + " [pxl]");
        Log.d("tag", "w_e: " + w_e + " [pxl]");
        float ratio_tiles = height/width_tiles;
        int w_t = (int) Math.floor(h/ratio_tiles); //int w_t = Math.round(h/ratio_tiles);
        //System.out.println("ratio_tiles - " + ratio_tiles);
        Log.d("tag", "ratio_tiles - " + ratio_tiles);
        //System.out.println("w_t: " + w_t + " [pxl]");
        Log.d("tag", "w_t: " + w_t + " [pxl]");
        //float ratio_square_of_interest = height/square;
        //int s = Math.round(h/ratio_square_of_interest);
        ////System.out.println("ratio_square_of_interest - " + ratio_square_of_interest);
        //Log.d("tag", "ratio_square_of_interest - " + ratio_square_of_interest);
        ////System.out.println("s: " + s + " [pxl]");
        //Log.d("tag", "s: " + s + " [pxl]");
        float ratio_left_margin = (float) (height/left_margin);
        int l_m = Math.round(h/ratio_left_margin);
        //System.out.println("ratio_left_margin - " + ratio_left_margin);
        Log.d("tag", "ratio_left_margin - " + ratio_left_margin);
        //System.out.println("l_m: " + l_m + " [pxl]");
        Log.d("tag", "l_m: " + l_m + " [pxl]");
        float ratio_right_margin = (float) (height/right_margin);
        int r_m = Math.round(h/ratio_right_margin);
        //System.out.println("ratio_right_margin - " + ratio_right_margin);
        Log.d("tag", "ratio_right_margin - " + ratio_right_margin);
        //System.out.println("r_m: " + r_m + " [pxl]");
        Log.d("tag", "r_m: " + r_m + " [pxl]");
        float ratio_top_and_bottom_margin = height/top_and_bottom_margin;
        int t_b_m = Math.round(h/ratio_top_and_bottom_margin);
        //System.out.println("ratio_top_and_bottom_margin - " + ratio_top_and_bottom_margin);
        Log.d("tag", "ratio_top_and_bottom_margin - " + ratio_top_and_bottom_margin);
        //System.out.println("t_b_m: " + t_b_m + " [pxl]");
        Log.d("tag", "t_b_m: " + t_b_m + " [pxl]");

        ////////////////////////////////////////////////////////////////////////







        // todo: B) Rotating the whole sequence if necessary
        ////////////////////////////////////////////////////////////////////////
        // ... the little black square that has to be on the top right of our
        // sequence of tiles has been detected on the bottom left (i.e. at the
        // beginning)
        // "Slicing-like behaviour" with Java Mat objects
        // (Cf.: https://answers.opencv.org/question/34591/opencv-extract-portion-of-a-mat-image/)
        // OLD: // Rect roi_probe_area = new Rect(5*o_m, 60*o_m, (15-5)*o_m, 10*o_m); // For Test9 --> "Rect roi_probe_area = new Rect(5*o_m, 62*o_m, (15-5)*o_m, (72-62)*o_m);"
        // Last working version // Rect roi_probe_area = new Rect(5*o_m, 55*o_m, (15-5)*o_m, 15*o_m);
        int real_height = rectified.rows();
        int begin_x = 0*o_m;
        int begin_y = (int) Math.floor(real_height*2/3); // 45*o_m
        int width_roi = 25*o_m;
        int height_roi = (int) Math.floor(real_height/3); // 25*o_m
        Rect roi_probe_area = new Rect(begin_x, begin_y, width_roi, height_roi); // Considering a larger area
        Mat probe_area = new Mat(rectified, roi_probe_area);

        // Old way of dealing with this black color detection:
        //-----
//        // /!\ probe_area HAS to contain a part of lighter pixels as well!
//        // (Otherwise, the grayscale step will only be based on darker pixels of
//        // the same range and the result won't be as expected...)
//        showResult(probe_area, "う.B.1) probe_area", 100, 100);
//        // Gray
//        Mat probe_area_gray = new Mat();
//        Imgproc.cvtColor(probe_area, probe_area_gray, Imgproc.COLOR_RGB2GRAY);
//        // Blur
//        Mat probe_area_blur = new Mat();
//        Imgproc.medianBlur(probe_area_gray, probe_area_blur, 5);
//        // Thresh
//        Mat probe_area_thresh = new Mat();
//        Imgproc.threshold(probe_area_blur, probe_area_thresh, 0, 255, Imgproc.THRESH_BINARY+Imgproc.THRESH_OTSU);
//        showResult(probe_area_thresh, "う.B.2) probe_area_thresh", 100, 100);
//        // '0' ≡ black, '255' ≡ white ✓
//        Core.MinMaxLocResult minMax_probe_area_thresh = Core.minMaxLoc(probe_area_thresh);
//        double maxVal_probe_area_thresh = minMax_probe_area_thresh.maxVal;
//        double minVal_probe_area_thresh = minMax_probe_area_thresh.minVal;
//        boolean black = Core.mean(probe_area_thresh).val[0] < Math.round((maxVal_probe_area_thresh - minVal_probe_area_thresh)/2);
        //-----
//        // Considering the V(alue) of HSV
//        Mat probe_area_hsv = new Mat();
//        Imgproc.cvtColor(probe_area, probe_area_hsv, Imgproc.COLOR_RGB2HSV);
//        Mat probe_area_v = new Mat();
//        extractChannel(probe_area_hsv, probe_area_v, 2); // Getting the 3rd channel (i.e. "V") of probe_area_hsv
//        // Darkness value for V of HSV
//        double darkness_value;
//        darkness_value = (Core.mean(probe_area_v).val[0]+Core.mean(probe_area_v).val[1]+Core.mean(probe_area_v).val[2])/3; // OLD darkness for RGB: // darkness_value = (Core.mean(probe_area).val[0]+Core.mean(probe_area).val[1]+Core.mean(probe_area).val[2])/3;
//        boolean black = darkness_value < 67; // OLD threshold: 120
        //-----
        //********
//        // Considering grayscale version
//        Mat probe_area_gray = new Mat();
//        Imgproc.cvtColor(probe_area, probe_area_gray, Imgproc.COLOR_RGB2GRAY);
//        // Darkness of grayscale
//        double darkness_value;
//        darkness_value = (Core.mean(probe_area_gray).val[0]+Core.mean(probe_area_gray).val[1]+Core.mean(probe_area_gray).val[2])/3; // OLD darkness for RGB: // darkness_value = (Core.mean(probe_area).val[0]+Core.mean(probe_area).val[1]+Core.mean(probe_area).val[2])/3;
//        boolean black = darkness_value > 50;
//        if (black) {
//            Core.flip(rectified,rectified,-1); // Rotate clockwise 180 degrees, cf.: https://stackoverflow.com/questions/16265673/rotate-image-by-90-180-or-270-degrees
//            if (showPictures == true) {
//                showResult(rectified, "う.B.3) flipped rectified", (int) width_rectified/3, (int) height_rectified/3);
//            }
//        }
        //********
        // Finding the contour present in probe_area
        // 1) gray
        Mat probe_area_gray = new Mat();
        Imgproc.cvtColor(probe_area, probe_area_gray, Imgproc.COLOR_RGB2GRAY);
        // 2) blur
        Mat probe_area_med = new Mat();
        Imgproc.medianBlur(probe_area_gray, probe_area_med, 9);
        // 3) otsu
        Mat probe_area_thresh = new Mat();
        Imgproc.threshold(probe_area_med, probe_area_thresh, 0, 255, Imgproc.THRESH_BINARY_INV+Imgproc.THRESH_OTSU);

//        // 4) morphology
//        Mat dist_transform = new Mat();
//        Imgproc.distanceTransform(probe_area_thresh, dist_transform, Imgproc.CV_DIST_L2, 5);
//        Mat probe_area_sure_fg = new Mat();
//        Imgproc.threshold(dist_transform, probe_area_sure_fg, 0.7*Core.minMaxLoc(dist_transform).maxVal, 255, 0);
//        if (showPictures == true) {
//            showResult(probe_area_sure_fg, "う.B.3) probe_area_sure_fg", width_roi, height_roi);
//        }
        // finding contours
        probe_area_thresh.convertTo(probe_area_thresh, CvType.CV_8UC1); // Converting the type of probe_area_sure_fg in order for findContours to support it
        List<MatOfPoint> contours_probe_area = new ArrayList<>();
        Mat hierarchy_probe_area = new Mat();
        Imgproc.findContours(probe_area_thresh, contours_probe_area, hierarchy_probe_area, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        // Drawing all the contours
//        Mat probe_area_all_contours_drawing = Mat.zeros(probe_area_thresh.size(), CvType.CV_8UC3);
//        for (int i = 0; i < contours_probe_area.size(); i++) {
//            Scalar color_probe_area = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
//            Imgproc.drawContours(probe_area_all_contours_drawing, contours_probe_area, i, color_probe_area, 1, Core.LINE_8, hierarchy_probe_area, 0, new Point());
//        }

        int probe_area_number_of_contours;
        probe_area_number_of_contours = contours_probe_area.size(); // in the case where we have no black square, the number of contours detected is either '=0' (if the probe_area is clear and well-lit like e.g. "tests_NVIDIA-tablet-PaPL-app-pictures_maximum_number_of_tiles/16Tiles.jpg") and otherwise '>1' if it is in a darker area of a house e.g. (like e.g. "tests_NVIDIA-tablet-PaPL-app-pictures_2020-04-09/TileSequence_Test20_13Tiles.jpg")

        if (probe_area_number_of_contours == 1) {
            Core.flip(rectified,rectified,-1); // Rotate clockwise 180 degrees, cf.: https://stackoverflow.com/questions/16265673/rotate-image-by-90-180-or-270-degrees
        }

        ////////////////////////////////////////////////////////////////////////













        // todo: C) Cropping
        ////////////////////////////////////////////////////////////////////////
        Rect roi_cropped = new Rect( w_e, 0,  W-2*w_e, h);
        Mat cropped = new Mat(rectified, roi_cropped);
        double W_cropped = cropped.cols();
        //showResult(cropped, "う.C.1) cropped", (int) W_cropped/3, (int) height_rectified/3);
        // Recentering the cropped window around the tiles
        Rect roi_cropped_recentered = new Rect( w_e-5*o_m, 0,  W-2*w_e, h);
        Mat cropped_recentered = new Mat(rectified, roi_cropped_recentered);
        //showResult(cropped_recentered, "う.C.2) cropped_recentered", (int) W_cropped/3, (int) height_rectified/3);
        // Number of tiles to identify
        int number_of_tiles = (int) Math.round(W_cropped/w_t); //int number_of_tiles = (int) Math.ceil(W_cropped/w_t);
        // Initialization of the String of commands
        String[] commands =  new String[number_of_tiles];
        ////////////////////////////////////////////////////////////////////////
















        // todo: D) Preparing the tiles-templates
        ////////////////////////////////////////////////////////////////////////
        Rect roi_whole_first_tile_template = new Rect(0, 0, w_t, h);
        Mat whole_first_tile = new Mat(cropped, roi_whole_first_tile_template);
        //showResult(whole_first_tile, "う.D.1) whole_first_tile", (int) w_t/3, (int) height_rectified/3);
        Rect precise_roi_first_tile = new Rect(0, t_b_m, w_t,w_t); //Rect precise_roi_first_tile = new Rect(l_m, t_b_m, s,s);
        Mat region_of_interest_first_tile = new Mat(whole_first_tile, precise_roi_first_tile);
        double h_roi = region_of_interest_first_tile.cols(); // this is equivalent to region_of_interest_first_tile.rows() since our region of interest is a square of side "s"!
        //showResult(region_of_interest_first_tile, "う.D.2) region_of_interest_first_tile", (int) h_roi/3, (int) h_roi/3);
        // Size for rescaling the templates (cf.: https://stackoverflow.com/questions/20902290/how-to-resize-an-image-in-java-with-opencv)
        Size sz = new Size(h_roi,h_roi);


        // Loading every tile-template
        // /!\ !!!Editable part!!! /!\
        // (If a later programmer wants to add some tiles-templates, this is here
        // below that he has to make the reference to the new tiles-templates!)
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++






//        Mat two = null;
//        try {
//            two = Utils.loadResource(context, tile_template_two, Highgui.CV_LOAD_IMAGE_COLOR);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//
////        Mat two = null;
////        try {
////            two = Utils.loadResource(ComputerVision.getContext(), R.drawable.tile_template_two);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat three = null;
////        try {
////            three = Utils.loadResource(ComputerVision.getContext(), tile_template_three);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat four = null;
////        try {
////            four = Utils.loadResource(ComputerVision.getContext(), tile_template_four);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat end_repeat = null;
////        try {
////            end_repeat = Utils.loadResource(ComputerVision.getContext(), tile_template_end_repeat);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat go_backward = null;
////        try {
////            go_backward = Utils.loadResource(ComputerVision.getContext(), tile_template_go_backward);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat go_forward = null;
////        try {
////            go_forward = Utils.loadResource(ComputerVision.getContext(), tile_template_go_forward);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat start_repeat = null;
////        try {
////            start_repeat = Utils.loadResource(ComputerVision.getContext(), tile_template_start_repeat);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat turn_back = null;
////        try {
////            turn_back = Utils.loadResource(ComputerVision.getContext(), tile_template_turn_back);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat turn_left = null;
////        try {
////            turn_left = Utils.loadResource(ComputerVision.getContext(), tile_template_turn_left);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//        //-----------------------------------------------------
//        Mat turn_right = null;
////        try {
////            turn_right = Utils.loadResource(ComputerVision.getContext(), tile_template_turn_right);
////        } catch (IOException e) {
////            e.printStackTrace();
////        }





        String[] tiles_templates_names = new String[] {"two", "three", "four", "end_repeat", "go_backward", "go_forward", "start_repeat", "turn_back", "turn_left", "turn_right"};

        int number_of_tiles_templates = 10;

        // Creating the array of Mat called containing all the tiles-templates
        // ("tableau d'objets Mat")
        Mat[] tiles_templates_array = new Mat[number_of_tiles_templates];
        tiles_templates_array[0] = two;
        tiles_templates_array[1] = three;
        tiles_templates_array[2] = four;
        tiles_templates_array[3] = end_repeat;
        tiles_templates_array[4] = go_backward;
        tiles_templates_array[5] = go_forward;
        tiles_templates_array[6] = start_repeat;
        tiles_templates_array[7] = turn_back;
        tiles_templates_array[8] = turn_left;
        tiles_templates_array[9] = turn_right;

        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
        //++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++



        // Looping through all the tiles-templates in order to retrieve and save
        // their main contour

        // Initializing the array in which we will save the main contours of the
        // tiles-templates
        MatOfPoint[] main_contour_tiles_templates_array = new MatOfPoint[number_of_tiles_templates];

        for (int i = 0; i < number_of_tiles_templates; i++) {
            // 1) Resize the current tile-template
            // Cf.: https://stackoverflow.com/questions/20902290/how-to-resize-an-image-in-java-with-opencv
            Mat tile_template_resized = new Mat();
            Imgproc.resize(tiles_templates_array[i], tile_template_resized, sz, 0, 0, Imgproc.INTER_AREA);
            // 2) Converting to grayscale
            Mat tile_template_resized_gray = new Mat();
            Imgproc.cvtColor(tile_template_resized, tile_template_resized_gray, Imgproc.COLOR_RGB2GRAY);
            // 3) Getting all the contours
            List<MatOfPoint> tile_template_contours = new ArrayList<>();
            Mat tile_template_hierarchy = new Mat();
            Imgproc.findContours(tile_template_resized_gray, tile_template_contours, tile_template_hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            // Retrieving THE contour of interest
            int tile_template_cnt_idx = 1; // taking '0' would lead to take the biggest
            // contour, i.e. the bounding square box (and this
            // is NOT what we want!)
            MatOfPoint tile_template_cnt = tile_template_contours.get(tile_template_cnt_idx);
            // Saving the THE contour of interest in our array of type MatOfPoint
            main_contour_tiles_templates_array[i] = tile_template_cnt;
            //// Drawing the main contour
            //Mat tile_template_main_contour_drawing = Mat.zeros(tile_template_resized_gray.size(), CvType.CV_8UC3);
            //Imgproc.drawContours(tile_template_main_contour_drawing, tile_template_contours, tile_template_cnt_idx, new Scalar(0,255,51), 1, Core.LINE_8, tile_template_hierarchy, 0, new Point());
            //showResult(tile_template_main_contour_drawing, "う.D.3) tile_template_main_contour", 300, 300);
        }


        ////////////////////////////////////////////////////////////////////////
















        // todo: E) Digging into identified square of interest
        ////////////////////////////////////////////////////////////////////////
        for (int i = 0; i < number_of_tiles; i++) {
            Mat whole_tile_temp = new Mat();
            if (i < Math.floor(number_of_tiles/2.0)) {
                // going on from the beginning of cropped
                Rect roi_current_real_tile = new Rect(i*w_t, 0, w_t, h);
                whole_tile_temp = new Mat(cropped_recentered, roi_current_real_tile);
            } else {
                // going on from the end of cropped
                Rect roi_current_real_tile = new Rect((int) W_cropped-(number_of_tiles-i)*w_t, 0, w_t, h);
                whole_tile_temp = new Mat(cropped_recentered, roi_current_real_tile);
            }
            //showResult(whole_tile_temp, "う.E.1) whole_tile_temp", w_t, h);
            // 1) Cropping the region of interest
            //**************************************************
            Rect roi_whole_real_tile = new Rect(0, 15*o_m, w_t,w_t); // Rect roi_whole_real_tile = new Rect(l_m, 18*o_m, s,s);
            Mat region_of_interest = new Mat(whole_tile_temp, roi_whole_real_tile);
            //showResult(region_of_interest, "う.E.2) region_of_interest", 300, 300);
            // 2) Converting to grayscale
            //**************************************************
            Mat region_of_interest_gray = new Mat();
            Imgproc.cvtColor(region_of_interest, region_of_interest_gray, Imgproc.COLOR_RGB2GRAY);
            // 3) Blur
            //**************************************************
            Mat region_of_interest_blur = new Mat();
            Imgproc.medianBlur(region_of_interest_gray, region_of_interest_blur, 5);
            // 4) Threshold
            //**************************************************
            Mat region_of_interest_thresh = new Mat();
            Imgproc.threshold(region_of_interest_blur, region_of_interest_thresh, 0, 255, Imgproc.THRESH_BINARY+ Imgproc.THRESH_OTSU);
            // 5) Blur 2 (to clear small details)
            //**************************************************
            Mat region_of_interest_blur_2 = new Mat();
            Imgproc.medianBlur(region_of_interest_thresh, region_of_interest_blur_2, 5);
            // 6) Getting all the contours
            //**************************************************
            List<MatOfPoint> region_of_interest_contours = new ArrayList<>();
            Mat region_of_interest_hierarchy = new Mat();
            Imgproc.findContours(region_of_interest_blur_2, region_of_interest_contours, region_of_interest_hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            // Drawing all the contours
            //Mat region_of_interest_all_contours_drawing = Mat.zeros(region_of_interest_blur_2.size(), CvType.CV_8UC3);
            //for (int j = 0; j < region_of_interest_contours.size(); j++) {
            //    Scalar region_of_interest_color = new Scalar(rng.nextInt(256), rng.nextInt(256), rng.nextInt(256));
            //    Imgproc.drawContours(region_of_interest_all_contours_drawing, region_of_interest_contours, j, region_of_interest_color, 1, Core.LINE_8, region_of_interest_hierarchy, 0, new Point());
            //}
            //showResult(region_of_interest_all_contours_drawing, "う.E.3) region_of_interest_all_contours", 300, 300);

            // 7) Retrieving and saving THE contour of interest
            //**************************************************
            // (I.e. the second largest contour! --> we run 2 times the
            // algorithm used to retrieve the largest contour!)

            //------------------------------------------------------------------
            // Finding the largest contour
            double maxVal1 = 0;
            int maxValIdx1 = 0;
            for (int contourIdx = 0; contourIdx < region_of_interest_contours.size(); contourIdx++)
            {
                double contourArea = Imgproc.contourArea(region_of_interest_contours.get(contourIdx));
                if (maxVal1 < contourArea)
                {
                    maxVal1 = contourArea;
                    maxValIdx1 = contourIdx;
                }
            }
            // Finding the second largest contour
            double maxVal2 = 0;
            int maxValIdx2 = 0;
            for (int contourIdx = 0; contourIdx < region_of_interest_contours.size(); contourIdx++)
            {
                double contourArea = Imgproc.contourArea(region_of_interest_contours.get(contourIdx));
                if (maxVal2 < contourArea && contourIdx!=maxValIdx1)
                {
                    maxVal2 = contourArea;
                    maxValIdx2 = contourIdx;
                }
            }
            //------------------------------------------------------------------

            int region_of_interest_cnt_idx = maxValIdx2;
            MatOfPoint region_of_interest_cnt = region_of_interest_contours.get(region_of_interest_cnt_idx);
            // Drawing the contour of interest
            //Mat region_of_interest_main_contour_drawing = Mat.zeros(region_of_interest_blur_2.size(), CvType.CV_8UC3);
            //Imgproc.drawContours(region_of_interest_main_contour_drawing, region_of_interest_contours, region_of_interest_cnt_idx, new Scalar(255,102,0), 1, Core.LINE_8, region_of_interest_hierarchy, 0, new Point());
            //showResult(region_of_interest_main_contour_drawing, "う.E.4) region_of_interest_main_contour", 300, 300);

            // 8) Template matching
            //**************************************************
            //System.out.println("\n---");
            Log.d("tag", "\n---");
            //System.out.println("Template matching - distance data for tile n°"+i+":");
            Log.d("tag", "Template matching - distance data for tile n°"+i+":");
            double d2_smallest_so_far = Double.POSITIVE_INFINITY;
            for (int j = 0; j < number_of_tiles_templates; j++) {
                MatOfPoint cnt_ref = main_contour_tiles_templates_array[j];
                // Making use of the matchShapes function
                // Cf.: https://stackoverflow.com/questions/41718140/how-to-check-if-two-contours-match-using-opencv-matchshapes
                // Cf.: https://docs.opencv.org/2.4/modules/imgproc/doc/structural_analysis_and_shape_descriptors.html#double%20matchShapes(InputArray%20contour1,%20InputArray%20contour2,%20int%20method,%20double%20parameter)
                double d2 = Imgproc.matchShapes(region_of_interest_cnt,cnt_ref,org.opencv.imgproc.Imgproc.CV_CONTOURS_MATCH_I2,0);
                //System.out.println("distance with tile-template n°" + j + " = " + d2);
                Log.d("tag", "distance with tile-template n°" + j + " = " + d2);
                if (d2 < d2_smallest_so_far) {
                    commands[i] = tiles_templates_names[j];
                    d2_smallest_so_far = d2;
                }
            }
        }



        ////////////////////////////////////////////////////////////////////////


        //System.out.println("\n\n⨁⨁⨁⨁⨁⨁⨁⨁⨁");
        Log.d("tag", "\n\n⨁⨁⨁⨁⨁⨁⨁⨁⨁");
        //System.out.println("The commands to send to the robot are:");
        Log.d("tag", "The commands to send to the robot are:");
        //System.out.println(Arrays.toString(commands));
        Log.d("tag", Arrays.toString(commands));
        //System.out.println("⨁⨁⨁⨁⨁⨁⨁⨁⨁");
        Log.d("tag", "⨁⨁⨁⨁⨁⨁⨁⨁⨁");








        //*******************************************************************************
        //*******************************************************************************
        //*******************************************************************************


        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□
        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□
        //□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□□



        //return commands;
        return new Pair<String[], Mat>(commands, cropped);


    }








    //**************************************************************************
    // Methods for operations on arrays:
    //----------------------------------

    // Find minimum (lowest) value in array using array sort
    public static int minValue(int[] numbers) {
        Arrays.sort(numbers);
        return numbers[0];
    }
    // Find maximum (largest) value in array using array sort
    public static int maxValue(int[] numbers) {
        Arrays.sort(numbers);
        return numbers[numbers.length-1];
    }

    // Find difference of two arrays
    public static int[] diffArray(int[] array1, int[] array2) {

        int[] diff = new int[array1.length];

        for (int i = 0; i < array1.length; i++) {
            diff[i] = array1[i] - array2[i];
        }
        return diff;
    }

    // Find sum of two arrays
    public static int[] sumArray(int[] array1, int[] array2) {

        int[] diff = new int[array1.length];

        for (int i = 0; i < array1.length; i++) {
            diff[i] = array1[i] + array2[i];
        }
        return diff;
    }

    // Find index of the smallest number in an array
    public static int indexOfSmallestNumberInArray(int[] array) {
        int min = array[0];
        int index=0;
        for(int i = 0; i < array.length; i++)
        {
            if(min > array[i])
            {
                min = array[i];
                index=i;
            }
        }
        return index;
    }

    // Find the index of the largest number in an array
    public static int indexOfLargestNumberInArray(int[] array) {
        int max = array[0];
        int index=0;
        for(int i = 0; i < array.length; i++)
        {
            if(max < array[i])
            {
                max = array[i];
                index=i;
            }
        }
        return index;
    }

    // Calculating the norm of an array of two elements
    public static int normMethod(int[] array) {
        int norm = (int) Math.sqrt(Math.pow(array[0],2)+Math.pow(array[1],2));
        return norm;
    }

    // Calculating the difference between two Point objects
    public static int[] diffPoint(Point point1, Point point2) {

        int[] diffPoint = new int[2];

        diffPoint[0] = (int) (point1.x - point2.x);

        diffPoint[1] = (int) (point1.y - point2.y);

        return diffPoint;
    }


    //**************************************************************************



}








