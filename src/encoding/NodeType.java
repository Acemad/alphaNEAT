package encoding;

import java.io.Serializable;

/**
 * An enum that represents the different node types in a neural network
 * @author Acemad
 */
public enum NodeType implements Serializable {
    INPUT, BIAS, HIDDEN, OUTPUT
}
