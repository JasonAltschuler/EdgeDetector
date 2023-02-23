package util;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

public class Threshold {
   
   /**
    * Calculates threshold as the mean of the |G| matrix for edge detection algorithms.
    * @param image
    * @return
    */
   public static int calcThresholdEdges(int[][] magnitude) {
      return (int) Statistics.calcMean(magnitude);
   }
   

   /**
    * Returns BufferedImage where color at (i, j) is black if pixel intensity >
    * threshold; white otherwise.
    * @param pixels
    * @param threshold
    * @return
    */
   public static BufferedImage applyThreshold(int[][] pixels, int threshold) {
      int height = pixels.length;
      int width = pixels[0].length;

      BufferedImage thresholdedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      WritableRaster raster = thresholdedImage.getRaster();

      int[] black = {0, 0, 0};
      int[] white = {255, 255, 255};

      // cache-efficient for both BufferedImage and int[][]
      for (int row = 0; row < height; row++)
         for (int col = 0; col < width; col++)
            raster.setPixel(col, row, pixels[row][col] > threshold ? white : black);

      return thresholdedImage;
   }
   
   public static BufferedImage applyThreshold(boolean[][] pixels) {
      int height = pixels.length;
      int width = pixels[0].length;

      BufferedImage thresholdedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      WritableRaster raster = thresholdedImage.getRaster();

      int[] black = {0, 0, 0};
      int[] white = {255, 255, 255};

      // cache efficient for both BufferedImage and int[][]
      for (int row = 0; row < height; row++)
         for (int col = 0; col < width; col++)
            raster.setPixel(col, row, pixels[row][col] ? white : black);

      return thresholdedImage;
   }
 
   public static BufferedImage applyThresholdReversed(boolean[][] pixels) {
      int height = pixels.length;
      int width = pixels[0].length;

      BufferedImage thresholdedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
      WritableRaster raster = thresholdedImage.getRaster();

      int[] black = {0, 0, 0};
      int[] white = {255, 255, 255};

      // cache efficient for both BufferedImage and int[][]
      for (int row = 0; row < height; row++)
         for (int col = 0; col < width; col++)
            raster.setPixel(col, row, pixels[row][col] ? black : white);

      return thresholdedImage;
   }
   
   public static BufferedImage applyThresholdWeakStrongCanny(boolean[][] weakEdges, boolean[][] strongEdges) {
      int height = weakEdges.length;
      int width = weakEdges[0].length;

      BufferedImage thresholdedImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
      WritableRaster raster = thresholdedImage.getRaster();

      int[] white = {255, 255, 255};
      int[] blue = {0, 0, 255};
      int[] green = {0, 255, 0};

      // cache efficient for both BufferedImage and int[][]
      for (int row = 0; row < height; row++)
         for (int col = 0; col < width; col++) {
            if (strongEdges[row][col])
               raster.setPixel(col, row, green);
            else if (weakEdges[row][col])
               raster.setPixel(col, row, blue);
            else
               raster.setPixel(col, row, white);
         }

      return thresholdedImage;
   }


   public static BufferedImage applyThresholdOriginal(boolean[][] edges, BufferedImage originalImage) {
      int height = edges.length;
      int width = edges[0].length;

      BufferedImage newImage = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
      WritableRaster raster_new = newImage.getRaster();
      WritableRaster raster_old = originalImage.getRaster();
      
      int[] white = {255, 255, 255};
      int[] arr = new int[3];
      int min;
      
      for (int row = 0; row < height; row++) {
         for (int col = 0; col < width; col++) {
            if (!edges[row][col]) {                   // not edge
               raster_new.setPixel(col, row, white);
            } else {                                  // edge
               // get original pixel color
               raster_old.getPixel(col, row, arr);

               // scale to max intensity
               min = 255;
               for (int i : arr)
                  if (i < min)
                     min = i;
               double scale = 255.0 / (255.0 - min);
               for (int i = 0; i < 3; i++)
                  arr[i] = 255 - (int) (scale * (255.0 - arr[i]));
               raster_new.setPixel(col, row, arr);
            }
         } 
      }
      
      return newImage;
   }
}
