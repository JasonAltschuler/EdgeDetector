/**************************************************************************
 * @author Jason Altschuler
 * 
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 * 
 * Abstract parent class for PrewittEdgeDetector.java, SobelEdgeDetector.java,
 * and RobertsCrossEdgeDetector.java
 ************************************************************************/

package edgedetector.detectors;

import edgedetector.imagederivatives.ImageConvolution;
import edgedetector.util.NonMaximumSuppression;
import util.Hypotenuse;
import util.Threshold;

public abstract class GaussianEdgeDetector {

   /************************************************************************
    * Data structures
    ***********************************************************************/
   // dimensions are slightly smaller than original image because of discrete convolution.
   protected boolean[][] edges;
   
   // threshold used to find edges; one requirement for [i,j] to be edge is |G[i,j]| = |f'[i,j]| > threshold.
   protected int threshold;
   
   // true --> use L1 norm. false --> use L2. L1 is less precise, but faster.
   protected boolean L1norm;
   
   
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
   protected void findEdges(int[][] image, boolean L1norm) {
      // get convolution kernels
      double[][] x_kernel = getXkernel();
      double[][] y_kernel = getYkernel();

      // apply convolutions to original image
      ImageConvolution x_ic = new ImageConvolution(image, x_kernel);
      ImageConvolution y_ic = new ImageConvolution(image, y_kernel);

      // calculate magnitude of gradients
      int[][] x_imageConvolution = x_ic.getConvolvedImage();
      int[][] y_imageConvolution = y_ic.getConvolvedImage();

      // note that smoothed image have slightly different dimensions than original image (because image convolution)
      int rows = x_imageConvolution.length;
      int columns = x_imageConvolution[0].length;

      // calculate magnitude of gradient for each pixel, and angle of edge direction
      int[][] mag = new int[rows][columns];
      NonMaximumSuppression.EdgeDirection[][] angle = new NonMaximumSuppression.EdgeDirection[rows][columns];
      for (int i = 0; i < rows; i++) {
         for (int j = 0; j < columns; j++) {
            mag[i][j] = (int) (L1norm ? Hypotenuse.L1(x_imageConvolution[i][j], y_imageConvolution[i][j]) :
                                        Hypotenuse.L2(x_imageConvolution[i][j], y_imageConvolution[i][j]));
            angle[i][j] = NonMaximumSuppression.EdgeDirection.getDirection(x_imageConvolution[i][j],
                                                                           y_imageConvolution[i][j]);
         }
      }

      // apply threshold and non-maximum suppression 
      edges = new boolean[rows][columns];
      threshold = Threshold.calcThresholdEdges(mag);
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            edges[i][j] = (mag[i][j] < threshold) ? false : NonMaximumSuppression.nonMaximumSuppression(mag, angle[i][j], i , j);
   }
  


   /*********************************************************************
    * Accessors
    *********************************************************************/
   
   /**
    * @return detected edges
    */
   public boolean[][] getEdges() {
      return edges;
   }

   /**
    * @return threshold compared with sgradient magnitudes to find edges
    */
   public int getThreshold() {
      return threshold;
   }
   
   /**
    * @return whether used L1 or L2 distance norm
    */
   public boolean getL1norm() {
      return L1norm;
   }
}
