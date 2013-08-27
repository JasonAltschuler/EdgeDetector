/**************************************************************************
 * @author Jason Altschuler
 * 
 * @tags machine learning, computer vision, image analysis, edge detection, AI
 * 
 * PURPOSE: Used to calculate image derivatives
 * 
 * PADDING: 
 *    zeros       0  ,  z_1,  z_2,  ...,  z_n-1,  z_n,  0
 *    same        z_1,  z_1,  z_2,  ...,  z_n-1,  z_n,  z_n
 *    symmetric   z_2,  z_1,  z_2,  ...,  z_n-1,  z_n,  z_n-1
 *    circular    z_n,  z_1,  z_2,  ...,  z_n-1,  z_n1, z_1
 *    
 *************************************************************************/

package edgedetector.imagederivatives;

public enum Padding {
   ZEROS, SAME, SYMMETRIC, CIRCULAR;

   @Override
   public String toString() {   
      switch (this) {
      case ZEROS:     return "Zeros padding: 0, z_1, z_2, ..., z_n-1, z_n, 0";
      case SAME:      return "Same padding: z_1, z_1, z_2, ..., z_n-1, z_n, z_n";
      case SYMMETRIC: return "Symmetric padding: z_2, z_1, z_2, ..., z_n-1, z_n, z_n-1";
      case CIRCULAR:  return "Circular padding: z_n, z_1, z_2, ..., z_n-1, z_n1, z_1";
      }
      return super.toString();
   }
}
