/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags machine learning, computer vision, image analysis, edge detection, AI
 *
 * Provides convolution kernels for Gaussian image smoothing / blurring
 **************************************************************************/

package edgedetector.imagederivatives;

public class ConvolutionKernel {

   // TODO: implement way to calculate generic Gaussian Kernel depending on kernel width and size
   // convolution kernel for Gaussian smoothing / blurring (kernel width (sigma) = 1.4, kernel size = 5)
   public static final double[][] GAUSSIAN_KERNEL = {{2/159.0, 4/159.0 , 5/159.0 , 4/159.0 , 2/159.0},
                                                     {4/159.0, 9/159.0 , 12/159.0, 9/159.0 , 4/159.0}, 
                                                     {5/159.0, 12/159.0, 15/159.0, 12/159.0, 5/159.0}, 
                                                     {4/159.0, 9/159.0 , 12/159.0, 9/159.0 , 4/159.0}, 
                                                     {2/159.0, 4/159.0 , 5/159.0 , 4/159.0 , 2/159.0}};
   
   /**
    * Generates a 1D averaging kernel with user-defined dimensions
    */
   public static double[] averagingKernel(int r) {
      double[] kernel = new double[r];
      double entry = 1.0 / r; 

         for (int i = 0; i < r; i++) 
            kernel[i] = entry;

      return kernel;
   } 


   /**
    * Generates a 2D averaging kernel with user-defined dimensions
    */
   public static double[][] averagingKernel(int r, int c) {
      double[][] kernel = new double[r][c];
      double entry = 1.0 / (r * c);

      for (int i = 0; i < r; i++)
         for (int j = 0; j < c; j++)
            kernel[i][j] = entry;

      return kernel;
   }

}
