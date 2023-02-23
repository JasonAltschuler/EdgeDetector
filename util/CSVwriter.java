package util;
/*************************************************************************
 * @author Altschuler and Wu Lab
 * 
 * Writes CSV files 
 ************************************************************************/

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;


public class CSVwriter {

   /**
    * Writes double[][] to .txt file
    */
   public static void write(String outFile, double[][] arr) throws FileNotFoundException {
      PrintStream out = new PrintStream(new FileOutputStream(outFile));

      int rows = arr.length;
      int columns = arr[0].length;

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            out.print(arr[r][c]);

            if (c != columns - 1)
               out.print(',');
         }
         out.println();
      }

      out.close();
   }
   
   
   /**
    * Writes double[][] to .txt file
    */
   public static void write(String outFile, boolean[][] arr) throws FileNotFoundException {
      PrintStream out = new PrintStream(new FileOutputStream(outFile));

      int rows = arr.length;
      int columns = arr[0].length;

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            out.print(arr[r][c] ? "1" : "0");

            if (c != columns - 1)
               out.print(',');
         }
         out.println();
      }

      out.close();
   }
   
   

   
   /**
    * Writes int[] to .txt file
    */
   public static void write(String outFile, int[] arr) throws FileNotFoundException {
      PrintStream out = new PrintStream(new FileOutputStream(outFile));
      
      for (int r = 0; r < arr.length; r++)
          out.println(arr[r]);
 
      out.close();
   }
   
   /**
    * Writes ArrayList<Integer> to .txt file
    */
   public static void write(String outFile, ArrayList<double[][][]> al) throws FileNotFoundException {
      PrintStream out = new PrintStream(new FileOutputStream(outFile));
      
      for (double[][][] arr : al) {
         for (int i = 0; i < arr.length; i++) {
            for (int j = 0; j < arr[0].length; j++) {
               for (int k = 0; k < arr[0][0].length; k++) {
                  out.print(arr[i][j][k] + " ");
               }
               out.print("\t");
            }
            out.println();
         }
         out.println("****");
      }
 
      out.close();
   }
   

   public static void write(String outFile, double[][][] arr) throws FileNotFoundException {
      PrintStream out = new PrintStream(new FileOutputStream(outFile));

      int rows = arr.length;
      int columns = arr[0].length;
      int slices = arr[0][0].length;

      for (int r = 0; r < rows; r++) {
         for (int c = 0; c < columns; c++) {
            for (int s = 0; s < slices; s++) {
               out.print(arr[r][c][s]);
            }
            out.println();
         }
         out.println("*******");
      }

      out.close();
   }
   
   
   public static void writeMultiple(String outFile, double[][] arr1, double[][] arr2, double[][] arr3) throws FileNotFoundException {
      PrintStream out = new PrintStream(new FileOutputStream(outFile));
      print(out, arr1);
      print(out, arr2);
      print(out, arr3);
      out.close();
   }
   
   private static void print(PrintStream out, double[][] arr) {
      for (int r = 0; r < arr.length; r++) {
         for (int c = 0; c < arr[0].length; c++) {
            out.print(arr[r][c]);

            if (c != arr[0].length - 1)
               out.print(',');
         }
         out.println();
      }
      out.println();
      out.println();
      out.println("***********************");
      out.println();
   }

}