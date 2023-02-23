package util;

public class Print {

   public static void array(double[][] arr) {
      for (int i = 0; i < arr.length; i++) {
         for (int j = 0; j < arr[0].length; j++) {
            System.out.print(arr[i][j]);
            if (j != arr[0].length - 1)
               System.out.print(", ");
         }
         System.out.println();
      }
   }
   
   public static void array(int[][] arr) {
      for (int i = 0; i < arr.length; i++) {
         for (int j = 0; j < arr[0].length; j++) {
            System.out.print(arr[i][j]);
            if (j != arr[0].length - 1)
               System.out.print(", ");
         }
         System.out.println();
      }
   }
   
   public static void dimensions(double[][][] arr) {
      System.out.println(arr.length + "\t" + arr[0].length + "\t" + arr[0][0].length);
   }
   
   public static void dimensions(double[][] arr) {
      System.out.println(arr.length + "\t" + arr[0].length);
   }
}
