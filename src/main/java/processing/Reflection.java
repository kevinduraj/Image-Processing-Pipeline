package processing;

public class Reflection {

    /*--------------------------------------------------------------------------------------------*/

    public int[][] convolve(int[][] iimage, int x_size, int y_size) {

        int i, j, lines, samples;
        int padded_lines, padded_samples;
        int half_x, half_y, oi, oj;
        int[][] oimage;

        lines = iimage.length;   //this.lines;
        samples = iimage[0].length; //this.samples;
        
        padded_lines = lines + y_size - 1;
        padded_samples = samples + x_size - 1;

        oimage = new int[padded_lines][padded_samples];

        half_x = x_size >> 1;
        half_y = y_size >> 1;

        // -- Fill interior of padded array with actual image. |
        for (i = 0; i < lines; ++i) {
            for (j = 0; j < samples; ++j) {
                oimage[i + half_y][j + half_x] = iimage[i][j];
            }
        }

        // -- Fill border of padded array with a mirror image of |
        //    the actual image reflected about the boundaries.   |
        // Left border 
        for (i = 0; i < lines; ++i) {
            for (j = 0, oj = half_x - 1; j < (int) half_x; ++j, --oj) {
                oimage[i + half_y][oj] = iimage[i][j];
            }
        }

        // Right border
        for (i = 0; i < lines; ++i) {
            for (j = samples - half_x, oj = samples + (2 * half_x) - 1; j < samples; ++j, --oj) {
                oimage[i + half_y][oj] = iimage[i][j];
            }
        }

        // Top border
        for (i = 0, oi = half_y - 1; i < (int) half_y; ++i, --oi) {
            for (j = 0; j < samples; ++j) {
                oimage[oi][j + half_x] = iimage[i][j];
            }
        }

        // Bottom border
        for (i = lines - half_y, oi = lines + (2 * half_y) - 1; i < lines; ++i, --oi) {
            for (j = 0; j < samples; ++j) {
                oimage[oi][j + half_x] = iimage[i][j];
            }
        }

        // Top left corner
        for (i = 0; i < (int) half_y; ++i) {
            for (j = 0, oj = half_x - 1; j < (int) half_x; ++j, --oj) {
                oimage[i][oj] = oimage[i][j + half_x];
            }
        }

        // Bottom right corner
        for (i = lines + half_y; i < lines + (2 * half_y); ++i) {
            for (j = samples, oj = samples + (2 * half_x) - 1; j < samples + half_x; ++j, --oj) {
                oimage[i][oj] = oimage[i][j];
            }
        }

        // Top right corner
        for (i = 0; i < (int) half_y; ++i) {
            for (j = samples, oj = samples + (2 * half_x) - 1; j < samples + half_x; ++j, --oj) {
                oimage[i][oj] = oimage[i][j];
            }
        }

        // Bottom left corner
        for (i = lines + half_y; i < lines + (2 * half_y); ++i) {
            for (j = 0, oj = half_x - 1; j < (int) half_x; ++j, --oj) {
                oimage[i][oj] = oimage[i][j + half_x];
            }
        }

        return oimage;
    }
    /*--------------------------------------------------------------------------------------------*/  
    
}