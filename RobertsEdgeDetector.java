
//TODO: same as SobelEdgeDetector, but uses different gradient operator
//SEE : page 11 in http://www.cse.unr.edu/~bebis/CS791E/Notes/EdgeDetection.pdf

/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 *
 * PURPOSE: Edge detector
 *
 * ALGORITHM: Roberts edge detector algorithm
 *
 * For full documentation, see the README
  ************************************************************************/

package edgedetector;

import grayscale.Grayscale;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ui.ImageViewer;
import util.Threshold;


public class RobertsEdgeDetector extends GaussianEdgeDetector {
   
   /*********************************************************************
    * Convolution kernels
    *********************************************************************/
   private final static double[][] X_kernel = {{1, 0}, {0, -1}};

   private final static double[][] Y_kernel = {{0, 1}, {-1, 0}};

   /*********************************************************************
    * Implemented abstract methods
    *********************************************************************/

   /**
    * @Override
    * {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
    */
   public double[][] getXkernel() {
      return RobertsEdgeDetector.X_kernel;
   }
   
   /**
    * @Override
    * {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}}
    */
   public double[][] getYkernel() {
      return RobertsEdgeDetector.Y_kernel;
   }

   
   /*********************************************************************
    * Constructor 
    **********************************************************************/
   
   /**
    * All work is done in constructor.
    * @param image
    */
   public RobertsEdgeDetector(int[][] image) {
      run(image);
   }


   /*********************************************************************
    * Unit testing
    * @throws IOException 
    *********************************************************************/

   public static void main(String[] args) throws IOException {
      // read image and get pixels
      String img = args[0];
      BufferedImage originalImage = ImageIO.read(new File(img));
      int[][] pixels = Grayscale.getGrayPixels(originalImage);

      // run SobelEdgeDetector
      final long startTime = System.currentTimeMillis();
      RobertsEdgeDetector sed = new RobertsEdgeDetector(pixels);
      final long endTime = System.currentTimeMillis();

      // print timing information
      final double elapsed = (double) (endTime - startTime) / 1000;
      System.out.println("Roberts Edge Detector took " + elapsed + " seconds.");
      System.out.println("Threshold = " + sed.threshold);

      // display edges
      boolean[][] edges = sed.getEdges();
      BufferedImage edges_image = Threshold.applyThresholdReversed(edges);
      BufferedImage[] toShow = {originalImage, edges_image};
      String title = "Roberts Edge Detector by Jason Altschuler";
      ImageViewer.showImages(toShow, title);
   }

}
