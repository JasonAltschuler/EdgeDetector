/**************************************************************************
 * @author Jason Altschuler
 *
 * @tags machine learning, computer vision, image analysis, edge detection, AI
 *
 * PURPOSE: Edge detector
 *
 * ALGORITHM: Canny's edge detector algorithm (also called the "Optimal Edge Detector")
 *
 * GOAL:
 *       1. Good detection (mark only real edges)
 *       2. Good localization (detected edges close to real edges)
 *       3. Minimal response (single edge mark, small false positive rate)
 *
 * STEPS:
 *       1. Gaussian smoothing / Gaussian blurring (for noise reduction)
 *       2. Calculate magnitude of gradient and edge angle for each pixel
 *       3. Non-maximum suppression (removes false edges)
 *       4. Hysteresis  ing (calculate two thresholds --> weak and strong edges)
 *       5. Edge tracing (keep strong edges and all weak edges connected to strong edges)
 **************************************************************************/

package edgedetector.detectors;


import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

import javax.imageio.ImageIO;

import kmeans.KMeans;
import ui.ImageViewer;
import util.CSVwriter;
import util.Hypotenuse;
import util.Threshold;
import edgedetector.imagederivatives.ConvolutionKernel;
import edgedetector.imagederivatives.ImageConvolution;
import edgedetector.util.NonMaximumSuppression;
import grayscale.Grayscale;


public class CannyEdgeDetector {
   
   /***********************************************************************
    * Static fields
    **********************************************************************/

   // convolution kernels to calculate discrete image gradient (Sobel operators; same as in SobelEdgeDetector.java)
   private static final double[][] X_KERNEL = {{-1, 0 ,  1},
                                               {-2, 0 ,  2},
                                               {-1, 0 ,  1}};
   private static final double[][] Y_KERNEL = {{1 , 2 ,  1}, 
                                               {0 , 0 ,  0},
                                               {-1, -2, -1}};
   

   /***********************************************************************
    * Non-static fields
    **********************************************************************/
   
   //=========================== PARAMETERS =============================/
   
   // true -> use L1 distance function. false -> use L2. L1 is less precise, but faster.
   private boolean L1norm;
   
   // TODO: provide option for user to define kernelRadius and then calculate appropriate Gaussian Kernel
   // radius of kernel for Gaussian smoothing. Bigger --> wider edges, smoother edges, more noise ignored
// private double kernelRadius;
   
   //======================= OPTIONAL PARAMETERS =========================/
   
   // false --> user provides high and low thresholds. true --> calculate thresholds automatically
   private boolean calcThreshold;
   
   // calculated and used in hysteresis: strong edges have gradient magnitudes above high threshold
   private int highThreshold;
   
   // calculated and used in hysteresis: weak edges have gradient magnitudes between low and high threshold
   private int lowThreshold;
   
   // minimum number of pixels for an edge to contain to be kept
   private int minEdgeSize;

   //============================== OUTPUT ===============================/

   // final answer: [i][j] is true iff pixel is part of edge
   private boolean[][] edges;
   
   // "strong" edges found by double thresholding in hysteresis step
   private boolean[][] strongEdges;
   
   // "weak" edges found by double thresholding in hysteresis step
   private boolean[][] weakEdges;
   
   // number of edge pixels
   private int numEdgePixels;
   
   // number of strong edge pixels
   private int numStrongEdgePixels;
   
   // number of weak edge pixels
   private int numWeakEdgePixels;
   
   // dimensions of edges[][]] image; slightly smaller than original image because of image convolution
   private int rows;
   private int columns;

   
   /***********************************************************************
    * Constructor
    **********************************************************************/
   
   /**
    * Empty constructor is private to ensure that clients have to use the 
    * Builder inner class to create a CannyEdgeDetector object.
    */
   private CannyEdgeDetector() {}
   
   /**
    * The proper way to construct a CannyEdgeDetector object: from an inner class object.
    * <P> All work is done in constructor.
    * @param builder
    */
   private CannyEdgeDetector(Builder builder) {
      // set user information from builder
      this.L1norm = builder.L1norm;
      this.minEdgeSize = builder.minEdgeSize;
      if (!(this.calcThreshold = builder.calcThreshold)) {
         this.lowThreshold = builder.lowThreshold;
         this.highThreshold = builder.highThreshold;
      }
      
      // run KMeans++ clustering algorithm
      findEdges(builder.image);
   }
   
