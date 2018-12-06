package javaml.math;

public class MatrixIndexOutOfBoundsException extends Exception{
    String msg;

    public MatrixIndexOutOfBoundsException() {
        this.msg = null;
    }

    public MatrixIndexOutOfBoundsException(String msg) {
        this.msg = msg;
    }

    public String getMessage() {
        return msg;
    }
}
