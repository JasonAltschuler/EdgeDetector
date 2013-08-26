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
 *       4. Hysteresis thresholding (calculate two thresholds --> weak and strong edges)
 *       5. Edge tracing (keep strong edges and all weak edges connected to strong edges)
 **************************************************************************/

package edgedetector.detectors;

// TODO: use this as a reference / resource:
// http://www.cse.iitd.ernet.in/~pkalra/csl783/canny.pdf
// http://dasl.mem.drexel.edu/alumni/bGreen/www.pages.drexel.edu/_weg22/can_tut.html

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Stack;

import javax.imageio.ImageIO;

import kmeans.KMeans;
import edgedetector.imagederivatives.ImageConvolution;
import edgedetector.util.NonMaximumSuppression;
import grayscale.Grayscale;
import ui.ImageViewer;
import util.CSVwriter;
import util.Hypotenuse;
import util.Threshold;

public class CannyEdgeDetector {
   
   /***********************************************************************
    * Static fields
    **********************************************************************/

   // convolution kernels to calculate discrete image gradient (Sobel operators; same as in SobelEdgeDetector.java)
   private static final double[][] X_KERNEL = {{-1, 0, 1},
                                               {-2, 0, 2},
                                               {-1, 0, 1}};
   private static final double[][] Y_KERNEL = {{1, 2, 1}, 
                                               {0, 0, 0},
                                               {-1, -2, -1}};

   /***********************************************************************
    * Non-static data structures
    **********************************************************************/
   
   //=========================== PARAMETERS =============================/
   // radius of kernel for Gaussian smoothing. Bigger --> _______________
   private double kernelRadius;
   
   // true -> use L1 distance function. false -> use L2. L1 is less precise, but faster.
   private boolean L1norm;
   
   //======================= OPTIONAL PARAMETERS =========================/
   
   // calculated and used in hysteresis: strong edges have gradient magnitudes above high threshold
   private int highThreshold;
   
   // calculated and used in hysteresis: weak edges have gradient magnitudes between low and high threshold
   private int lowThreshold;

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
   private int strongEdgePixels;
   
   // number of weak edge pixels
   private int weakEdgePixels;
 

   
   /***********************************************************************
    * Constructor
    **********************************************************************/
   
   // TODO: builder class?

   public CannyEdgeDetector(int[][] image, double kernelRadius) {      
      this.kernelRadius = kernelRadius;

      run(image);
   }


   /***********************************************************************
    * Canny's Edge Detection method -- the algorithm itself
    ***********************************************************************/

