# JavaML
Basic object-oriented java implementation of neural network optimization.
## Overview
JavaML, as it stands, is a very bare-bones implementation of traditional neural network backpropagation in the sense that there is not (yet) support for activation functions or complex layer operations (such as convolutions or pooling). I built this framework as a simple multi-layer perceptron(MLP) sandbox to use for my own personal projects and endeavors in machine learning.

I could have decided to use any number of standard machine learning libraries such as Keras or TensorFlow to serve this purpose, but I wanted to use a system that I had designed myself, so as to know that I have full control and comprehension of the components that make it work. On top of that, to make the user-interface of my framework simple and comprehensible, I wanted it to be deeply object-oriented. My strongest experience with object-oriented programming is in Java, so I decided to use it to build my framework, JavaML.

I plan to update this and add more capability as my needs for my projects build in complexity. However, at the time of writing this, my needs call only for a simplistic generalized MLP optimization platform.

## Reference

At the moment, the user *needs* only to interface with three classes to optimize an MLP model on their data: `javaml.Dataset`, `javaml.Network` and `javaml.math.Matrix2D`.

### Matrix2D
The `Matrix2D` class is exactly what it sounds like: an object that stores a two-dimensional matrix. Memory-wise, it stores the matrix in a one-dimensional array of doubles.  The following static methods can be used to instantiate matrices:
|Method  | Description |
|--|--|
| `Matrix2D.zeros(int m, int n)` | Returns a matrix $M\in\mathbb{R}^{m\times n}$ wherein every entry is $0$.|
|`Matrix2D.ones(int m, int n)`|Returns a matrix $M\in\mathbb{R}^{m\times n}$ wherein every entry is $1$.|
|`Matrix2D.rand(int m, int n, double min, double max)`|Returns a matrix $M\in\mathbb{R}^{m\times n}$ wherein every entry is a random value, $M_{ij} \in \left(\mathrm{min, max}\right)$|

The following elementary operations can be done with matrices using the following methods:

|Operation| Code |
|--|--|
| $A+B$ | `A.add(B)` |
|$A-B$|`A.sub(B)`|
|$AB$|`A.mult(B)`|
|$17.2A$|`A.mult(17.2)`|
|$A\bigodot B$*|`A.hadamard(B)`|
<sub>*The Hadamard product is element-wise multiplication</sub>

### Dataset
The `Dataset` class serves as a generalized data parsing vessel that separates training data into features(input nodes) and classes (output nodes), as designated by the user.

The user can use a Matrix2D or a .csv spreadsheet to instantiate a dataset like so:

```
//Both of these instantiations are for a dataset
//that maps two inputs to two outputs.

//Matrix2D implementation:
int numEntries = 256;
Matrix2D random = Matrix2D.rand(numEntries, 4, 0, 1);
Dataset datasetMat = new Dataset(random, 2, 2);

//CSV implementation:
Dataset datasetCSV = new Dataset(new File("source.csv"), Dataset.Format.CSV, 2, 2);
```

To isolate the features and classes of a `Dataset` object, they are publicly accessible via the methods:

```
Matrix2D features = dataset.X(); //returns rows of features
Matrix2D classes = dataset.Y(); //returns rows of classes
```

### Network

The `Network`  class is where all of the learning happens. First, you design the topology of the network when you instantiate it. The arguments of `Network`'s constructor is a list of integers, each which represent the size of each subsequent layer. So, the instantiation `Network n = new Network(3, 4, 1);` would result in this topology:

![Art from Luis Bermudez's Medium Article](https://cdn-images-1.medium.com/max/1600/1*0UovTUIDiixK3z5ewLMlfg.png)
<sub>Art from Luis Bermudez's Medium Article</sub>

Again, because there is not (yet) support for activations per each layer, the only necessary information to construct a neural network is the dimension of each subsequent layer. To fit a network object to a compatible dataset, one would run the following code:
```
//Read in data from source file into a Dataset object
Dataset data = new Dataset(new File("2features_2classes.csv"), Dataset.Format.CSV, 2, 2);

//Construct a compatible network (two input nodes, two output nodes)
Network n = new Network(2, 5, 5, 2);

//Fit the model to the dataset
n.fit(dataset, Network.Error.MSE, 32000, false);
```

There are a few customizable caveats to the above code:
 * If we want to see the error at every 10 iterations of backpropagation, we simply set the final argument of `fit` to `true`, putting the Network into "verbose mode."
 * There are not yet any other supported loss models apart from MSE (Mean-Squared Error), but in the future one could change that argument to another model.
 * If we want to backpropagate indefinitely until `0.0001` error or less is achieved, we may set the third argument(`iterations`) to `-1`.
	* What if we want to achieve an error other than `0.0001`, such as `0.1`? We simply set the network's $\delta$-value to that value by calling `n.delta = 0.1;` before fitting.

## Appendix : Theory
The backbone of this project is essentially the computerization of the generalized backpropagation equations:

$$
N^k \rightarrow \frac{\partial E_{\mathrm{MSE}}}{\partial W_j} = \sum_{d\in D} \left(X\prod_{i=1}^{j} W_i\right) ^\top \left(Y_d - \hat Y_d\right) \prod_{i=0}^{k-j}W_{k-i}^\top
$$
$$
\rightarrow W'_j = W_j - \gamma\frac{\partial E}{\partial W_j}
$$

Which are written more or less explicitly in `Network.java`'s code. When I add support for activation functions and various loss models, this will be complicated and this section will change. Currently, however, this is the extent of the math required to optimize basic MLPs.