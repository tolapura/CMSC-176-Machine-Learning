package polynomial_feature;

import Jama.Matrix;
import org.jfree.chart.*;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class Main {
    public static Matrix X, y, polynomial, theta, his;
    public static double lambda = 0.0001;
    public static int m = 0;
    public static int n = 0;
    public final static int iters = 1000;
    public static double alpha = 0.001;

    public static void load(String fileName) throws FileNotFoundException {
        ArrayList<Double> arr = new ArrayList<>();
        ArrayList<String> arr2 = new ArrayList<>();
        File file = new File(fileName);
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
        X = new Matrix(m, n+1);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n+1; j++) {
                if (j == 0) {
                    X.set(i,j, 1);
                } else {
                    X.set(i, j, arr.get(0));
                    arr.remove(0);
                }
            }
        }
        y = new Matrix(m, 3, 0);
        for (int i = 0; i < m; i++) {
            if (arr2.get(0).equals("Iris-setosa")) {
                y.set(i, 0, 1);
            } else if (arr2.get(0).equals("Iris-versicolor")) {
                y.set(i, 1, 1);
            } else if (arr2.get(0).equals("Iris-virginica")) {
                y.set(i, 2, 1);
            }
            arr2.remove(0);
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

    public static void engineerPolynomials(Matrix X, int degree) {
        int perm = (int) Math.pow(degree + 1, n);
        ArrayList header = new ArrayList();
        for (int i = 0; i < perm; i++) {
            String ans = Integer.toString(i, degree + 1);
            header.add(String.format("%0" + n + "d", Integer.parseInt(ans)));
        }
        polynomial = new Matrix(m, perm, 1);
        for (int j = 0; j < m; j++) {
            for (int k = 1; k < perm; k++) {
                double res = 1;
                for (int l = 0; l < n; l++) {
                    double val = X.get(j, l+1);
                    int pow = (header.get(k).toString().charAt(l)) - '0';
                    res *= (Math.pow(val, pow));
                }
                polynomial.set(j,k, res);
            }
        }
    }

    public static double sigmoid(double x) {
        return (1.0/ (1 + Math.pow(Math.E, (-1 * x))));
    }

    public static Matrix sigmoidMatrix(Matrix polyX, Matrix theta){
        Matrix temp = polyX.times(theta);
        for(int i = 0; i < temp.getRowDimension(); i++){
            temp.set(i,0, sigmoid(temp.get(i,0)));
        }
        return temp;
    }

    public static double regularizedCost(Matrix x, Matrix y,Matrix theta,double lambda){
        Matrix polyXTheta = sigmoidMatrix(x,theta);
        double cost = 0.0;
        for(int j = 0; j < polyXTheta.getRowDimension(); j++){
            Double value = 0.0;
            if(y.get(j,0) == 1){
                value = ((Math.log(polyXTheta.get(j,0))));
                if(!value.isNaN() && !value.isInfinite()){
                    cost += value;
                }
            } else {
                value = ((Math.log(1 - polyXTheta.get(j,0))));
                if(!value.isNaN() && !value.isInfinite()){
                    cost += value;
                }
            }
        }
        cost = cost * (-1) * (1.0 / polyXTheta.getRowDimension());
        cost += computePenalty(theta,lambda);
        return cost;
    }

    public static double computePenalty(Matrix theta,double lambda){
        double p = 0;
        for(int k = 0; k < theta.getRowDimension(); k++)
            p += Math.pow(theta.get(k,0), 2);
        p *= (lambda / (2 * X.getRowDimension()));
        return p;
    }

    public static double computePenaltyDescent(Matrix theta){
        double p = 0;
        for (int k = 0; k < theta.getRowDimension(); k++)
            p += theta.get(k,0);
        p *= (lambda / X.getRowDimension());
        return p;
    }


    public static void gradientDescent(Matrix polyX, Matrix y, double alpha, int iters){
        his = new Matrix(iters, y.getColumnDimension(),0);
        theta = new Matrix(polyX.getColumnDimension(), y.getColumnDimension(),1);
        for(int i = 0; i < iters; i++) {
            for (int c = 0; c < y.getColumnDimension(); c++) {
                Matrix m = polyX.times(theta).minus(y);
                double p = computePenaltyDescent(theta.getMatrix(0, theta.getRowDimension() - 1, c, c));
                for (int r = 0; r < theta.getRowDimension(); r++) {
                    double step;
                    Matrix a = m.getMatrix(0, polyX.getRowDimension() - 1, c, c);
                    Matrix b = polyX.getMatrix(0, polyX.getRowDimension() - 1, r, r);
                    step = a.transpose().times(b).get(0, 0);
                    step = ((alpha / X.getRowDimension()) * step) + p;
                    theta.set(r, c, theta.get(r, c) - step);
                }
                Matrix partY = y.getMatrix(0, y.getRowDimension() - 1, c, c);
                Matrix partTheta =  theta.getMatrix(0, theta.getRowDimension() - 1, c, c);
                his.set(i, c, regularizedCost(polyX, partY, partTheta, lambda));
            }
        }
        graph(his,0, "Setosa");
        graph(his,1, "Versicolor");
        graph(his,2, "Virginica");
    }

    public static void graph(Matrix cost_history,int col, String title){
        XYSeries dataSet = new XYSeries("Cost vs Iteration");
        for (int i = 0; i < iters; i++) dataSet.add(i, cost_history.get(i,col));
        XYDataset xyDataSet = new XYSeriesCollection(dataSet);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Cost vs Iteration for " + title + " Predictor", "Iterations", "Cost", xyDataSet,
                PlotOrientation.VERTICAL, true, true, false
        );
        ChartFrame chartFrame = new ChartFrame("Gradient Descent", chart);
        chartFrame.setVisible(true);
        chartFrame.setSize(600, 400);
    }

    public static void main(String[] args) throws FileNotFoundException {
        load("irisflowers.csv");
        scaleFeatures(X);
        engineerPolynomials(X, 1);
        gradientDescent(polynomial,y,alpha,iters);

    }
}