   /**
    * Canny's Edge Detection algorithm
    * @param image
    */
   private void run(int[][] image) {
     
      //================== STEP 1: GAUSSIAN SMOOTHING ====================/

      // TODO: implement this
//      int kernel_rows = 5;
//      int kernel_columns = 5;
//      double[][] gaussianKernel = GaussianKernel.generate(kernelWidth); 
      double[][] gaussianKernel = {{2, 4, 5, 4, 2},  // this is the kernel from the Wikipedia page on Canny Edge Detection
                                   {4, 9, 12, 9, 4}, 
                                   {5, 12, 15, 12, 5}, 
                                   {4, 9, 12, 9, 4}, 
                                   {2, 4, 5, 4, 2}};
      for (int i = 0; i < 5; i++)
         for (int j = 0; j < 5; j++)
            gaussianKernel[i][j] /= 159.0;
      
      
      
      
      
      
      ImageConvolution gaussianConvolution = new ImageConvolution(image, gaussianKernel);
      int[][] smoothedImage = gaussianConvolution.getImageConvolution();
      
      
      //===================== STEP 2: IMAGE GRADIENT =====================/
      
      // apply convolutions to smoothed image
      ImageConvolution x_ic = new ImageConvolution(smoothedImage, X_KERNEL);
      ImageConvolution y_ic = new ImageConvolution(smoothedImage, Y_KERNEL);
      
      // calculate magnitude of gradients
      int[][] x_imageConvolution = x_ic.getImageConvolution();
      int[][] y_imageConvolution = y_ic.getImageConvolution();
      
      // note that the image convolutions have slightly different dimensions that original image
      int rows = x_imageConvolution.length;
      int columns = x_imageConvolution[0].length;
      
      // calculate magnitude of gradient and tangent angle to edge
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
      
      
      //================ STEP 3: NON-MAXIMUM SUPPRESSION =================/

      edges = new boolean[rows][columns];
      weakEdges = new boolean[rows][columns];
      strongEdges = new boolean[rows][columns];
      
      // apply non-maximum suppression (suppress false edges)
      for (int i = 0; i < rows; i++)
         for (int j = 0; j < columns; j++)
            if (NonMaximumSuppression.nonMaximumSuppression(mag, angle[i][j], i, j))
               mag[i][j] = 0;

      
      //======================= STEP 4: HYSTERESIS =======================/

      // calculate two thresholds: high and low
      
      // TODO: only do if thresholds not given by user
      // TODO: other methods (see image analysis book)
      
      // run KMeans++ clustering with 3 clusters in 1 dimensions using magnitudes of gradient
//      int k = 3; // two thresholds --> three clusters
//      double[][] points = new double[rows * columns][1];
//      int counter = 0;
//      for (int i = 0; i < rows; i++)
//         for (int j = 0; j < columns; j++)
//            points[counter++][0] = mag[i][j];
//      
//      KMeans clustering = new KMeans.Builder(k, points)
//                                    .iterations(20)
//                                    .pp(true)
//                                    .epsilon(.001)
//                                    .useEpsilon(true)
//                                    .build();
//      double[][] centroids = clustering.getCentroids();
//      
//      boolean b = centroids[0][0] < centroids[1][0];
//      
//      lowThreshold = (int) (b ? centroids[0][0] : centroids[1][0]);
//      highThreshold = (int) (b ? centroids[1][0] : centroids[0][0]);    

      
      lowThreshold = 10;
      highThreshold = 20;
      
      // delete later
      System.out.println("Low threshold = " + lowThreshold);
      System.out.println("High threshold = " + highThreshold);
      System.out.println();
      
      
     
      

      //====================== STEP 5: EDGE TRACING ======================/
      
      // data structures to help with DFS in edge tracing
      HashSet<Integer> strongSet = new HashSet<Integer>();
      HashSet<Integer> weakSet = new HashSet<Integer>();
      
      // find strong and weak edges
      int counter = 0;
      weakEdgePixels = 0;
      strongEdgePixels = 0;
      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            if (mag[r][c] >= highThreshold) {
               strongSet.add(counter);
               strongEdges[r][c] = true;
               strongEdgePixels++;
            }
            else if (mag[r][c] >= lowThreshold) {
               weakSet.add(counter);
               weakEdges[r][c] = true;
               weakEdgePixels++;
            }
            counter++;
         }
      }
      
      // delete later
      System.out.println("# of total pixels = " + (rows * columns));
      System.out.println("# of strong edges = " + strongSet.size());
      System.out.println("# of weak edges = " + weakSet.size());
      
      
      // if false --> not checked. if true --> checked
      boolean[][] checked = new boolean[rows][columns];
      
      for (int index : strongSet) {
         int[] indices = ind2sub(index, columns);
         int r = indices[0];
         int c = indices[1];
         dfs(r, c, weakSet, strongSet, rows, columns, checked);
      }
      
      // delete later
      System.out.println();
      System.out.println("# of edges (at end) = " + numEdgePixels);
   }
   
   
   /**
    * Depth-first-search for edge tracking.
    * @param r
    * @param c
    * @param weakEdges
    * @param strongEdges
    * @param edge_stack
    * @param rows
    * @param columns
    * @param checked
    */
   private void dfs(int r, int c, HashSet<Integer> weakEdges, HashSet<Integer> strongEdges, int rows, int columns, boolean[][] checked) {
      // check indices still in bounds and haven't already checked this point
      if (r < 0 || r >= rows || c < 0 || c >= columns || checked[r][c])
         return;

      // mark so that we don't come back
      checked[r][c] = true;
      
      int index = sub2ind(r, c, columns);
      if (weakEdges.contains(index) || strongEdges.contains(index)) {
         // mark as edge if connected to weak or strong edge
         edges[r][c] = true;
         
         // continue depth first search
         dfs(r - 1, c - 1, weakEdges, strongEdges, rows, columns, checked);
         dfs(r - 1, c, weakEdges, strongEdges, rows, columns, checked);
         dfs(r - 1, c + 1, weakEdges, strongEdges, rows, columns, checked);
         dfs(r, c - 1, weakEdges, strongEdges, rows, columns, checked);
         dfs(r, c + 1, weakEdges, strongEdges, rows, columns, checked);
         dfs(r + 1, c - 1, weakEdges, strongEdges, rows, columns, checked);
         dfs(r + 1, c, weakEdges, strongEdges, rows, columns, checked);
         dfs(r + 1, c + 1, weakEdges, strongEdges, rows, columns, checked);
      } 
   }

   /***********************************************************************
    * Indexing helper functions
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
   

   /***********************************************************************
    * Accessors
    ***********************************************************************/

   // TODO: make comments
   
   public static double[][] getxKernel() {
      return X_KERNEL;
   }


   public static double[][] getyKernel() {
      return Y_KERNEL;
   }


   public double getKernelRadius() {
      return kernelRadius;
   }


   public boolean isL1norm() {
      return L1norm;
   }


   public int getHighThreshold() {
      return highThreshold;
   }


   public int getLowThreshold() {
      return lowThreshold;
   }


   public boolean[][] getEdges() {
      return edges;
   }


   public boolean[][] getStrongEdges() {
      return strongEdges;
   }


   public boolean[][] getWeakEdges() {
      return weakEdges;
   }


   public int getNumEdgePixels() {
      return numEdgePixels;
   }


   public int getStrongEdgePixels() {
      return strongEdgePixels;
   }


   public int getWeakEdgePixels() {
      return weakEdgePixels;
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
      CannyEdgeDetector canny = new CannyEdgeDetector(pixels, -1);
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