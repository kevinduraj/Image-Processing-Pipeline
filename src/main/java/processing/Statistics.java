package processing;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.imageio.ImageIO;

public class Statistics {

    List<Double> data = new ArrayList<Double>();
    double size;
    String filename;

    /*--------------------------------------------------------------------------------------------*/    
    public Statistics(String filename) {
        this.filename = filename;       
        this.readGreen();
    }
    /*--------------------------------------------------------------------------------------------*/
    public int[][] readGreen() {

        try {

            File infile = new File(filename);
            BufferedImage bi = ImageIO.read(infile);

            int red[][] = new int[bi.getHeight()][bi.getWidth()];
            int grn[][] = new int[bi.getHeight()][bi.getWidth()];
            int blu[][] = new int[bi.getHeight()][bi.getWidth()];

            for (int i = 0; i < grn.length; ++i) {
                for (int j = 0; j < grn[i].length; ++j) {                                      
                    red[i][j] = bi.getRGB(j, i) >> 16 & 0xFF;
                    grn[i][j] = bi.getRGB(j, i) >> 8 & 0xFF;
                    blu[i][j] = bi.getRGB(j, i) & 0xFF;
                    data.add((double)grn[i][j]);
                }
            }
            size = data.size();            
            return grn;

        } catch (IOException e) {
            System.out.println("image I/O error");
            return null;
        }
    }    
 
    /*----------------------------------------------------------------------*/
    double getMean() {
        
        double sum = 0.0;
        for (double a : data) {
            sum += a;
        }
        return sum / size;
    }

    /*----------------------------------------------------------------------*/
    double getVariance() {
        
        double mean = getMean();
        double temp = 0;
        for (double a : data) {
            temp += (mean - a) * (mean - a);
        }
        return temp / size;
    }
    /*----------------------------------------------------------------------*/
    int getMin() {
        
        double min = 255;
        for (double a : data) {
            if(a < min) min=a;
        }
        return (int) min;
    }
    /*----------------------------------------------------------------------*/
    int getMax() {
        
        double max = 0;
        for (double a : data) {
            if(a > max) max=a;
        }
        return (int) max;
    }    
    /*----------------------------------------------------------------------*/
    double getStdDev() {
        return Math.sqrt(getVariance());
    }

    /*----------------------------------------------------------------------*/
    public double median() {
        double[] b = new double[data.size()];
        System.arraycopy(data, 0, b, 0, b.length);
        Arrays.sort(b);

        if (data.size() % 2 == 0) {
            return (b[(b.length / 2) - 1] + b[b.length / 2]) / 2.0;
        } else {
            return b[b.length / 2];
        }
    }
    /*----------------------------------------------------------------------*/
    public void display() {
        System.out.format("\nMean     : %.3f", getMean());
        System.out.format("\nVariance : %.3f", getVariance());
        System.out.format("\nStdDev   : %.3f", getStdDev());
               
        System.out.println("\n------------------------");
        System.out.format("Max      : %3d", getMax());
        System.out.format("\nMin      : %3d\n", getMin()); 
        System.out.println("------------------------");        
    }
    /*----------------------------------------------------------------------*/
}