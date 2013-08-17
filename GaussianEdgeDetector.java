/**************************************************************************
 * @author Jason Altschuler
 * 
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 * 
 * Abstract parent class for all edge detectors that use the 1st derivative
 * (as opposed to the 2nd derivative, like Laplacian edge detectors) to 
 * find edges in images.
 * 
 ************************************************************************/


// TODO: better way of calculating threshold
// TODO: perhaps show grayscale edges (not just black & white; weak & strong edges)
// TODO: maybe apply Gaussian filter first to reduce noise?


package edgedetector;

import util.Distance;
import util.Statistics;

public abstract class GaussianEdgeDetector {

   /************************************************************************
    * Data structures
    ***********************************************************************/
   // dimensions are slightly smaller than original image because of discrete convolution
   protected boolean[][] edges;
   
   // threshold used to find edges; [i,j] is edge iff |G[i,j]| = |f'[i,j]| > threshold.
   protected int threshold;
   
   
   /************************************************************************
    * Abstract methods to implement 
    ***********************************************************************/
   protected abstract double[][] getXkernel();
   protected abstract double[][] getYkernel();
   
   
   /***********************************************************************
    * Detect edges
    ***********************************************************************/
   
   /**
    * Find beautiful edges.
    * @param image
    */
   protected void run(int[][] image) {
      // get kernels
      double[][] x_kernel = getXkernel();
      double[][] y_kernel = getYkernel();

      // apply convolutions with kernels
      GaussianSmoother x_gs = new GaussianSmoother(image, x_kernel);
      GaussianSmoother y_gs = new GaussianSmoother(image, y_kernel);

      // calculate magnitude of gradients
      int[][] x_smoothed = x_gs.getSmoothedImage();
      int[][] y_smoothed = y_gs.getSmoothedImage();

      int rows = x_smoothed.length;
      int columns = x_smoothed[0].length;

      int[][] mag = new int[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            // TODO: offer choice of L1 or L2 norms. 
            mag[i][j] = Distance.L1(x_smoothed[i][j], y_smoothed[i][j]);

      // calculate threshold intensity to be edge
      threshold = calcThreshold(mag);

      // threshold image to find edges
      edges = new boolean[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            edges[i][j] = mag[i][j] > threshold;
   }


   /**
    * Calculates threshold as the mean of the |G| matrix. 
    * @param image
    * @return
    */
   // TODO: MATLAB offers the following possibilities:
   // 1: user provides threshold
   // 2: user can provide scaling (does this method, but then just
   // multiplies by that factor to obtain threshold
   // TODO: also consider using Otsu's instead?
   private int calcThreshold(int[][] magnitude) {
      return Statistics.calcMean(magnitude);
   }
   

   /*********************************************************************
    * Accessors
    *********************************************************************/
   public boolean[][] getEdges() {
      return edges;
   }

   public int getThreshold() {
      return threshold;
   }
}
