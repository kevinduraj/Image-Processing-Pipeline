package processing;

import static java.lang.Math.exp;

public class Gaussian2D {
    
    /* --------------------------------------------------------------------------------------------
                            Compute Gaussian 2D Kernel
     ---------------------------------------------------------------------------------------------*/

    public  float[][] kernel( float sigma, int size, boolean print) {

        float[][] kernel = new float[size][size];
        int uc, vc;
        float g, sum;
        sum = 0;
        
        /*--- COMPUTE: Kernel Matrix Values ---*/
        for (int u = 0; u < kernel.length; u++) {
            for (int v = 0; v < kernel[0].length; v++) {
                
                uc = u - (kernel.length - 1) / 2;
                vc = v - (kernel[0].length - 1) / 2;

                g = (float) exp(-(uc * uc + vc * vc) / (2 * sigma * sigma));
                sum += g;
                kernel[u][v] = g;
                if(print) System.out.format("%.6f ", g);
            }
            if(print) System.out.println();
        }
        
        //--- NORMALIZE: Total of all kernel's elemenents = 1.0 ---//
        for (int u = 0; u < kernel.length; u++) {
            for (int v = 0; v < kernel[0].length; v++) {
                kernel[u][v] /= sum;
            }
        }
        
        return kernel;
    }

    /* --------------------------------------------------------------------------------------------
     *                      Convolves Image with a Kernel 
     * -------------------------------------------------------------------------------------------*/
    public  int[][] convolve(int[][] img, float[][] kernel) {
        
        int xn, yn;
        float average;

        int w = img.length;
        int h = img[0].length;
        int[][] output = new int[w][h];

        //--- IMAGE: Iterate through image pixels ---//
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {

                //--- KERNEL: Iterate through kernel ---//
                average = 0;
                for (int u = 0; u < kernel.length; u++) {
                    for (int v = 0; v < kernel[0].length; v++) {

                        //--- Get associated neighbor pixel coordinates ---//
                        xn = x + u - kernel.length / 2;
                        yn = y + v - kernel[0].length / 2;

                        //--  Make sure we don't go off of an edge of the image ---//
                        xn = constrain(xn, 0, w - 1);
                        yn = constrain(yn, 0, h - 1);

                        //--- Add weighted neighbor to average ---//
                        average += img[xn][yn] * kernel[u][v];
                    }
                } /*--- KERNEL ---*/

                //--- Set output pixel to weighted average value ---//
                output[x][y] = (int) average;
            }
        } /*--- IMAGE ---*/

        return output;
    }
    /*--------------------------------------------------------------------------------------------*/

    public int constrain(int x, int a, int b) {
        if (x < a) {
            return a;
        } else if (b < x) {
            return b;
        } else {
            return x;
        }
    }
    /*--------------------------------------------------------------------------------------------*/
}