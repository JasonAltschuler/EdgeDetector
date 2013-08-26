package edgedetector.util;
// TODO: header
//
// TODO: package?
//
// TODO: add more kernels? Esp. 1D and 2D gaussian kernels

// A collection of kernels to use

public class GaussianKernel {

 // TODO: use this: http://rsbweb.nih.gov/ij/plugins/canny/Canny_Edge_Detector.java
// private void computeGradients(float kernelRadius, int kernelWidth) {....}
   
//   public static double[][] Gaussian2DKernel(int r, int c, double sigma) {
//
//   }



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
