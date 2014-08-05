package processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Main {

    private static String inputDir  = "src/main/java/image/";
    private static String outputDir = "src/main/java/pipeline/";
    
    private static final String sourceImage = "Lenna1.png";
    private static final String finalImage = "Lenna2.png"; 
    
    private static final int padding_x = 7;     // Must be odd number
    private static final int padding_y = 7;     // Must be odd number
    private static final int kernelSize = 15;   // Kernel size 
    private static final float sigma = 1.6f;
    private static int treshold = 0;
  
    
    /*--------------------------------------------------------------------------------------------*/        
    public static void main(String[] args) throws IOException {

        System.out.println("Processing Image Tranformation Pipeline");
        
        /*-------------------- Percentile -----------------------*/
        int[][] imgPrecentile = ProcessPrecentile();
        
        Reflection ref = new Reflection();        
        int[][] imgReflection = ref.convolve(imgPrecentile, padding_x, padding_y);  
        ImageWrite(outputDir + "reflection.png", imgReflection);
           
        /*------------------- Median Filter ---------------------*/
        MedianFilter median = new MedianFilter();
        int[][] imgMedian = median.process(imgReflection);        
        ImageWrite(outputDir + "Median.png", imgMedian);
        
        /*------------------- Gausian 2D ------------------------*/
        Gaussian2D g2d = new Gaussian2D();
        float[][] kernel = g2d.kernel(sigma, kernelSize, false);
        int[][] imgGaussian = g2d.convolve(imgMedian, kernel);        
        ImageWrite(outputDir + "Gaussian.png", imgGaussian);
                
        /*---------------- Sobel Edge Detection -----------------*/
        Sobel sobel = new Sobel();
        sobel.process(outputDir + "Gaussian.png");
        
        /*---------- Scaledown Remove Reflection Padding --------*/
        int[][] temp = ScaleDown(sobel.Magnitute ,padding_x,padding_y);
        ImageWrite( outputDir + "SobelMagnitute.png", temp);                  
        temp = ScaleDown(sobel.Direction ,padding_x,padding_y);
        ImageWrite( outputDir + "SobelDirection.png", temp);
        
        /*------------------ Otsu Binarize ----------------------*/
        OtsuBinarize otsu = new OtsuBinarize(outputDir + "SobelMagnitute.png");
        otsu.run(); 
        File file = new File(inputDir + finalImage);
        ImageIO.write(otsu.binarized, "png", file);
        
        
        /*--------------------- Statistics ----------------------*/
        Statistics stat1 = new Statistics(outputDir + "SobelMagnitute.png");
        System.out.format("\n1.) src/image/SobelMagnitute.png"  
                  + "\n\tSobel Magnitide - Image Output Mean = %.3f\n\n"
                  , stat1.getMean());        

        Statistics stat2 = new Statistics(outputDir + "SobelDirection.png");
        System.out.format("\n2.) src/image/SobelDirection.png"  
                  + "\n\tSobel Direction - Image Output Mean = %.3f\n\n"
                  , stat2.getMean());        

        Statistics stat3 = new Statistics(inputDir + finalImage);
        System.out.format("\n3.) " + inputDir + finalImage  
                  + "\n\tSobel with Otsu Treshold = " + otsu.treshold 
                  + " - Image Output Mean = %.3f\n", stat3.getMean());        
        
    }
    /*--------------------------------------------------------------------------------------------*/    
    private static int[][] ProcessPrecentile() throws IOException {
        
        Percentile percentile = new Percentile();
        int [] mapHist = percentile.readHistogram(inputDir + sourceImage);      
        int[] cutoff = percentile.setCutoff(mapHist, 10.0); 
       
        int[][] green = ImageRead(inputDir + sourceImage);
        
        percentile.WriteStretchedImage(green
                                        , percentile.first
                                        , percentile.last
                                        , new int[0]
                                        , outputDir + "zCutOff.png");
        
        int[] stretchedHist = percentile.stretchMap(percentile.first, percentile.last);
        
        percentile.WriteStretchedImage(green
                                        , percentile.first
                                        , percentile.last
                                        , stretchedHist
                                        , outputDir + "Percentile.png");        
        return green;
    }    
    /*--------------------------------------------------------------------------------------------*/
    private static int[][] ImageRead(String filename) throws IOException {

            File infile = new File(filename);
            BufferedImage bi = ImageIO.read(infile);

            int red[][] = new int[bi.getHeight()][bi.getWidth()];
            int grn[][] = new int[bi.getHeight()][bi.getWidth()];
            int blu[][] = new int[bi.getHeight()][bi.getWidth()];
            
            for (int i = 0; i < red.length; ++i) {
                for (int j = 0; j < red[i].length; ++j) {
                    red[i][j] = bi.getRGB(j, i) >> 16 & 0xFF;
                    grn[i][j] = bi.getRGB(j, i) >> 8 & 0xFF;
                    blu[i][j] = bi.getRGB(j, i) & 0xFF;
                }
            }
            return grn;
    }
    /*--------------------------------------------------------------------------------------------*/
    private static void ImageWrite(String filename, int img[][]) throws IOException {

            BufferedImage bi = new BufferedImage(img[0].length, img.length, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < bi.getHeight(); ++i) {
                for (int j = 0; j < bi.getWidth(); ++j) {
                    int val = img[i][j];
                    int pixel = (val << 16) | (val << 8) | (val);
                    bi.setRGB(j, i, pixel);
                }
            }

            File outputfile = new File(filename);
            ImageIO.write(bi, "png", outputfile);
    }
    /*--------------------------------------------------------------------------------------------*/
    private static void ImageWrite(String filename, double img[][]) throws IOException {

            BufferedImage bi = new BufferedImage(img[0].length, img.length, BufferedImage.TYPE_INT_RGB);

            for (int i = 0; i < bi.getHeight(); ++i) {
                for (int j = 0; j < bi.getWidth(); ++j) {
                    int val = (int)img[i][j];
                    int pixel = (val << 16) | (val << 8) | (val);
                    bi.setRGB(j, i, pixel);
                }
            }

            File outputfile = new File(filename);
            ImageIO.write(bi, "png", outputfile);
    }
    /*--------------------------------------------------------------------------------------------*/    
    public static int[][] ScaleDown(double[][] paddedImg, int x_size, int y_size) {
        
        int height=paddedImg.length-y_size+1;
        int width=paddedImg[0].length-x_size+1;
        
        int half_y=y_size/2;
        int half_x=x_size/2;
        
        int[][] scaledDownImg = new int[height][width];
        
        for(int i=0; i<height; i++) {
            for(int j=0; j<width; j++) {                                
                scaledDownImg[i][j] = (int) paddedImg[half_y+i][half_x+j];
            }
        }
        
        return scaledDownImg;
        
    }
    /*--------------------------------------------------------------------------------------------*/    
}
