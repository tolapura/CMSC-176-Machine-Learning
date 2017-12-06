package linear_regressions;

import Jama.Matrix;
import java.io.*;
import java.util.*;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;

public class Main {
    public static Matrix a, b;
    public static int m = 0;
    public static int n = 0;
    public final static int iters = 100;
    public final static double alpha = 0.00000001;
    
    public static void load_data(String fileName) throws FileNotFoundException {
        ArrayList<Integer> arr = new ArrayList<>();
        ArrayList<Integer> arr2 = new ArrayList<>();
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
        // counting the size of the file and temporarily storing the elements into an arraylist
        while (sc.hasNextLine()) {
            m += 1;
            String curr = sc.nextLine();
            StringTokenizer st = new StringTokenizer(curr, ",");
            n = st.countTokens() - 1;
            int ctr = 0;
            while (st.hasMoreTokens()) {
                if (ctr < n) {
                    arr.add(Integer.parseInt(st.nextToken()));
                } else {
                    arr2.add(Integer.parseInt(st.nextToken()));
                }
                ctr++;
            }
        }
        // setting of the elements for matrix a
        a = new Matrix(m, n+1);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n+1; j++) {
                if (j == 0) {
                    a.set(i,j, 1);
                } else {
                    a.set(i, j, arr.get(0));
                    arr.remove(0);
                }
            }
        }
        // setting of the elements for matrix b
        b = new Matrix(m, 1);
        for (int j = 0; j < m; j++) {
            b.set(j, 0, arr2.get(0));
            arr2.remove(0);
        }
    }

    public static double cost(Matrix x, Matrix y, Matrix theta) {
        double ans = 0;
        Matrix h = x.times(theta).minus(y);
        for (int i = 0; i < h.getRowDimension(); i++) {
            h.set(i, 0, (Math.pow(h.get(i, 0), 2)));
            ans += h.get(i, 0);
        }
        return ans/(2 * m);
    }

    public static void gradientDescent(Matrix X, Matrix y, double alpha, int iters) {
        ArrayList<Double> costs = new ArrayList<>();
        Matrix t = new Matrix(n+1, 1,0);
        for (int i = 0; i < iters; i++) {
            Matrix hOfx = X.times(t);
            for (int j = 0; j < t.getRowDimension(); j++) {
                double summation = 0;
                summation = ((hOfx.minus(y)).transpose().times(X.getMatrix(0,m-1,j,j))).get(0,0);
                t.set(j, 0, t.get(j, 0) - (alpha/m * summation));
            }
            costs.add(cost(X, y, t));
        }
        graph(costs);
    }

    public static void graph(ArrayList<Double> costs) {
        // Prepare the data set
        XYSeries xySeries = new XYSeries("House Pricing Relationship");
        for (int i = 0; i < iters; i++) {
            xySeries.add(i, costs.get(i));
        }
        XYDataset xyDataset = new XYSeriesCollection(xySeries);

        //Create the chart
        JFreeChart chart = ChartFactory.createXYLineChart(
                "House Pricing Relationship", "Iteration", "Cost", xyDataset,
                PlotOrientation.VERTICAL, true, true, false);

        //Render the frame
        ChartFrame chartFrame = new ChartFrame("Gradient Descent", chart);
        chartFrame.setVisible(true);
        chartFrame.setSize(500, 400);
    }

    public static void main(String[] args) throws FileNotFoundException {
        load_data("HousePricingRelationship.in");
        gradientDescent(a, b, alpha, iters);
    }
}