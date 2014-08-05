package processing;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/*------------------------------------------------------------------------------------------------*/
public class OtsuBinarize {

    private BufferedImage original, grayscale;
    public BufferedImage binarized;
    public int treshold;
    //private String output_file;
    private File filename;

    /*--------------------------------------------------------------------------------------------
     Perform an Otsu optimal threshold operation
     --------------------------------------------------------------------------------------------*/
    public OtsuBinarize(String input) throws IOException {

        this.filename = new File(input);
    }
    /*--------------------------------------------------------------------------------------------*/

    public void run() throws IOException {
        original = ImageIO.read(filename);
        grayscale = toGray(original);
        binarized = binarize(grayscale);
        
        //writeImage(output_file);
        //return output_file;
    }
    /*--------------------------------------------------------------------------------------------*/

    private void writeImage(String output) throws IOException {

        File file = new File(output);
        ImageIO.write(binarized, "jpg", file);
    }
    /*----------------------------------------------------------------------------------------------
     Return histogram of grayscale image
     ----------------------------------------------------------------------------------------------*/

    private int[] imageHistogram(BufferedImage input) {

        int[] histogram = new int[256];

        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = 0;
        }

        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {

                int green = new Color(input.getRGB(i, j)).getGreen();
                histogram[green]++;
            }
        }
        //displayHistogram(histogram);
        return histogram;
    }
    /*----------------------------------------------------------------------------------------------
     The Luminance Method - convert original image to gray scale image
     --------------------------------------------------------------------------------------------*/

    private BufferedImage toGray(BufferedImage original) {

        int alpha, red, green, blue, gray;
        int pixel;

        BufferedImage bi = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {

                alpha = new Color(original.getRGB(i, j)).getAlpha();
                red = new Color(original.getRGB(i, j)).getRed();
                green = new Color(original.getRGB(i, j)).getGreen();
                blue = new Color(original.getRGB(i, j)).getBlue();

                gray = (int) (0.21 * red + 0.71 * green + 0.07 * blue);
                pixel = colorToRGB(alpha, gray, gray, gray);
                bi.setRGB(i, j, pixel);
            }
        }
        return bi;
    }

    /*----------------------------------------------------------------------------------------------    
     Get binary treshold using Otsu's method
     ---------------------------------------------------------------------------------------------*/
    private int otsuTreshold(BufferedImage original) {

        int[] histogram = imageHistogram(original);
        int total = original.getHeight() * original.getWidth();

        float sum = 0;
        for (int i = 0; i < 256; i++) {
            sum += i * histogram[i];
        }

        float sumB = 0;
        int wB = 0;
        int wF = 0;

        float varMax = 0;
        int threshold = 0;

        for (int i = 0; i < 256; i++) {
            wB += histogram[i];
            if (wB == 0) {
                continue;
            }
            wF = total - wB;

            if (wF == 0) {
                break;
            }

            sumB += (float) (i * histogram[i]);
            float mB = sumB / wB;
            float mF = (sum - sumB) / wF;

            float varBetween = (float) wB * (float) wF * (mB - mF) * (mB - mF);

            if (varBetween > varMax) {
                varMax = varBetween;
                threshold = i;
            }
        }

        return threshold;

    }
    /*----------------------------------------------------------------------------------------------
     Binarize Buffered Image
     ----------------------------------------------------------------------------------------------*/

    private BufferedImage binarize(BufferedImage original) {

        int red, pixel;
        this.treshold = otsuTreshold(original);

        //output_file = "src/image/OtsuBinarize" + threshold + ".png";

        BufferedImage bin = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

        for (int i = 0; i < original.getWidth(); i++) {
            for (int j = 0; j < original.getHeight(); j++) {

                // Get pixels
                red = new Color(original.getRGB(i, j)).getRed();
                int alpha = new Color(original.getRGB(i, j)).getAlpha();
                if (red > treshold) {
                    pixel = 255;
                } else {
                    pixel = 0;
                }
                pixel = colorToRGB(alpha, pixel, pixel, pixel);
                bin.setRGB(i, j, pixel);

            }
        }

        return bin;

    }
    /*---------------------------------------------------------------------------------------------
     Convert R, G, B, Alpha to standard 8 bit
     ----------------------------------------------------------------------------------------------*/

    private int colorToRGB(int alpha, int red, int green, int blue) {

        int pixel = 0;
        
        pixel += alpha;
        pixel <<= 8;
        pixel += red;
        pixel <<= 8;
        pixel += green;
        pixel <<= 8;
        pixel += blue;

        return pixel;
    }
    /*----------------------------------------------------------------------------------------------
     Display Histogram Values
     ----------------------------------------------------------------------------------------------*/

    private void displayHistogram(int[] histogram) {

        for (int i = 0; i < histogram.length; i++) {
            System.out.format("%3d -> %5d\n", i, histogram[i]);
        }
    }
    /*--------------------------------------------------------------------------------------------*/
}
