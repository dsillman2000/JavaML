package javaml;

import javaml.math.*;

public class Layer {

    private Matrix2D l;
    private Matrix2D w_after;

    public Layer(int dim) {
        l = Matrix2D.zeros(1, dim);
    }

    public Layer(int dim, Layer l_after) {
        this(dim);
        link(l_after);
    }

    public void set(Matrix2D values) {
        if(values.dim() == l.dim()) {
            l = values;
        }
    }

    public int dim() {
        return l.dim()[1];
    }

    public Matrix2D get_weights() {
        return w_after;
    }

    public Matrix2D get_values() {
        return l;
    }

    public void link(Layer l_after) {
        w_after = Matrix2D.rand(l.dim()[1], l_after.dim(), 0, 1);
    }

    public String toString() {
        return l.toString();
    }
}
