import Jama.Matrix;
import java.io.*;
import java.util.*;

public class Main {
    public static Matrix a,b,t;
    public static int m = 0;
    public static int n = 0;

    public static void load_data(String fileName) throws FileNotFoundException {
        ArrayList<Integer> arr = new ArrayList<>();
        ArrayList<Integer> arr2 = new ArrayList<>();
        File file = new File(fileName);
        Scanner sc = new Scanner(file);
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
        b = new Matrix(m, 1);
        for (int j = 0; j < m; j++) {
            b.set(j, 0, arr2.get(0));
            arr2.remove(0);
        }

        t = new Matrix(n+1, 1);
        Random rand = new Random();
        for (int i = 0; i < n+1; i++) {
            int r = rand.nextInt(1000);
            t.set(i, 0, r);
        }
    }

    public static int cost(Matrix x, Matrix y, Matrix theta) {
        int ans = 0;
        Matrix h = x.times(theta).minus(y);
        for (int i = 0; i < h.getRowDimension(); i++) {
            h.set(i, 0, (Math.pow(h.get(i, 0), 2)));
            ans += h.get(i, 0);
        }
        return ans/(2 * m);
    }

    public static void main(String[] args) throws FileNotFoundException {
        load_data("HousePricingRelationship.in");
        System.out.println(cost(a, b, t));
    }
}
