package edgedetector;
// A type of Kernel (specifically for Sobel Edge Detection)

public class SobelKernel {

   public static double[][] X = {{-1, 0, 1},
                              {-2, 0, 2},
                              {-1, 0, 1}};

   public static double[][] Y = {{1, 2, 1}, 
                              {0, 0, 0},
                              {-1, -2, -1}};

}
