/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 *
 * PURPOSE: Edge detector
 *
 * ALGORITHM: Laplacian edge detector algorithm
 * 
 * Finds edges by finding pixel intensities where the 2nd derivative
 * (discrete image derivative found by image convolutions) is 0. However, 
 * this method historically has been replaced by Sobel, Canny, etc. because 
 * it finds many false edges. The reason is that 2nd derivative could mean
 * a local min or max of first derivative. We only want the max's;
 * the mins are false edges.
 *
 * For full documentation, see the README
  ************************************************************************/

package edgedetector.detectors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import edgedetector.imagederivatives.ImageConvolution;
import grayscale.Grayscale;
import ui.ImageViewer;
import util.Threshold;


public class LaplacianEdgeDetector {
   
   /************************************************************************
    * Data structures
    ***********************************************************************/
   
   // dimensions are slightly smaller than original image because of discrete convolution.
   private boolean[][] edges;
   
   // threshold used to find edges; one requirement for [i,j] to be edge is |G[i,j]| = |f'[i,j]| > threshold.
   private int threshold;
   
   // convolution kernel; discretized appromixation of 2nd derivative
   private double[][] kernel = {{-1, -1, -1},
                                {-1, 8, -1},
                                {-1, -1, -1}};
   
   
   /***********************************************************************
    * Detect edges
    ***********************************************************************/
   
   /**
    * Find beautiful edges.
    * <P> All work is done in constructor.
    * @param image
    */
   public LaplacianEdgeDetector(int[][] image) {
      // TODO: don't hardcode here
      double[][] gaussianKernel = {{2, 4, 5, 4, 2},  // this is the kernel from the Wikipedia page on Canny Edge Detection
                                   {4, 9, 12, 9, 4}, 
                                   {5, 12, 15, 12, 5}, 
                                   {4, 9, 12, 9, 4}, 
                                   {2, 4, 5, 4, 2}};
      for (int i = 0; i < 5; i++) 
         for (int j = 0; j < 5; j++)
            gaussianKernel[i][j] /= 159.0;
            
      ImageConvolution gaussianConvolution = new ImageConvolution(image, gaussianKernel);
      int[][] smoothedImage = gaussianConvolution.getConvolvedImage();
      
      
      // apply convolutions to original image
      ImageConvolution ic = new ImageConvolution(smoothedImage, kernel);

      // calculate magnitude of gradients
      int[][] smoothed = ic.getConvolvedImage();
      int rows = smoothed.length;
      int columns = smoothed[0].length;
      
      // calculate threshold intensity to be edge
      threshold = Threshold.calcThresholdEdges(smoothed);

      // threshold image to find edges
      edges = new boolean[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            edges[i][j] = Math.abs(smoothed[i][j]) == 0.0;
        
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
   
   
   /*********************************************************************
    * Unit testing and display
    *********************************************************************/
   
   /**
    * 
    * @param imageFile
    * @throws IOException
    */
   public static void main(String[] args) throws IOException {
      // read image and get pixels
      String imageFile = args[0];
      BufferedImage originalImage = ImageIO.read(new File(imageFile));
      int[][] pixels = Grayscale.imgToGrayPixels(originalImage);

      // run Laplacian edge detector
      LaplacianEdgeDetector led = new LaplacianEdgeDetector(pixels);
      
      // get edges
      boolean[][] edges = led.getEdges();

      // make images out of edges
      BufferedImage laplaceImage = Threshold.applyThresholdReversed(edges);

      // display edges
      BufferedImage[] toShow = {originalImage, laplaceImage};
      String title = "Laplace Edge Detection by Jason Altschuler";
      ImageViewer.showImages(toShow, title);
   }
}