   /**
    * Builder class for constructing KMeans objects.
    *
    * For descriptions of the fields in this (inner) class, see outer class
    */
   public static class Builder {
      
      //============================ FIELDS =============================//
      
      // required parameters
      private int[][] image;
      
      // optional parameters (default values given)
      private boolean calcThreshold = true;
      private int lowThreshold;
      private int highThreshold;
      private boolean L1norm = false;
      private int minEdgeSize = 0;
      
      
      //=========================== CONSTRUCTOR =========================//

      /**
       * Provide the required parameters.
       * @param image
       */
      public Builder(int[][] image) {
         this.image = image;
      }
      
      /**
       * Set high and low thresholds.
       * @param lowThreshold
       * @param highThreshold
       * @return
       */
      public Builder thresholds(int lowThreshold, int highThreshold) {
         if (lowThreshold > highThreshold || lowThreshold < 0 || highThreshold > 255)
            throw new IllegalArgumentException("Invalid threshold values");
         this.calcThreshold = false;
         this.lowThreshold = lowThreshold;
         this.highThreshold = highThreshold;
         return this;
      }
      
      /**
       * Set whether to use L1 or L2 norm.
       * @param L1norm
       * @return
       */
      public Builder L1norm(boolean L1norm) {
         this.L1norm = L1norm;
         return this;
      }
      
      /**
       * Set the minimum number of pixels an edge must contain to be kept.
       * @param minEdgeSize
       * @return
       */
      public Builder minEdgeSize(int minEdgeSize) {
         this.minEdgeSize = minEdgeSize = 0;
         return this;
      }
      
      /**
       * Builds a CannyEdgeDetector object.
       * @return
       */
      public CannyEdgeDetector build() {
         return new CannyEdgeDetector(this);
      }
   }

   /***********************************************************************
    * Canny's Edge Detection method -- the algorithm itself
    ***********************************************************************/

   /**
    * Canny's Edge Detection algorithm.
    * <P> Finds only the most beautiful edges.
    * @param image
    */
   private void findEdges(int[][] image) {
     
      //================== STEP 1: GAUSSIAN SMOOTHING ===================//

      // convolve image with Gaussian kernel
      ImageConvolution gaussianConvolution = new ImageConvolution(image, ConvolutionKernel.GAUSSIAN_KERNEL);
      int[][] smoothedImage = gaussianConvolution.getConvolvedImage();      
      
      
      //===================== STEP 2: IMAGE GRADIENT ====================//
      
      // apply convolutions to smoothed image
      ImageConvolution x_ic = new ImageConvolution(smoothedImage, X_KERNEL);
      ImageConvolution y_ic = new ImageConvolution(smoothedImage, Y_KERNEL);
      
      // calculate magnitude of gradients
      int[][] x_imageConvolution = x_ic.getConvolvedImage();
      int[][] y_imageConvolution = y_ic.getConvolvedImage();
      
      // note: image convolutions have slightly different dimensions that original image
      rows = x_imageConvolution.length;
      columns = x_imageConvolution[0].length;
      
      // calculate magnitude of gradient and tangent angle to edge
      int[][] mag = new int[rows][columns];
      NonMaximumSuppression.EdgeDirection[][] angle = new NonMaximumSuppression.EdgeDirection[rows][columns];
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++) {
            mag[i][j] = hypotenuse(x_imageConvolution[i][j], y_imageConvolution[i][j]);
            angle[i][j] = direction(x_imageConvolution[i][j], y_imageConvolution[i][j]);
         }
      
      
      //================ STEP 3: NON-MAXIMUM SUPPRESSION ================//

      edges = new boolean[rows][columns];
      weakEdges = new boolean[rows][columns];
      strongEdges = new boolean[rows][columns];
      
      // apply non-maximum suppression (suppress false edges)
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            if (NonMaximumSuppression.nonMaximumSuppression(mag, angle[i][j], i, j))
               mag[i][j] = 0;

      
      //======================= STEP 4: HYSTERESIS ======================//
      
