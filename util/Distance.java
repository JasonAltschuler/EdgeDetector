/*************************************************************************
 * @author Jason Altschuler
 * 
 * Minkowski distance calculator for Euclidean space.
 *************************************************************************/

package util;

public class Distance {

   /**
    * L1 norm: distance = sum_i=1:n[|x_i - y_i|]
    * <P> Minkowski distance of order 1.
    * @param x
    * @param y
    * @return
    */
   public static double L1(double[] x, double[] y) {
      if (x.length != y.length) throw new IllegalArgumentException("dimension error");
      double dist = 0;
      for (int i = 0; i < x.length; i++) 
         dist += Math.abs(x[i] - y[i]);
      return dist;
   }
   
   /**
    * L2 norm: distance = sqrt(sum_i=1:n[(x_i-y_i)^2])
    * <P> Euclidean distance, or Minkowski distance of order 2.
    * @param x
    * @param y
    * @return
    */
   public static double L2(double[] x, double[] y) {
      if (x.length != y.length) throw new IllegalArgumentException("dimension error");
      double dist = 0;
      for (int i = 0; i < x.length; i++)
         dist += Math.abs((x[i] - y[i]) * (x[i] - y[i]));
      return dist;
   }
   

   /**
    *  p-norm: distance = (sum_i=1:n[(Math.abs(x_i-y_i))^p])^(1/p)
    * <P> Minkowski distance of order p.
    * @param x
    * @param y
    * @param p positive integer
    * @return 
    */
   public static double PNorm(double[] x, double[] y, int p) {
      if (p <= 0) throw new IllegalArgumentException("p must be positive");
      if (x.length != y.length) throw new IllegalArgumentException("dimension error");
      double dist = 0;
      for (int i = 0; i < x.length; i++)
         dist += Math.pow(Math.abs(x[i]-y[i]), 1 / (double) p);
      return dist;
   }

}
