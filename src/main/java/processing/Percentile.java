package processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Percentile {

    private Integer totalPixels;
    private Integer cutoff;
    private Integer cutoffHigh;

    Integer first;
    Integer last;

    /*--------------------------------------------------------------------------------------------*/
    public Percentile() {
        totalPixels = 0;
        cutoff = 0;
        cutoffHigh = 0;
        first = 0;
        last = 0;
    }
    /*--------------------------------------------------------------------------------------------*/

    public int[] readHistogram(String filename) {

        int pixel[] = new int[256];

        try {

            File infile = new File(filename);
            BufferedImage bi = ImageIO.read(infile);

            int grn[][] = new int[bi.getHeight()][bi.getWidth()];

            for (int i = 0; i < grn.length; ++i) {
                for (int j = 0; j < grn[i].length; ++j) {

                    totalPixels++;
                    grn[i][j] = bi.getRGB(j, i) >> 8 & 0xFF;
                    int key = grn[i][j];

                    //--- increment pixel ---//                  
                    pixel[key] += 1;

                }
            }

        } catch (IOException e) {
            System.out.println(e + "image I/O error");
        }

        return pixel;
    }

    /*----------------------------------------------------------------------------------------------
     Cutoff Histogram 
     set 10% to   0 close to 0 
     set 10% to 255 close to 255
     ----------------------------------------------------------------------------------------------*/
    public int[] setCutoff(int[] source, double percent) {

        percent = percent / 100;
        cutoff = (int) (totalPixels * percent);
        cutoffHigh = totalPixels - cutoff;

        int sum = 0;
        int[] pixel = new int[256];

        for (int key = 0; key < source.length; key++) {

            int value = source[key];

            sum += value;
            if (sum < cutoff) {

                pixel[key] = 0;
                first = key + 1;             //---------- set first ----------//

            } else if (sum > cutoffHigh) {

                if (last == 0) {
                    last = key - 1;          //---------- set last  ----------//
                }
                pixel[key] = 255;

            } else {
                pixel[key] = value;
            }
        }

        return pixel;

    }
    /*--------------------------------------------------------------------------------------------*/

    public int[] stretchMap(int first, int last) {

        int[] mapStretch = new int[256];

        double increment = (double) 253 / (last - first);    // 1.8540 = 254 / (188-51)     
        //System.out.format("--- Increment = %.4f ---\n", increment);

        for (int oldKey = first; oldKey <= last; oldKey++) {

            double newKey = ((oldKey - first) * increment) + 1;   // 16.75 = (60-51) * 1.8613
            mapStretch[oldKey] = (int) newKey;
        }
        return mapStretch;
    }
    /*--------------------------------------------------------------------------------------------*/

    public void WriteStretchedImage(int[][] img, int first, int last, int[] lookup, String filename) throws IOException {

        BufferedImage bi = new BufferedImage(img[0].length, img.length, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < bi.getHeight(); ++i) {
            for (int j = 0; j < bi.getWidth(); ++j) {

                int val = img[i][j];

                if (val < first) {
                    val = 0;
                } else if (val > last) {
                    val = 255;
                } else {
                    if (lookup.length > 0) {
                        val = lookup[val];
                    }
                }

                int pixel = (val << 16) | (val << 8) | (val);
                bi.setRGB(j, i, pixel);
            }
        }

        File outputfile = new File(filename);
        ImageIO.write(bi, "png", outputfile);

    }
    /*--------------------------------------------------------------------------------------------*/
}
