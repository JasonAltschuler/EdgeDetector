/**************************************************************************
 * @author Jason Altschuler
 * 
 * @tags edge detection, image analysis, computer vision, AI, machine learning
 * 
 * PURPOSE: Applies non-maximum suppression, a key step in edge detection. 
 * 
 * OVERVIEW: Edges in images are usually to defined to be 1 pixel thick. Thus,
 * we cannot simply look for pixels at which place the change in intensity
 * is sufficiently large (greater than a threshold). We must also suppress
 * non-maximum pixels. Essentially, we calculate the edge direction 
 * (direction in which gradient of pixel intensity is greatest). We round the 
 * edge direction to the nearest 45 degrees. (i, j) is suppressed if
 * either of the pixels one unit in the edge direction have greater gradient
 * magnitudes than (i, j). 
 ************************************************************************/

package edgedetector.util;

public class NonMaximumSuppression {
   
   /**
    * Direction to check non-maximum suppression
    */
   public enum EdgeDirection { 
      // rounded to nearest 45 degrees. checks pixels at both side of each direction.
      VERTICAL, 
      HORIZONTAL, 
      DIAG_LEFT_UP, 
      DIAG_RIGHT_UP; 
      
      // constants used for getting direction
      public static final double UP = Math.PI / 2.0;
      public static final double UP_TILT = Math.PI * 77.5 / 180.0;
      public static final double FLAT_TILT = Math.PI * 22.5 / 180.0;
      public static final double FLAT = 0;
      
      /**
       * Gets direction for non-maximum suppression from G_x and G_y.
       * Handles case when G_x == 0 and can't calculate arctan.
       * @param G_x
       * @param G_y
       * @return
       */
      public static EdgeDirection getDirection(double G_x, double G_y) {
         return (G_x != 0) ? getDirection(Math.atan(G_y / G_x)) : ((G_y == 0) ? EdgeDirection.HORIZONTAL : EdgeDirection.VERTICAL);
      }
      
      /**
       * Rounds direction to nearest 45 degrees.
       * @param radians
       * @return
       */
      public static EdgeDirection getDirection(double radians) {
         double radians_abs = Math.abs(radians);
         if (radians_abs >= UP_TILT && radians_abs <= UP)
            return EdgeDirection.VERTICAL;
         else if (radians_abs <= FLAT_TILT)
            return EdgeDirection.HORIZONTAL;
         else if (radians >= FLAT_TILT && radians <= UP_TILT)
            return EdgeDirection.DIAG_RIGHT_UP;
         else 
            return EdgeDirection.DIAG_LEFT_UP;
      }
   }
   
   /**
    * See if pixel at (i, j) is an edge. Requires the following two criteria:
    * <P> 1. mag[i][j] > threshold
    * <P> 2. Non-maximum suppression 
    * @param mag
    * @param angle
    * @param threshold
    * @param i
    * @param j
    * @return
    */
   public static boolean nonMaximumSuppression(int[][] mag, EdgeDirection angle, int i, int j) {
      // calculate indices of 2 points to check for non-maximum suppression
      int[] indices = indicesMaxSuppresion(angle, i, j);

      // first point to check
      int i1 = indices[0];
      int j1 = indices[1];

      // second point to check
      int i2 = indices[2];
      int j2 = indices[3];

      // non-maximum suppression 
      boolean suppress1 = checkInBounds(i1, j1, mag.length, mag[0].length) && mag[i1][j1] > mag[i][j];
      boolean suppress2 = checkInBounds(i2, j2, mag.length, mag[0].length) && mag[i2][j2] > mag[i][j];

      // only return true if (i, j) is not suppressed by either of its 2 neighbors
      return (suppress1 || suppress2) ? false : true;
   }
 
   /**
    * Get coordinates of the two points needed to check for non-maximum suppression.
    * @param d
    * @param i
    * @param j
    * @return {i1, j1, i2, j2}
    */
   public static int[] indicesMaxSuppresion(EdgeDirection d, int i, int j) {
      // coordinates of two points to check for non-maximum suppression
      int[] indices = new int[4];
      
      switch (d) {
      case VERTICAL:
         indices[0] = i - 1;
         indices[1] = j;
         indices[2] = i + 1;
         indices[3] = j;
         break;
      case HORIZONTAL:
         indices[0] = i;
         indices[1] = j - 1;
         indices[2] = i;
         indices[3] = j + 1;
         break;
      case DIAG_LEFT_UP:
         indices[0] = i - 1;
         indices[1] = j - 1;
         indices[2] = i + 1;
         indices[3] = j + 1;
         break;
      default: // DIAG_RIGHT_UP
         indices[0] = i - 1;
         indices[1] = j + 1;
         indices[2] = i + 1;
         indices[3] = j - 1;
         break;
      }
      
      return indices;
   }
   

   /**
    * Checks that pixel at (i, j) is in bounds for image. 
    * @param i
    * @param j
    * @param rows
    * @param columns
    * @return
    */
   private static boolean checkInBounds(int i, int j, int rows, int columns) {
      return (i >= 0 && i < rows && j >= 0 && j < columns) ? true : false;
   }
   
}