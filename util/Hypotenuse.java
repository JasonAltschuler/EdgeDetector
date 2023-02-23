/**************************************************************************
 * @author Jason Altschuler
 *
 * Calculate hypotenuse of triangle given length of other two sides. 
 ************************************************************************/

package util;

public class Hypotenuse {
   
   /**
    * |Hypotenuse| = |x| + |y|
    * @param x
    * @param y
    * @return
    */
   public static double L1(double x, double y) {
      return Math.abs(x) + Math.abs(y);
   }
   
   /**
    * |Hypotenuse| = sqrt(x^2 + y^2)
    * @param x
    * @param y
    * @return
    */
   public static double L2(double x, double y) {
      return Math.sqrt(x * x + y * y);
   }
}
