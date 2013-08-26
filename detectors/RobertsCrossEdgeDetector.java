package edgedetector.detectors;

import grayscale.Grayscale;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import ui.ImageViewer;
import util.Threshold;


public class RobertsCrossEdgeDetector extends GaussianEdgeDetector {
   
   /*********************************************************************
    * Convolution kernels
    *********************************************************************/
   private final static double[][] X_kernel = {{1, 0},
                                               {0, -1}};

   private final static double[][] Y_kernel = {{0, -1},
                                               {1, 0}};

   /*********************************************************************
    * Implemented abstract methods
    *********************************************************************/

   /**
    * @Override
    * {{1, 0}, {0, -1}}
    */
   public double[][] getXkernel() {
      return RobertsCrossEdgeDetector.X_kernel;
   }
   
   /**
    * @Override
    * {{0, -1}, {1, 0}}
    */
   public double[][] getYkernel() {
      return RobertsCrossEdgeDetector.Y_kernel;
   }

   
   /*********************************************************************
    * Constructor 
    **********************************************************************/
   
   /**
    * All work is done in constructor.
    * <P> Uses L2 norm by default.
    * @param image
    */
   public RobertsCrossEdgeDetector(int[][] image) {
      findEdges(image, false);
   }
   
   /**
    * All work is done in constructor. 
    * <P> Gives option to use L1 or L2 norm.
    */
   public RobertsCrossEdgeDetector(int[][] image, boolean L1norm) {
      findEdges(image, L1norm);
   }
   


   /*********************************************************************
    * Unit testing
    * @throws IOException 
    *********************************************************************/

   public static void main(String[] args) throws IOException {
      // read image and get pixels
      String img = args[0];
      BufferedImage originalImage = ImageIO.read(new File(img));
      int[][] pixels = Grayscale.imgToGrayPixels(originalImage);

      // run SobelEdgeDetector
      final long startTime = System.currentTimeMillis();
      RobertsCrossEdgeDetector sed = new RobertsCrossEdgeDetector(pixels);
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
