/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 *
 * PURPOSE: Edge detector
 *
 * ALGORITHM: Sobel's edge detector algorithm
 *
 * For full documentation, see read me
  ************************************************************************/

package edgedetector;

import grayscale.Grayscale;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ui.ImageViewer;
import util.Statistics;
import util.Threshold;


// TODO: better way of calculating threshold
// TODO: perhaps show grayscale edges (not just black and white -- weak edges, strong edges, etc.)
// TODO: maybe apply Gaussian filter first to reduce noise?


public class SobelEdgeDetector {

   /************************************************************************
    * Data structures
    ***********************************************************************/
   private boolean[][] edges;
   private int threshold;

   /***********************************************************************
    * Constructor
    ***********************************************************************/
   
   /**
    * All work is done in constructor.
    * @param image
    */
   public SobelEdgeDetector(int[][] image) {
      run(image);
   }

   /*************************************************************************************
    * Sobel's Edge Detector -- the algorithm itself
    ************************************************************************************/
   
   /**
    * Find beautiful edges.
    */
   private void run(int[][] image) {
      // Apply discrete convolutions with Sobel Kernels to obtain dimension-wise gradients
      double[][] x_kernel = SobelKernel.X;
      double[][] y_kernel = SobelKernel.Y;
      
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
            mag[i][j] = calcMagnitude(x_smoothed[i][j], y_smoothed[i][j]);
            
      // calculate threshold intensity to be edge
      threshold = calcThreshold(mag);
                  
      // TODO: 'edges' is now the same as the origin image.. is that OK?
      // threshold image to find edges
      edges = new boolean[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            edges[i][j] = mag[i][j] > threshold;
   }
   

   /**
    * L2norm: |G| = |G_x^2 + G_y^2|. 
    * @param x
    * @param y
    * @return
    */
   // TODO: offer L1norm option: |G| = |G_x| + |G_y|
   private int calcMagnitude(int x, int y) {
//      return Math.abs(x) + Math.abs(y); 
      return (int) Math.sqrt(x * x + y * y);
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
   // 3. (not MATLAB). can I try using Otsu's instead?
   private int calcThreshold(int[][] magnitude) {
      return Statistics.calcMean(magnitude);
   }

   /*************************************************************************************
    * Accessors
    ************************************************************************************/
   public boolean[][] getEdges() {
      return edges;
   }
   
   public int getThreshold() {
      return threshold;
   }
   
   /*************************************************************************************
    * Unit testing
    * @throws IOException 
    ************************************************************************************/
   
   public static void main(String[] args) throws IOException {
      // read image and get pixels
      String img = args[0];
      BufferedImage originalImage = ImageIO.read(new File(img));
      int[][] pixels = Grayscale.getGrayPixels(originalImage);
      
      // run SobelEdgeDetector
      final long startTime = System.currentTimeMillis();
      SobelEdgeDetector sed = new SobelEdgeDetector(pixels);
      final long endTime = System.currentTimeMillis();
      
      // print timing information
      final double elapsed = (double) (endTime - startTime) / 1000;
      System.out.println("Sobel Edge Detector took " + elapsed + " seconds.");
      System.out.println("Threshold = " + sed.threshold);
      
      // display edges
      boolean[][] edges = sed.getEdges();
      BufferedImage edges_image = Threshold.applyThresholdReversed(edges);
      BufferedImage[] toShow = {originalImage, edges_image};
      String title = "Sobel Edge Detector by Jason Altschuler";
      ImageViewer.showImages(toShow, title);
   }

}
