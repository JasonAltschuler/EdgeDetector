/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags machine learning, computer vision, image analysis, edge detection, AI
 *
 * Provides convolution kernels for Gaussian image smoothing / blurring
 **************************************************************************/

package edgedetector.imagederivatives;

public class ConvolutionKernel {
   
   
   /**
    * Generates a 1D averaging kernel with user-defined dimensions
    */
   public static double[] averagingKernel(int r) {
     double[] kernel = new double[r];

     for (int i = 0; i < r; i++) 
       kernel[i] = 1;
     
     return kernel;
   } 


   /**
    * Generates a 2D averaging kernel with user-defined dimensions
    */
   public static double[][] averagingKernel(int r, int c) {
      double[][] kernel = new double[r][c];

      for (int i = 0; i < r; i++)
         for (int j = 0; j < c; j++)
            kernel[i][j] = 1;

      return kernel;
   }

}
