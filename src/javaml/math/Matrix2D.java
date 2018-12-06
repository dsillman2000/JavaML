package javaml.math;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Matrix2D {

    private int m, n;
    private double[] values;
    public static Random random = new Random();

    public Matrix2D() {
        this.m = 0;
        this.n = 0;
        values = new double[0];
    }

    public Matrix2D(List<Matrix2D> rows) throws MatrixDimensionException {
        this.m = rows.size();
        this.n = (rows.size() == 0 ) ? 0 : rows.get(0).dim()[1];
        values = new double[m * n];
        for (int i = 0; i < rows.size(); i++) {
            if (rows.get(i).dim()[1] != rows.get(0).dim()[1]) {
                throw new MatrixDimensionException("rows list contains rows of varying lengths");
            }
            for (int c = 0; c < rows.get(i).dim()[1]; c++) {
                try {
                    values[i * n + c] = rows.get(i).get(0, c);
                } catch (MatrixIndexOutOfBoundsException mioobe) {
                    mioobe.printStackTrace();
                }
            }
        }
    }

    public Matrix2D(int m, int n) throws MatrixDimensionException {
        if (m < 1 || n < 1) {
            throw new MatrixDimensionException(String.format("invalid dimensions: %d, %d", m, n));
        }
        this.m = m;
        this.n = n;
        this.values = new double[m * n];
    }

    public int[] dim() {
        return new int[]{m, n};
    }

    public static Matrix2D zeros(int m, int n) {
        Matrix2D ret;
        try {
            ret = new Matrix2D(m, n);
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
            return null;
        }
        return ret;
    }

    public static Matrix2D ones(int m, int n) {
        Matrix2D ret;
        try {
            ret = new Matrix2D(m, n);
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
            return null;
        }
        for (int i = 0; i < ret.values.length; i++) {
            ret.set(i, 1.0);
        }
        return ret;
    }

    public static Matrix2D rand(int m, int n, double min, double max) {
        Matrix2D ret;
        try {
            ret = new Matrix2D(m, n);
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
            return null;
        }
        for (int i = 0; i < ret.values.length; i++) {
            ret.set(i, random.nextDouble() * (max - min) + min);
        }
        return ret;
    }

    public void set(int m, int n, double v) throws MatrixIndexOutOfBoundsException {
        if (m < this.m && m >= 0 && n < this.n && n >= 0) {
            set(m * this.n + n, v);
        } else {
            throw new MatrixIndexOutOfBoundsException(String.format("%d, %d", m, n));
        }
    }

    public double get(int m, int n) throws MatrixIndexOutOfBoundsException {
        if (m < this.m && m >= 0 && n < this.n && n >= 0) {
            return get(m * this.n + n);
        } else {
            throw new MatrixIndexOutOfBoundsException(String.format("%d, %d", m, n));
        }
    }

    private double get(int i) {
        return values[i];
    }

    private void set(int i, double v) {
        values[i] = v;
    }

    public Matrix2D add(Matrix2D m) throws MatrixDimensionException, MatrixIndexOutOfBoundsException {
        if (m.m != this.m || m.n != this.n) {
            throw new MatrixDimensionException(String.format("%d, %d != %d, %d", this.m, this.n, m.m, m.n));
        }
        Matrix2D sum = this.clone();
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                sum.set(i, j, sum.get(i, j) + m.get(i, j));
            }
        }
        return sum;
    }

    public Matrix2D sub(Matrix2D m) throws MatrixDimensionException, MatrixIndexOutOfBoundsException {
        Matrix2D diff = this.clone();
        return diff.add(m.neg());
    }

    public Matrix2D neg() throws MatrixIndexOutOfBoundsException {
        Matrix2D neg = this.clone();
        return neg.mult(-1);
    }

    public Matrix2D mult(double coefficient) throws MatrixIndexOutOfBoundsException {
        Matrix2D prod = this.clone();
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                prod.set(i, j, coefficient * prod.get(i, j));
            }
        }
        return prod;
    }

    public Matrix2D mult(Matrix2D m) throws MatrixDimensionException, MatrixIndexOutOfBoundsException {
        if (m.m != this.n) {
            throw new MatrixDimensionException("cols of m1 must equal rows of m2");
        }
        Matrix2D ret = new Matrix2D(this.m, m.n);
        for (int i = 0; i < ret.m; i++) {
            for (int j = 0; j < ret.n; j++) {
                for (int k = 0; k < this.n; k++) {
                    ret.set(i, j, ret.get(i, j) + this.get(i, k) * m.get(k, j));
                }
            }
        }
        return ret;
    }

    public Matrix2D hadamard(Matrix2D m) throws MatrixDimensionException, MatrixIndexOutOfBoundsException{
        if (this.m != m.m && this.n != m.n) {
            throw new MatrixDimensionException(String.format("%d, %d != %d, %d", this.m, this.n, m.m, m.n));
        }
        Matrix2D ret = new Matrix2D(this.m, this.n);
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                ret.set(i, j, this.get(i, j) * m.get(i, j));
            }
        }
        return ret;
    }

    public Matrix2D T() throws MatrixDimensionException, MatrixIndexOutOfBoundsException {
        Matrix2D trans = Matrix2D.zeros(this.n, this.m);
        for (int i = 0; i < this.m; i++) {
            for (int j = 0; j < this.n; j++) {
                trans.set(j, i, this.get(i, j));
            }
        }
        return trans;
    }

    public List<Matrix2D> rows() {
        Matrix2D[] mat = new Matrix2D[m];
        for (int i = 0; i < m; i++) {
            mat[i] = Matrix2D.zeros(1, n);
            for (int c = 0; c < n; c++) {
                try {
                    mat[i].set(0, c, values[i * n + c]);
                } catch (MatrixIndexOutOfBoundsException miobe) {
                    miobe.printStackTrace();
                    return null;
                }
            }
        }
        return Arrays.asList(mat);
    }

    public String toString() {
        String tot = "";
        for (int i = 0; i < this.m; i++) {
            String line = "";
            for (int j = 0; j < this.n; j++) {
                line += String.format("%.6f\t", values[i * this.n + j]);
            }
            tot += line + "\n";
        }
        return tot;
    }

    public Matrix2D clone() {
        Matrix2D clone = Matrix2D.zeros(m, n);
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                try {
                    clone.set(i, j, get(i, j));
                } catch (MatrixIndexOutOfBoundsException mioobe) {
                    mioobe.printStackTrace();
                    return null;
                }
            }
        }
        return clone;
    }
}
