# alphaNEAT

alphaNEAT is a flexible, experimental NEAT implementation written in Java. It is inspired by the original 
Kenneth O. Stanley's C++ NEAT [implementation](https://github.com/F3R70/NEAT), Colin Green's 
[SharpNEAT](https://sharpneat.sourceforge.io/), and the [evo-NEAT](https://github.com/vishnugh/evo-NEAT) 
Java implementation by vishnugh.

## What is NEAT?

NEAT stands for *NeuroEvolution of Augmenting Topologies*, it describes a Neuroevolution algorithm that tries to find a 
suitable neural network for solving a given problem. NEAT employs a genetic algorithm to search for an optimal neural 
network. Most importantly, NEAT does not require users to provide the topology of the network
to optimize, instead, it evolves network topologies through a complexification process and adapts the classic genetic
operators to work on different topologies. For an in-depth description consult the 
[original paper](http://nn.cs.utexas.edu/keyword?stanley:ec02).

## alphaNEAT's features

- Externally defined algorithm parameters as a Java Parameters file. (Parameter descriptions in the 
[`NEATConfig`](https://github.com/Acemad/alphaNEAT/blob/master/src/main/java/engine/NEATConfig.java) class)
- Centralized RNG (Random Number Generator) using the apache-commons rng package.
- Support for resuming interrupted evolution through Java's object serialization/deserialization mechanism.
- Support for concurrent evaluation of networks across multiple threads.
- Continuous computation of evolution statistics and support for saving stats in CSV format.
- Possibility to start evolution with disconnected input neurons to force feature selection.
- Activation mutation operator *(Experimental)*: mutate nodes' activation functions to one of the allowed functions.
- Link reorientation mutation *(Experimental)*: mutate a network by reorienting one of the links.
- Link filtering: impose restrictions on the proportions of each link type (loops, recurrent).
- [Phased Search](https://sharpneat.sourceforge.io/phasedsearch.html): allows transition to a simplification phase if 
mean complexity surpasses a given threshold.
  - Species phased search: only species with high mean complexity transition to simplification.
  - *(On simplification, node deletion and link deletion operators take effect)*
- Dead-end (dangling) nodes repair mechanism.
- Simple API.

## Download

Precompiled JARs with all dependencies are available in the [releases](https://github.com/Acemad/alphaNEAT/releases) 
page.

## Usage

The following is a simple usage example for the classic XOR problem domain. The `evalXOR` method is located in
[`XORExample.java`](https://github.com/Acemad/alphaNEAT/blob/master/src/main/java/examples/XORExample.java). 
This snippet will run the NEAT evolution for 1000 generations using the configurations provided. Sample configurations 
for the XOR example are given in [`xor\xorConfigs.cfg`](https://github.com/Acemad/alphaNEAT/blob/master/xor/xorConfigs.cfg).

```java
import engine.ANEAT;
import examples.XORExample;

public class Example {
    
    public static void main(String[] args) {
        String configPath = "path/to/neat/config/file";
        int generations = 1000;
        ANEAT aneat = new ANEAT(configPath);
        aneat.run(XORExample::evalXOR, generations, null);
    }
}
```

For more details concerning saving/resuming evolution please consult [`XORExample.java`](https://github.com/Acemad/alphaNEAT/blob/master/src/main/java/examples/XORExample.java).

## To-Do

- Alleviate the somewhat high memory usage.
- Transition from Java's serialization/deserialization format to more lightweight formats (json, yaml).
- Include example evaluation functions for more problem domains (eg. pole balancing).
- General code improvements and optimizations.

## Problems

If you find any bug or problem with the code please open an issue.

## Contributing

All contributions are welcome!