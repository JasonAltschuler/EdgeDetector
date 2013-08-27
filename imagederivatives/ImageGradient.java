/**************************************************************************
 * @author Jason Altschuler
 * 
 * @tags machine learning, computer vision, image analysis, edge detection, AI
 * 
 * PURPOSE: Calculates discrete derivative of image.
 * 
 * GRADIENTS: 
 *    left               (f_n)   - (f_n-1)
 *    right              (f_n+1) - (f_n)
 *    simple_symmetric   (f_n+1) - (f_n-1)
 *    double-symmetric   (f_n+1) - 2 * (f_n) + (f_n-1)
 *    
 * PADDING: 
 *    zeros       0  ,  z_1,  z_2,  ...,  z_n-1,  z_n,  0
 *    same        z_1,  z_1,  z_2,  ...,  z_n-1,  z_n,  z_n
 *    symmetric   z_2,  z_1,  z_2,  ...,  z_n-1,  z_n,  z_n-1
 *    circular    z_n,  z_1,  z_2,  ...,  z_n-1,  z_n1, z_1
 *    
 **************************************************************************/

package edgedetector.imagederivatives;

public class ImageGradient {
   // for BufferedImages in Java, pixel itensities range from 0 to 255, inclusive.
   private final static int MAX = 255; 
   
   /***********************************************************************
    * Data structures
    ***********************************************************************/
   private double[][] imageGradient;   // final answer 

   private final Gradient gradient;    // method of calculate the gradient of the image
   private final Padding padding;      // method to pad the sides of the image
   private final boolean L1norm;       // method to calculate the magnitude of the gradient
                                       // True: |g_x| + |g_y|. False: Math.sqrt(g_x^2 + g_y^2).

   private final int rows;             // # of rows in image
   private final int columns;          // # of columns in image
   

   /***********************************************************************
    * Constructor
    **********************************************************************/

   /**
    * Calculates the gradient of an image using user-defined gradient and padding methods. 
    * Work is done in constructor.
    * @param image
    * @param gradient
    * @param padding
    * @param L1norm
    */
   public ImageGradient(double[][] image, Gradient gradient, Padding padding, boolean L1norm) {
      checkSize(image);
      
      this.gradient = gradient;
      this.padding = padding;
      this.L1norm = L1norm;
      this.rows = image.length;
      this.columns = image[0].length;
      
      run(image);
   }
   
   private void checkSize(double[][] image) {
      if (image.length < 3 || image[0].length < 3)
         throw new IllegalArgumentException("Image too small");
   }


   /***********************************************************************
    * Calculate gradient
    **********************************************************************/
   private void run(double[][] image) {
      this.imageGradient = new double[image.length][image[0].length];

      // set up side padding
      double[] left = new double[rows];
      double[] right = new double[rows];
      double[] top = new double[columns];
      double[] bottom = new double[columns];
      setUpPadding(image, left, right, top, bottom);
      
      // calculate the image gradient using the padding
      calcImageGradient(image, left, right, top, bottom);
   } 

   /**
    * Set up side padding depending on user-defined padding method
    * @param image
    * @param left
    * @param right
    * @param top
    * @param bottom
    */
   private void setUpPadding(double[][] image, 
                             double[] left,
                             double[] right,
                             double[] top,
                             double[] bottom) {
      switch (padding) {
      case ZEROS:
         break; // already initialized to 0's

      case SAME:
         for (int r = 0; r < rows; r++) {
            left[r] = image[r][0];
            right[r] = image[r][columns - 1];
         }

         for (int c = 0; c < columns; c++) {
            top[c] = image[0][c];
            bottom[c] = image[rows - 1][c];
         }
         break;

      case SYMMETRIC:
         for (int r = 0; r < rows; r++) {
            left[r] = image[r][1];
            right[r] = image[r][columns - 2];
         }

         for (int c = 0; c < columns; c++) {
            top[c] = image[1][c];
            bottom[c] = image[rows - 2][c];
         }
         break;

      case CIRCULAR:
         for (int r = 0; r < rows; r++) {
            left[r] = image[r][columns - 1];
            right[r] = image[r][0];
         }

         for (int c = 0; c < columns; c++) {
            top[c] = image[rows - 1][c];
            bottom[c] = image[0][c];
         }
         break;
      }
   }
   
   /**
    * Calculate the image gradient using the user-defined gradient function
    * @param image
    * @param left padding
    * @param right padding
    * @param top padding
    * @param bottom padding
    */
   private void calcImageGradient(double[][] image,
                                  double[] left,
                                  double[] right,
                                  double[] top, 
                                  double[] bottom) {
      switch (gradient) {
      case LEFT: 
         calcLeftGradient(image, left, top);
         return;
      case RIGHT: 
         calcRightGradient(image, right, bottom);
         return;
      case SIMPLE_SYMMETRIC: 
         calcSimpleSymmetricGradient(image, left, right, top, bottom);
         return;
      case DOUBLE_SYMMETRIC: 
         calcDoubleSymmetricGradient(image, left, right, top, bottom);
         break;
      }
   }
   
   
   /***********************************************************************
    * Different methods of calculating the gradient
    **********************************************************************/ 
   
