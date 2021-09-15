package util;

import java.util.Objects;

/**
 * A representation of a simple link between two objects
 *
 * @author Acemad
 */
public class Link {

    private final int source;
    private final int destination;

    public Link(int source, int destination) {
        this.source = source;
        this.destination = destination;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Link link = (Link) o;
        return source == link.source && destination == link.destination;
    }

    @Override
    public int hashCode() {
        return Objects.hash(source, destination);
    }

    @Override
    public String toString() {
        return "Pair{" + "a=" + source + ", b=" + destination + '}';
    }
}
