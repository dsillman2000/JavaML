import javaml.math.*;
import javaml.*;

public class TestMain {
    public static void main(String[] args) {
        Matrix2D randomness = Matrix2D.rand(128, 4, 0, 5);
        Dataset dataset = new Dataset(randomness, 3, 1);
        Network n = new Network(3,4,3,4,1);
        System.out.println(n);
        n.fit(dataset, Network.Error.MSE, 130000, false);
        System.out.println(n);
    }
}