   /**
    * Calculates the magnitude of the gradient |g| using the user-defined method.
    * Either |g_x| + |g_y| or Math.sqrt(g_x^2 + g_y^2). Also checks that
    * return value is within 0 and 255, inclusive. 
    * @param g_x
    * @param g_y
    * @return
    */
   private double calcGradientMagnitude(double g_x, double g_y) {
      double g = (L1norm) ? Math.abs(g_x) + Math.abs(g_y) : Math.hypot(g_x, g_y);
      return (g > 0) ? ((g < MAX) ? g : MAX) : 0;
   }
  
   
   private void calcLeftGradient(double[][] image,
                                 double[] left,
                                 double[] top) {
      double g_x, g_y;  // temporary gradients

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            g_x = image[r][c] - ((c != 0) ? image[r][c - 1] : left[r]);
            g_y = image[r][c] - ((r != 0) ? image[r - 1][c] : top[c]);
            imageGradient[r][c] = calcGradientMagnitude(g_x, g_y);
         }
      }
   }

   private void calcRightGradient(double[][] image,
                                  double[] right,
                                  double[] bottom) {
      double g_x, g_y;  // temporary gradients

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            g_x = ((c != columns - 1) ? image[r][c + 1] : right[r])  - image[r][c];
            g_y = ((r != rows - 1)    ? image[r + 1][c] : bottom[c]) - image[r][c];
            imageGradient[r][c] = calcGradientMagnitude(g_x, g_y);
         }
      }
   }
   
   private void calcSimpleSymmetricGradient(double[][] image,
                                            double[] left,
                                            double[] right,
                                            double[] top,
                                            double[] bottom) {
      double g_x, g_y;  // temporary gradients

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            g_x = ((c != columns - 1) ? image[r][c + 1] : right[r])  
                      - ((c != 0) ? image[r][c - 1] : left[r]);
            g_y = ((r != rows - 1)    ? image[r + 1][c] : bottom[c]) 
                      - ((r != 0) ? image[r - 1][c] : top[c]);
            imageGradient[r][c] = calcGradientMagnitude(g_x, g_y);
         }
      }
   }

   private void calcDoubleSymmetricGradient(double[][] image,
                                            double[] left,
                                            double[] right,
                                            double[] top,
                                            double[] bottom) {
      double g_x, g_y;  // temporary gradients

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            g_x = ((c != columns - 1) ? image[r][c + 1] : right[r]) 
                      + ((c != 0) ? image[r][c - 1] : left[r]) - 2 * image[r][c];
            g_y = ((r != rows - 1)    ? image[r + 1][c] : bottom[c]) 
                      + ((r != 0) ? image[r - 1][c] : top[c])  - 2 * image[r][c];
            imageGradient[r][c] = calcGradientMagnitude(g_x, g_y);
         }
      }
   }   
   
   
   /***********************************************************************
    * Getters
    **********************************************************************/

   public double[][] getImageGradient() {
      return imageGradient;
   }

   public Gradient getGradient() {
      return gradient;
   }

   public Padding getPadding() {
      return padding;
   }


   /*************************************************************************************
    * Unit testing
    *************************************************************************************/
   
   /**
    * runs, times, and prints information from ImageGradient
    * @param image
    * @param g
    * @param p
    * @param useL1norm
    */
   private static void test(double[][] image, Gradient g, Padding p, boolean useL1norm) {
      // time and run ImageGradient
      final long startTime = System.currentTimeMillis();
      ImageGradient test_run = new ImageGradient(image, g, p, useL1norm);
      final long endTime = System.currentTimeMillis();

      // print timing information
      final long duration = endTime - startTime;
      System.out.println("Calculating the image gradient took " + (double) duration / 1000 + " seconds");
      
      // print testing information
      System.out.println(g.toString());
      System.out.println(p.toString());
      
      // print testing results
      System.out.println("Image gradient:");
      double[][] gradient = test_run.getImageGradient();
      for (int r = 0; r < gradient.length; r++) {
         System.out.print("[");
         for (int c = 0; c < gradient[0].length; c++) {
            System.out.print(gradient[r][c]);
            if (c != gradient[0].length - 1) 
               System.out.print(", ");
         }
         System.out.println("]");
      }
      System.out.println();
      System.out.println();
   }

   public static void main(String[] args) {
      // set up testing parameters
      double[][] image = {{1, 2, 3}, 
            {4, 5, 6},
            {7, 8, 9}};
      boolean useL1norm = true;

      // test all gradient and padding options
      for (Gradient g : Gradient.values()) { 
         for (Padding p: Padding.values()) {
            test(image, g, p, useL1norm);
         }
      }
   }
   
}
