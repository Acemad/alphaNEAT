package util;

import encoding.Genome;
import encoding.LinkGene;
import encoding.NodeGene;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.layout.HierarchicalLayout;
import org.graphstream.ui.view.Viewer;

/**
 * Utility class for the visualization of a Genome using a third party graph visualization library
 *
 * @author Acemad
 */
public class Visualizer {

    private static org.graphstream.graph.Graph genomeToGraphGStream(Genome genome) {
        org.graphstream.graph.Graph graph = new SingleGraph("genome");
        //graph.setStrict(false);
        //graph.setAutoCreate(true);

        for (NodeGene nodeGene : genome.getNodeGenes()) {
            graph.addNode(Long.toString(nodeGene.getId()))
                    .setAttribute("ui.label", nodeGene.getId());
        }

        for (LinkGene linkGene : genome.getLinkGenes()) {
            if (linkGene.isEnabled())
                graph.addEdge("E" + linkGene.getId(),
                        Long.toString(linkGene.getSourceNodeId()),
                        Long.toString(linkGene.getDestinationNodeId()), true)
                        .setAttribute("ui.label", linkGene.getId());
        }
        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        return graph;
    }

    public static void showGStream(Genome genome) {
        System.setProperty("org.graphstream.ui", "swing");
        HierarchicalLayout hierarchicalLayout = new HierarchicalLayout();
        Viewer viewer = genomeToGraphGStream(genome).display(false);
        viewer.enableAutoLayout(hierarchicalLayout);
    }

}
