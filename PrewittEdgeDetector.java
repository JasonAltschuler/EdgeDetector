/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 *
 * PURPOSE: Edge detector
 *
 * ALGORITHM: Prewitt edge detector algorithm
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


public class PrewittEdgeDetector extends GaussianEdgeDetector {
   
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
      return PrewittEdgeDetector.X_kernel;
   }
   
   /**
    * @Override
    * {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}}
    */
   public double[][] getYkernel() {
      return PrewittEdgeDetector.Y_kernel;
   }

   
   /*********************************************************************
    * Constructor 
    **********************************************************************/
   
   /**
    * All work is done in constructor.
    * @param image
    */
   public PrewittEdgeDetector(int[][] image) {
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
      PrewittEdgeDetector sed = new PrewittEdgeDetector(pixels);
      final long endTime = System.currentTimeMillis();

      // print timing information
      final double elapsed = (double) (endTime - startTime) / 1000;
      System.out.println("Prewitt Edge Detector took " + elapsed + " seconds.");
      System.out.println("Threshold = " + sed.threshold);

      // display edges
      boolean[][] edges = sed.getEdges();
      BufferedImage edges_image = Threshold.applyThresholdReversed(edges);
      BufferedImage[] toShow = {originalImage, edges_image};
      String title = "Prewitt Edge Detector by Jason Altschuler";
      ImageViewer.showImages(toShow, title);
   }

}
