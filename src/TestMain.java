import javaml.math.*;
import javaml.*;

import java.io.File;

public class TestMain {
    public static void main(String[] args) {

        Matrix2D.random.setSeed(1);

        //(random) Matrix2D
        /*Matrix2D randomness = Matrix2D.rand(128, 4, 0, 5);
        Dataset dataset = new Dataset(randomness, 3, 1);
        Network n = new Network(3,4,3,4,1);
        System.out.println(n);
        //n.fit(dataset, Network.Error.MSE, 13000, false);
        System.out.println(n);*/

        //CSV
        Dataset csv = new Dataset(new File("src/test.csv"), Dataset.Format.CSV, 2, 2);
        System.out.println(csv);
        Network n2 = new Network(2,5,5,2);
        System.out.println(n2);
        n2.fit(csv, Network.Error.MSE, -1, false);
        System.out.println(n2);
    }
}
