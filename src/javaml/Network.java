package javaml;

import javaml.math.Matrix2D;
import javaml.math.MatrixDimensionException;
import javaml.math.MatrixIndexOutOfBoundsException;

import java.util.LinkedList;

public class Network {

    public enum Error {
        MSE
    }

    public double learning_rate = 0.0001;
    public double delta = 0.0001;
    private LinkedList<Layer> layers;
    private LinkedList<Matrix2D> weights;
    private double oldError = Double.MAX_VALUE;

    public Network(int... dims) {
        layers = new LinkedList<Layer>();
        weights = new LinkedList<Matrix2D>();
        int i = 0;
        Layer l0 = new Layer(dims[0]);
        Layer prev = l0;
        while (i < dims.length - 1) {
            Layer next = new Layer(dims[i+1]);
            prev.link(next);
            layers.add(prev);
            i++;
            prev = next;
        }
        layers.add(new Layer(dims[dims.length - 1]));
        for (i = 0; i < layers.size() - 1; i++) {
            weights.add(layers.get(i).get_weights());
        }
    }

    public Matrix2D computeOutput(Dataset dataset) {
        LinkedList<Matrix2D> output = new LinkedList<Matrix2D>();
        try {
            for (Object o : dataset.X().rows()) {
                Matrix2D row = (Matrix2D) o;
                if (row.dim()[1] != layers.get(0).dim()) {
                    throw new MatrixDimensionException("Number of features in dataset != dimension of input layer!");
                }
                output.add(forwardPropagate(row));
            }
        } catch(MatrixDimensionException mde) {
            mde.printStackTrace();
            return null;
        }
        try {
            return new Matrix2D(output);
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
            return null;
        }
    }

    public void fit(Dataset dataset, Error error_protocol, int iterations, boolean verbose) {
        System.out.println("Starting fitting process...");
        for (int i = 0; i != iterations; i++) {
            backPropagate(dataset, error_protocol);
            if (i % 10 == 0) {
                double newError = evaluate(dataset, error_protocol);
                if (newError <= delta) {
                    iterations = i;
                    break;
                }
                oldError = newError;
                if (verbose) {
                    System.out.printf("(%8d) -------> E: %.8f\n", i, oldError);
                }
            }
        }
        System.out.printf("----------------------------\nFinal error: %f (%d)" +
                "\n----------------------------\n", oldError, iterations);
    }

    private void backPropagate(Dataset dataset, Error error_protocol) {
        for (int j = weights.size() - 1; j >= 0; j--) {
            weightUpdate(j, derivative(dataset, j, error_protocol));
        }
    }

    private Matrix2D derivative(Dataset dataset, int index, Error error_protocol) {
        Matrix2D dWj;
        Matrix2D Y = computeOutput(dataset);
        switch(error_protocol) {
            case MSE:
                dWj = Matrix2D.zeros(weights.get(index).dim()[0], weights.get(index).dim()[1]);
                try {
                    for (int m = 0; m < weights.size(); m++) {
                        Matrix2D d = Y.rows().get(m).sub(dataset.Y().rows().get(m));
                        Matrix2D prefix = dataset.X().rows().get(m);
                        for (int i = 0; i < index; i++) {
                            prefix = prefix.mult(weights.get(i));
                        }
                        prefix = prefix.T().mult(d);
                        for (int i = 0; i < weights.size() - index - 1; i++) {
                            prefix = prefix.mult(weights.get(weights.size() - i - 1).T());
                        }
                        dWj = dWj.add(prefix);
                    }
                    return dWj;
                } catch (MatrixDimensionException|MatrixIndexOutOfBoundsException mde) {
                    mde.printStackTrace();
                    return null;
                }
            default:
                return null;
        }
    }

    public void weightUpdate(int index, Matrix2D dWj) {
        try {
            weights.set(index, weights.get(index).sub(dWj.mult(learning_rate)));
        } catch (MatrixIndexOutOfBoundsException|MatrixDimensionException mioobe) {
            mioobe.printStackTrace();
            return;
        }
    }

    public double evaluate(Dataset dataset, Error error_protocol) {
        Matrix2D Y_expected = dataset.Y();
        Matrix2D Y = computeOutput(dataset);
        double E = 0.0;
        switch (error_protocol) {
            case MSE:
                for (Matrix2D row : Y_expected.rows()) {
                    try {
                        Matrix2D dE = Y.sub(Y_expected);
                        dE = dE.T().mult(dE).mult(0.5);
                        E += dE.get(0, 0);
                    } catch(MatrixIndexOutOfBoundsException|MatrixDimensionException mde) {
                        mde.printStackTrace();
                        return -1.0;
                    }
                }
                break;
            default:
                return -1.0;
        }
        return E;
    }

    public Matrix2D forwardPropagate(Matrix2D X) {
        return forwardPropagate(X, weights);
    }
    private Matrix2D forwardPropagate(Matrix2D X, LinkedList<Matrix2D> w) {
        try {
            if (w.size() > 1) {
                    LinkedList<Matrix2D> post = new LinkedList<Matrix2D>();
                    for (int i = 1; i < w.size(); i++) {
                        post.add(w.get(i));
                    }
                    return forwardPropagate(X.mult(w.get(0)), post);

            } else {
                return X.mult(w.get(0));
            }
        } catch (MatrixIndexOutOfBoundsException miobe) {
            miobe.printStackTrace();
        } catch (MatrixDimensionException mde) {
            mde.printStackTrace();
        }
        return null;
    }

    public String toString() {
        String tot = "";
        for (int i = 0; i < weights.size(); i++) {
            tot += String.format("W%d:\n%s\n", i, weights.get(i).toString());
        }
        return tot;
    }
}
