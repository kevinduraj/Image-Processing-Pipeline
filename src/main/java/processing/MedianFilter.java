package processing;

public class MedianFilter {

    int nrows, ncols;
    int[][] inImage; 
    int[][] outImage;

    /*--------------------------------------------------------------------------------------------*/
    public int[][] process(int[][] source) {
        
        this.inImage = source;
        nrows = source.length; 
        ncols = source[0].length;
               
        outImage = new int[nrows][ncols];

        //--- Browse Source Image Matrix----//
        for (int i = 0; i < nrows; i++) {
            for (int j = 0; j < ncols; j++) {
                outImage[i][j] = median(i, j);
            }
        }
        
        return outImage;
    }
    /*--------------------------------------------------------------------------------------------*/
    private int median(int i, int j) {
        int m, n, count, t[], tmp;

        //--- Browse Kernel Matrix ---//
        t = new int[9];
        for (m = i - 1, count = 0; m <= i + 1; m++) {
            for (n = j - 1; n <= j + 1; n++) {
                if (m >= 0 && m < nrows && n >= 0 && n < ncols) {
                    t[count++] = inImage[m][n];
                }
            }
        }

        //--- Apply Bubble Sort ---//
        for (m = 0; m < count - 1; m++) {
            for (n = m + 1; n < count; n++) {
                if (t[m] < t[n]) {
                    tmp = t[m];
                    t[m] = t[n];
                    t[n] = tmp;
                }
            }
        }
        return t[count / 2];
    }
    /*--------------------------------------------------------------------------------------------*/    
} 