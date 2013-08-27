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

import ui.ImageViewer;
import util.Threshold;
import edgedetector.imagederivatives.ConvolutionKernel;
import edgedetector.imagederivatives.ImageConvolution;
import grayscale.Grayscale;


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
    * All work is done in constructor.
    * @param filePath path to image
    */
   public LaplacianEdgeDetector(String filePath) {
      // read image and get pixels
      BufferedImage originalImage;
      try {
         originalImage = ImageIO.read(new File(filePath));
         findEdges(Grayscale.imgToGrayPixels(originalImage));
      } catch (IOException e) {
         e.printStackTrace();
      }
   }

   
   /**
    * Find beautiful edges.
    * <P> All work is done in constructor.
    * @param image
    */
   public LaplacianEdgeDetector(int[][] image) {
      findEdges(image);
   }

   
   /**
    * Finds only the most beautiful edges. 
    * @param image
    */
   private void findEdges(int[][] image) {
      // convolve image with Gaussian kernel
      ImageConvolution gaussianConvolution = new ImageConvolution(image, ConvolutionKernel.GAUSSIAN_KERNEL);
      int[][] smoothedImage = gaussianConvolution.getConvolvedImage();

      // apply convolutions to smoothed image
      ImageConvolution ic = new ImageConvolution(smoothedImage, kernel);

      // calculate magnitude of gradients
      int[][] convolvedImage = ic.getConvolvedImage();
      int rows = convolvedImage.length;
      int columns = convolvedImage[0].length;
      
      // calculate threshold intensity to be edge
      threshold = Threshold.calcThresholdEdges(convolvedImage);

      // threshold image to find edges
      edges = new boolean[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            edges[i][j] = Math.abs(convolvedImage[i][j]) == 0.0;
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
