package featureScaling;

import Jama.Matrix;
import java.io.*;
import java.util.*;

public class Main {
    public static Matrix a;
    public static int m = 0;
    public static int n = 0;
    public static void load(String filename) throws FileNotFoundException {
        ArrayList<Double> arr = new ArrayList<>();
        ArrayList<String> arr2 = new ArrayList<>();
        File file = new File(filename);
        Scanner sc = new Scanner(file);
        String title = sc.nextLine();
        while (sc.hasNextLine()) {
            m += 1;
            String curr = sc.nextLine();
            StringTokenizer st = new StringTokenizer(curr, ",");
            n = st.countTokens() - 1;
            int ctr = 0;
            while (st.hasMoreTokens()) {
                if (ctr < n) {
                    arr.add(Double.parseDouble(st.nextToken()));
                } else {
                    arr2.add(st.nextToken());
                }
                ctr++;
            }
        }
        a = new Matrix(m, n+1, 1);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n+1; j++) {
                if (j != 0) {
                    a.set(i, j, arr.get(0));
                    arr.remove(0);
                }
            }
        }
    }
    public static void scaleFeatures(Matrix X){
        for (int i = 1; i < X.getColumnDimension(); i++) {
            Matrix cons = new Matrix(m, 1, 1);
            double mean = (X.getMatrix(0, m-1, i, i).transpose().times(cons).get(0,0))/m;
            Matrix meanCopy = new Matrix(m, 1, mean);
            Matrix temp = X.getMatrix(0, m-1, i, i).minus(meanCopy);
            double sDev = Math.sqrt(temp.transpose().times(temp).get(0,0)/m);
            for (int j = 0; j < X.getRowDimension(); j++) {
                X.set(j, i, (X.get(j, i) - mean) / sDev);
            }
        }
    }

    public static void printMatrix(Matrix h) {
        for (int i = 0; i < h.getRowDimension(); i++) {
            System.out.println();
            for (int j = 0; j < h.getColumnDimension(); j++) {
                System.out.print(h.get(i,j) + " ");
            }
        }
        System.out.println();
    }
    public static void main(String[] args) throws FileNotFoundException {
        load("irisflowers.csv");
        scaleFeatures(a);
        printMatrix(a);
    }
}
