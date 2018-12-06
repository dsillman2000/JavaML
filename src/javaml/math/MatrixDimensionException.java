package javaml.math;

public class MatrixDimensionException extends Exception {
    String msg;
    public MatrixDimensionException() {
        this.msg = null;
    }
    public MatrixDimensionException(String msg) {
        this.msg = msg;
    }
    public String getMessage() {
        return msg;
    }
}
