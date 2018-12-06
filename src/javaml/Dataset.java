package javaml;

import javaml.math.Matrix2D;
import javaml.math.MatrixDimensionException;
import javaml.math.MatrixIndexOutOfBoundsException;

import java.io.*;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class Dataset implements Iterable {

    public enum Format {
        CSV
    }

    private Matrix2D data;
    private int numFeatures;
    private int numClasses;

    public Dataset(File src, Format format, int numFeatures, int numClasses) {
        this.numFeatures = numFeatures;
        this.numClasses = numClasses;
        if (format == Format.CSV) {
            LinkedList<Matrix2D> rows = new LinkedList<Matrix2D>();
            try (
                    FileReader fr = new FileReader(src);
                    BufferedReader br = new BufferedReader(fr);
            ) {
                for (String line = br.readLine(); line != null; line = br.readLine()) {
                    String[] entries = line.split(",");
                    Matrix2D curRow = Matrix2D.zeros(1, entries.length);
                    for (int i = 0; i < entries.length; i++) {
                        try {
                            curRow.set(0, i, Double.parseDouble(entries[i]));
                        } catch (MatrixIndexOutOfBoundsException mioobe) {
                            mioobe.printStackTrace();
                            return;
                        }
                    }
                    rows.add(curRow);
                }
            } catch (FileNotFoundException fnfe) {
                System.err.printf("File not found : %s", src);
                return;
            } catch (IOException ioe) {
                System.err.printf("IOException thrown when reading file : %s", src);
            }
            try {
                data = new Matrix2D(rows);
            } catch (MatrixDimensionException mde) {
                data = null;
                mde.printStackTrace();
                return;
            }
        }
    }

    public Dataset(int size, int numFeatures, int numClasses) {
        this.numFeatures = numFeatures;
        this.numClasses = numClasses;
        try {
            data = new Matrix2D(size, numFeatures + numClasses);
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
        }
    }

    public Dataset(Matrix2D data, int numFeatures, int numClasses) {
        this(data.dim()[0], numFeatures, numClasses);
        if (data.dim()[1] == 1) {
            System.err.println("Warning: only one column found in dataset.");
        }
        this.data = data;
    }

    public Iterator<Matrix2D> iterator() {
        return data.rows().iterator();
    }

    public int numFeatures() {
        return numFeatures;
    }

    public int numClasses() {
        return numClasses;
    }

    public Matrix2D X() {
        Matrix2D X;
        try {
            X = new Matrix2D(size(), numFeatures);
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
            return null;
        }
        for (int i = 0; i < size(); i++) {
            for (int c = 0 ; c < numFeatures; c++) {
                try {
                    X.set(i, c, data.get(i, c));
                } catch (MatrixIndexOutOfBoundsException mioobe) {
                    mioobe.printStackTrace();
                    return null;
                }
            }
        }
        return X;
    }

    public Matrix2D Y() {
        Matrix2D Y;
        try {
            Y = new Matrix2D(size(), numClasses);
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
            return null;
        }
        for (int i = 0; i < size(); i++) {
            for (int c = 0 ; c < numClasses; c++) {
                try {
                    Y.set(i, c, data.get(i, c + numFeatures - 1));
                } catch (MatrixIndexOutOfBoundsException mioobe) {
                    mioobe.printStackTrace();
                    return null;
                }
            }
        }
        return Y;
    }

    public int size() {
        return data.dim()[0];
    }

    public String toString() {
        return data.toString();
    }
}
