/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags Machine learning, computer vision, image processing, image
 * analysis, Fourier Transforms
 *
 * PURPOSE: Applies discrete 2D Fourier transform (convolution kernel) to image.
 * 
 * OVERVIEW: Writes each pixel intensity as linear combo of itself and its neighbors.
 * 
 * Useful for image convolutions (also called applying image filters),
 * especially Gaussian smoothing (also called Gaussian blurring) of
 * an image, which reduces noise in an image, although it is also makes
 * edges less well-defined.
 * 
 * Essentially, expresses each pixel intensity as a linear combination
 * of the pixel intensities around it.
 *************************************************************************/

package edgedetector.imagederivatives;

public class ImageConvolution {

   /***********************************************************************
    * Fields
    **********************************************************************/
   private int[][] image;          // original image
   private double[][] kernel;      // Gaussian kernel
   private int[][] convolvedImage;  // final answer

   private int M;                  // # of rows in original image
   private int N;                  // # of columns in original image
   private int m;                  // # of rows in kernel
   private int n;                  // # of columns in kernel

   
   /***********************************************************************
    * Constructor
    **********************************************************************/
   public ImageConvolution(int[][] image, double[][] kernel) {
      // set fields
      this.image = image;
      this.kernel = kernel;
      this.M = image.length;
      this.N = image[0].length;
      this.m = kernel.length;
      this.n = kernel[0].length;
      this.convolvedImage = new int[M - m + 1][N - n + 1];

      // convolve image with kernel
      convolve();
   }

   
   /***********************************************************************
    * Convolution
    **********************************************************************/

   /**
    * Discretized 2D Fourier Transform.
    * <P> Write each pixel intensity as linear combo of 
    * itself and its neighbors.
    */
   private void convolve() {
      double smoothed;
      for (int i = 0; i < convolvedImage.length; i++) {
         for (int j = 0; j < convolvedImage[0].length; j++) {
            smoothed = 0;
            for (int k = 0; k < m; k++)
               for (int l = 0; l < n; l++)
                  smoothed += kernel[k][l] * image[i + k][j + l];

            // round off if not between 0 and 255, inclusive
            convolvedImage[i][j] = (smoothed > 255) ? 255 : (smoothed < 0) ? 0 : (int) smoothed; 
         }
      }
   }

   
   /***********************************************************************
    * Accessors
    **********************************************************************/
   
   /**
    * @return convolvedImage
    */
   public int[][] getConvolvedImage() {
      return convolvedImage;
   }

   /**
    * @return original image
    */
   public int[][] getImage() {
      return image;
   }

   /**
    * @return convolution kernel
    */
   public double[][] getKernel() {
      return kernel;
   }

   /**
    * @return # of rows in original image
    */
   public int getM() {
      return M;
   }

   /**
    * @return # of columns in original image
    */
   public int getN() {
      return N;
   }

   /**
    * @return # of rows in convolution kernel
    */
   public int getm() {
      return m;
   }

   /**
    * @return # of columns in convolution kernel
    */
   public int getn() {
      return n;
   }
}
