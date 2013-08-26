/****************************************************************
 * @author Jason Altschuler
 *
 * @tags Machine learning, computer vision, image processing, image
 * analysis, Fourier Transforms
 *
 * PURPOSE: Applies discrete Fourier transform (convolution kernel) to image.
 * 
 * Useful for image convolutions (also called applying image filters),
 * especially Gaussian smoothing (also called Gaussian blurring) of
 * an image, which reduces noise in an image, although it is also makes
 * edges less well-defined.
 * 
 * Essentially, makes new image with each pixel intensity a linear 
 * combination of the pixel intensities around it in the original image.
 ****************************************************************/

// TODO: use the following as a reference/resource
// http://homepages.inf.ed.ac.uk/rbf/HIPR2/convolve.htm

// TODO: reconcile this with ImageGradient?

package edgedetector.imagederivatives;

import edgedetector.util.GaussianKernel;

// TODO: rename stuff (e.g. smoothedImage data structure)
public class ImageConvolution {

   /****************************************************
    * Fields
    ***************************************************/
   private int[][] image;          // original image
   private double[][] kernel;      // Gaussian kernel
   private int[][] smoothedImage;  // final answer

   private int M;                  // # of rows in original image
   private int N;                  // # of columns in original image
   private int m;                  // # of rows in kernel
   private int n;                  // # of columns in kernel

   /****************************************************
    * Constructor
    ***************************************************/
   public ImageConvolution(int[][] image, double[][] kernel) {
      // set fields
      this.image = image;
      this.kernel = kernel;
      this.M = image.length;
      this.N = image[0].length;
      this.m = kernel.length;
      this.n = kernel[0].length;
      this.smoothedImage = new int[M - m + 1][N - n + 1];

      // convolve image with smoother
      convolve();
   }


   /****************************************************
    * Convolving
    ***************************************************/

   private void convolve() {
      double smoothed;
      for (int i = 0; i < smoothedImage.length; i++) {
         for (int j = 0; j < smoothedImage[0].length; j++) {
            // for each pixel in smoothedImage
            smoothed = 0;
            for (int k = 0; k < m; k++) {
               for (int l = 0; l < n; l++) {
                  smoothed += kernel[k][l] * image[i + k][j + l];
               }
            }
            // smoothedImage[i][j] = (smoothed < 255) ? (((int) smoothed > 0) ? smoothed : 0) : 255;
            if (smoothed < 255) {
               if (smoothed > 0)
                  smoothedImage[i][j] = (int) smoothed;
               else
                  smoothedImage[i][j] = 0;
            } else {
               smoothedImage[i][j] = 255;
            }
         }
      }
   }


   /****************************************************
    * Accessors
    ***************************************************/
   public int[][] getImageConvolution() {
      return smoothedImage;
   }


   /****************************************************
    * Unit testing
    ***************************************************/

   public static void main(String[] args) {
      // easy example test case
      int[][] image = {{1, 2, 3},
                       {4, 5, 6,},
                       {7, 8, 9}};

      double[][] kernel = GaussianKernel.averagingKernel(2, 2);

      // TODO: timing
      // run Gaussian smoother and get result
      ImageConvolution gs = new ImageConvolution(image, kernel);
      int[][] smoothedImage = gs.getImageConvolution();

      // print result for visualization
      for (int i = 0; i < smoothedImage.length; i++) {
         for (int j = 0; j < smoothedImage[0].length; j++) {
            System.out.print(smoothedImage[i][j] + "\t");
         }
         System.out.println();
      }

   }

}
