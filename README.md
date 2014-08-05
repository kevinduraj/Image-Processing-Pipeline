Image Processing Pipeline
=========================
* Read image file
* Separate image into three components keeping the green component for subsequent processing
* Perform a contrast enhancement (histogram stretch) using the 10th and 90th percentile bins as cutoff points
* Perform a 3x3 median filter operation utilizing reflection for the boarder pixels
* Perform a Gaussian kernel convolution with a kernel width of 15 and a sigma of 1.5. Reflection is used to expand the border pixels. You may use either 1D or 2D filtering.
* Perform a Sobel edge detection producing scaled magnitude and direction images.
* Perform the Otsu algorithm on the Sobel magnitude image to determine the optimal threshold.
* Using the optimal threshold determined in step 7, threshold the magnitude image.
* Use the thresholded Sobel magnitude image to mask out unwanted edges in the Sobel direction image.
 - If a pixel in the thresholded Sobel magnitude image is 0, the corresponding pixel in theSobel direction image should be set to 0. If a pixel in the thresholded Sobel magnitude image is 255 then the corresponding pixel in the Sobel direction image should retain its direction value.
* Write the thresholded Sobel magnitude and direction images to image files.

Output
======
* Processing Pipeline

* 1.) src/image/SobelMagnitute.png
    - Sobel Magnitide - Image Output Mean = 4.457

* 2.) src/image/SobelDirection.png
    - Sobel Direction - Image Output Mean = 133.778

* 3.) src/image/SobelTreshhold9.png
    - Sobel with Otsu Treshold = 9 - Image Output Mean = 34.012
