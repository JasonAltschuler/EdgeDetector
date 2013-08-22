/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 *
 * PURPOSE: Edge detector
 *
 * ALGORITHM: Sobel edge detector algorithm
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


public class SobelEdgeDetector extends GaussianEdgeDetector {
   
   /*********************************************************************
    * Convolution kernels
    *********************************************************************/
   private final static double[][] X_kernel = {{-1, 0, 1},
                                               {-2, 0, 2},
                                               {-1, 0, 1}};

   private final static double[][] Y_kernel = {{1, 2, 1}, 
                                               {0, 0, 0},
                                               {-1, -2, -1}};

   /*********************************************************************
    * Implemented abstract methods
    *********************************************************************/

   /**
    * @Override
    * {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}}
    */
   public double[][] getXkernel() {
      return SobelEdgeDetector.X_kernel;
   }
   
   /**
    * @Override
    * {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}}
    */
   public double[][] getYkernel() {
      return SobelEdgeDetector.Y_kernel;
   }

   
   /*********************************************************************
    * Constructor 
    **********************************************************************/
   
   /**
    * All work is done in constructor.
    * @param filePath path to image
    */
   public SobelEdgeDetector(String filePath) {
      // read image and get pixels
      BufferedImage originalImage;
      try {
         originalImage = ImageIO.read(new File(filePath));
         run(Grayscale.imgToGrayPixels(originalImage));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }
   
   /**
    * All work is done in constructor.
    * @param image
    */
   public SobelEdgeDetector(int[][] image) {
      run(image);
   }


   /*********************************************************************
    * Unit testing
    * @throws IOException 
    *********************************************************************/

   // TODO: refactor; make this a generic method
   
   public static void main(String[] args) throws IOException {
      // read image and get pixels
      String img = args[0];
      BufferedImage originalImage = ImageIO.read(new File(img));
      int[][] pixels = Grayscale.imgToGrayPixels(originalImage);

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
