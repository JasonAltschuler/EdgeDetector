/**************************************************************************
 * @author Jason Altschuler
 * 
 * @tags machine learning, computer vision, image analysis, edge detection, AI
 * 
 * PURPOSE: Used to calculate image derivatives.
 * 
 * GRADIENTS: 
 *    left               (f_n)   - (f_n-1)
 *    right              (f_n+1) - (f_n)
 *    simple_symmetric   (f_n+1) - (f_n-1)
 *    double-symmetric   (f_n+1) - 2 * (f_n) + (f_n-1)
 *    
 *************************************************************************/

package edgedetector.imagederivatives;

public enum Gradient {
   LEFT, RIGHT, SIMPLE_SYMMETRIC, DOUBLE_SYMMETRIC;

   @Override
   public String toString() {   
      switch (this) {
      case LEFT:             return "Left gradient: (f_n) - (f_n-1)";
      case RIGHT:            return "Right gradient: (f_n+1) - (f_n)";
      case SIMPLE_SYMMETRIC: return "Simple_symmetric gradient: (f_n+1) - (f_n-1)";
      case DOUBLE_SYMMETRIC: return "Double_symmetric gradient: (f_n+1) - 2 * (f_n) + (f_n-1)";
      }
      return super.toString();
   }
}
