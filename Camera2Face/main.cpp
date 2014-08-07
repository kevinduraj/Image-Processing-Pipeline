/*
 * Face Detection based on OpenCV
 * Written by: Kevin T. Duraj
 */
#include "opencv2/objdetect/objdetect.hpp"
#include "opencv2/highgui/highgui.hpp"
#include "opencv2/imgproc/imgproc.hpp"
/*----------------------------------------------------------------------------*/
#include <iostream>
#include <stdio.h>
#include <unistd.h>
/*----------------------------------------------------------------------------*/
using namespace std;
using namespace cv;

/*----------------------------------------------------------------------------*/
// Function Headers
void FindFace2Frame(Mat frame);

/*----------------------------------------------------------------------------
                            Global Variables
 ----------------------------------------------------------------------------*/
unsigned int go_sleep = 10 * 100000;
string face_cascade_name = "haarcascade_frontalface_alt.xml";
CascadeClassifier face_cascade_classifier;

string window_name = "Capture - Face detection";
int filenumber; 
string filename;

/*----------------------------------------------------------------------------*/
int main(void) {
    
    VideoCapture capture(0);

    if (!capture.isOpened())
        return -1;

    // Load the cascade files
    if (!face_cascade_classifier.load(face_cascade_name)) {
        cout << "Error loading " <<  face_cascade_name;
        return (-1);
    };

    //-- Read camera stream
    Mat frame;

    for (;;) {
        
        capture >> frame;

        // Apply the classifier to the frame
        if (!frame.empty()) {
            FindFace2Frame(frame);
        } else {
            cout << "No captured frame " << endl;
            break;
        }

        int c = waitKey(10);

        // Press Escape to exit application
        if (27 == char(c)) {
            break;
        }
        
        usleep(go_sleep);
    }

    return 0;
}

/*----------------------------------------------------------------------------*/
void FindFace2Frame(Mat frame) {
    
    std::vector<Rect> faces;
    
    Mat frame_gray;
    Mat crop;
    Mat res;
    Mat gray;
    string text;
    stringstream sstm;

    cvtColor(frame, frame_gray, COLOR_BGR2GRAY);
    equalizeHist(frame_gray, frame_gray);

    // Detect faces
    //face_cascade.detectMultiScale(frame_gray, faces, 1.1, 2, 0 | CASCADE_SCALE_IMAGE, Size(30, 30));
    face_cascade_classifier.detectMultiScale(frame_gray, faces, 1.1, 2, 0 | CASCADE_SCALE_IMAGE, Size(50, 50));

    // Set Region of Interest
    cv::Rect roi_b;
    cv::Rect roi_c;

    size_t ic = 0; // ic is index of current element
    int ac = 0;    // ac is area of current element

    size_t ib = 0; // ib is index of biggest element
    int ab = 0;    // ab is area of biggest element

    
    // Iterate through all current elements (detected faces)
    for (ic = 0; ic < faces.size(); ic++) 
    {
        roi_c.x = faces[ic].x;
        roi_c.y = faces[ic].y;
        roi_c.width = (faces[ic].width);
        roi_c.height = (faces[ic].height);

        // Get the area of current element (detected face)
        ac = roi_c.width * roi_c.height; 

        roi_b.x = faces[ib].x;
        roi_b.y = faces[ib].y;
        roi_b.width = (faces[ib].width);
        roi_b.height = (faces[ib].height);

        //--- Area of biggest detected element
        ab = roi_b.width * roi_b.height; 

        if (ac > ab) {
            ib = ic;
            roi_b.x = faces[ib].x;
            roi_b.y = faces[ib].y;
            roi_b.width = (faces[ib].width);
            roi_b.height = (faces[ib].height);
        }

        crop = frame(roi_b);
        
        //--- Resize image to save it into images subdirectory
        resize(crop, res, Size(256, 256), 0, 0, INTER_LINEAR); 
        
        //--- Convert cropped image into Gray-scale for further processing
        cvtColor(crop, gray, CV_BGR2GRAY); 

        //--- create a filename
        filename = "";
        stringstream ssfn;
        //ssfn << "images/" << filenumber << ".png";
        ssfn << "../src/main/java/faces/" << filenumber << ".png";
        filename = ssfn.str();
        filenumber++;

        imwrite(filename, gray);

        //--- Draw green square around the a detected face
        Point pt1(faces[ic].x, faces[ic].y);
        Point pt2((faces[ic].x + faces[ic].height)
                , (faces[ic].y + faces[ic].width));
        
        rectangle(frame, pt1, pt2, Scalar(0, 255, 0), 2, 8, 0);
    }

    //--- Show Image within separate frame
    sstm << "Crop area size: " << roi_b.width << "x" 
         << roi_b.height << " Filename: " << filename;
    text = sstm.str();

    putText(frame, text, cvPoint(30, 30)
                 , FONT_HERSHEY_COMPLEX_SMALL
                 , 0.8, cvScalar(0, 0, 255), 1, CV_AA);
    
    imshow("original", frame);

    if (!crop.empty()) {
        imshow("detected", crop);
    } else {
        destroyWindow("detected");
    }
}
/*---------------------------------------------------------------------------------------*/