      // calculate high and low thresholds if user did not provide
      if (calcThreshold) {
         // TODO: implement other automated hysteresis algorithms
         
         // run KMeans++ clustering with 3 clusters (because 2 thresholds) 
         // in 1 dimension using magnitudes of gradient
         int k = 3; 
         double[][] points = new double[rows * columns][1];
         int counter = 0;
         for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
               points[counter++][0] = mag[i][j];

         KMeans clustering = new KMeans.Builder(k, points)
                                       .iterations(10)
                                       .pp(true)
                                       .epsilon(.01)
                                       .useEpsilon(true)
                                       .build();
         double[][] centroids = clustering.getCentroids();

         boolean b = centroids[0][0] < centroids[1][0];
         lowThreshold = (int) (b ? centroids[0][0] : centroids[1][0]);
         highThreshold = (int) (b ? centroids[1][0] : centroids[0][0]);    
      }

      
      //====================== STEP 5: EDGE TRACING =====================//

      // data structures to help with DFS in edge tracing
      HashSet<Integer> strongSet = new HashSet<Integer>();
      HashSet<Integer> weakSet = new HashSet<Integer>();

      // find strong and weak edges
      int index = 0;
      numWeakEdgePixels = 0;
      numStrongEdgePixels = 0;
      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            if (mag[r][c] >= highThreshold) {
               strongSet.add(index);
               strongEdges[r][c] = true;
               numStrongEdgePixels++;
            } else if (mag[r][c] >= lowThreshold) {
               weakSet.add(index);
               weakEdges[r][c] = true;
               numWeakEdgePixels++;
            }
            index++;
         }
      }
      
      // if false --> not checked. if true --> checked
      boolean[][] marked = new boolean[rows][columns];
      Stack<Integer> toAdd = new Stack<Integer>();
      
      // depth-first search to track all contiguous edge segments, each consisting
      // of weak edge pixels and at least 1 strong edge pixel
      for (int strongIndex : strongSet) {
         dfs(ind2sub(strongIndex, columns)[0], ind2sub(strongIndex, columns)[1], weakSet, strongSet, marked, toAdd);
         
         if (toAdd.size() >= minEdgeSize)
            for (int edgeIndex : toAdd)
               edges[ind2sub(edgeIndex, columns)[0]][ind2sub(edgeIndex, columns)[1]] = true;
         
         toAdd.clear();
      }
   }
   
   
   /**
    * Depth-first-search for edge tracking.
    * @param r
    * @param c
    * @param weakSet HashSet of indices of all weak edges
    * @param strongSet HashSet of indices of all strong edges
    * @param marked boolean[][] to avoid double-checking pixels
    * @param toAdd possible edges (must still check edge contains >= minEdgeSize # of pixels)
    */
   private void dfs(int r, int c, HashSet<Integer> weakSet, HashSet<Integer> strongSet, boolean[][] marked, Stack<Integer> toAdd) {
      // check indices still in bounds and haven't already checked this point
      if (r < 0 || r >= rows || c < 0 || c >= columns || marked[r][c])
         return;

      // mark so that we don't come back
      marked[r][c] = true;
      
      int index = sub2ind(r, c, columns);
      if (weakSet.contains(index) || strongSet.contains(index)) {
         // mark as possible edge (must also have >= minEdgeSize # of pixels)
         toAdd.push(sub2ind(r, c, columns));
         
         // continue depth first search
         dfs(r - 1, c - 1, weakSet, strongSet, marked, toAdd);
         dfs(r - 1, c, weakSet, strongSet, marked, toAdd);
         dfs(r - 1, c + 1, weakSet, strongSet, marked, toAdd);
         dfs(r, c - 1, weakSet, strongSet, marked, toAdd);
         dfs(r, c + 1, weakSet, strongSet, marked, toAdd);
         dfs(r + 1, c - 1, weakSet, strongSet, marked, toAdd);
         dfs(r + 1, c, weakSet, strongSet, marked, toAdd);
         dfs(r + 1, c + 1, weakSet, strongSet, marked, toAdd);
      } 
   }

   
   /***********************************************************************
    * Helper methods
    ***********************************************************************/
   
   /**
    * Linear indexing to 2D indexing.
    * @param index
    * @param columns
    * @return
    */
   private static int[] ind2sub(int index, int columns) {
      return new int[] {index / columns, index - columns * (index / columns)};
   }
   
   /**
    * 2D indexing to linear indexing.
    * @param r
    * @param c
    * @param columns
    * @return
    */
   private static int sub2ind(int r, int c, int columns) {
      return columns * r + c;
   }
   
   /**
    * Approximates hypotenuse given two (integer) side lengths of right triangle.
    * @param x
    * @param y
    * @return
    */
   private int hypotenuse(int x, int y) {
      return (int) (L1norm ? Hypotenuse.L1(x, y) : Hypotenuse.L2(x, y));
   }
   
   /**
    * Finds angle tangent to edge direction given image gradient in x and y directions.
    * @param x
    * @param y
    * @return
    */
   private NonMaximumSuppression.EdgeDirection direction(int G_x, int G_y) {
      return NonMaximumSuppression.EdgeDirection.getDirection(G_x, G_y);
   }
   

   /***********************************************************************
    * Accessors
    ***********************************************************************/

   /**
    * @return convolution kernel used to calculate image gradient in x direction
    */
   public static double[][] getX_KERNEL() {
      return X_KERNEL;
   }


   /**
    * @return convolution kernel used to calculate image gradient in y direction
    */
   public static double[][] getyKernel() {
      return Y_KERNEL;
   }

   /**
    * @return whether using L1 or L2 norm to calculate distance
    */
   public boolean isL1norm() {
      return L1norm;
   }

   /**
    * @return high threshold used in hysteresis (double thresholding)
    */
   public int getHighThreshold() {
      return highThreshold;
   }

   /**
    * @return low threshold used in hysteresis (double thresholding)
    */
   public int getLowThreshold() {
      return lowThreshold;
   }

   /**
    * @return edges detected by Canny Edge Detector
    */
   public boolean[][] getEdges() {
      return edges;
   }

   /**
    * @return weak edges detected in hysteresis step
    */
   public boolean[][] getStrongEdges() {
      return strongEdges;
   }

   /**
    * @return strong edges detected in hysteresis step
    */
   public boolean[][] getWeakEdges() {
      return weakEdges;
   }

   /**
    * @return # of edge pixels detected by Canny Edge Detector
    */
   public int getNumEdgePixels() {
      return numEdgePixels;
   }

   /**
    * @return # of strong edge pixels detected by Canny Edge Detector
    */
   public int getStrongEdgePixels() {
      return numStrongEdgePixels;
   }

   /**
    * @return # of weak edge pixels detected by Canny Edge Detector
    */
   public int getWeakEdgePixels() {
      return numWeakEdgePixels;
   }
   
   /**
    * @return # of rows in edges image. (Slightly smaller than original image because of convolutions)
    */
   public int getRows() {
      return rows;
   }
   
   /**
    * @return # of columns in edges image. (Slightly smaller than original image because of convolutions)
    */
   public int getColumns() {
      return columns;
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
      CannyEdgeDetector canny = new CannyEdgeDetector.Builder(pixels)
                                                     .minEdgeSize(10)
                                                     .thresholds(5, 15)
                                                     .L1norm(false)
                                                     .build();
      final long endTime = System.currentTimeMillis();

      // print timing information
      final double elapsed = (double) (endTime - startTime) / 1000;
      System.out.println("Canny Edge Detector took " + elapsed + " seconds.");

      // display edges
      boolean[][] edges = canny.getEdges();
      boolean[][] weakEdges = canny.getWeakEdges();
      boolean[][] strongEdges = canny.getStrongEdges();

      BufferedImage cannyImage = Threshold.applyThresholdReversed(edges);
      BufferedImage strongweakImage = Threshold.applyThresholdWeakStrongCanny(weakEdges, strongEdges);
      BufferedImage edgesOriginalColor = Threshold.applyThresholdOriginal(edges, originalImage);

      BufferedImage[] toShow = {originalImage, strongweakImage, cannyImage, edgesOriginalColor};
      String title = "Canny Edge Detector by Jason Altschuler";
      ImageViewer.showImages(toShow, title, 2, 2);

      CSVwriter.write("canny", edges);
   }

}