package gkap.aufgabe1.aufgabe3;


import gkap.aufgabe1.aufgabe3.TSPAlgorithms;
import org.graphstream.graph.Graph;

import static org.junit.Assert.*;

import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;
import org.junit.Test;


public class Aufgabe3Test {

    public Graph testCompleteGraph() {

        System.setProperty("org.graphstream.ui", "swing");
        Graph g = new SingleGraph("CompleteGraph");
        g.setAutoCreate(true);
        g.setStrict(false);
        Node node1 = g.addNode(String.valueOf(1));
        Node node2 = g.addNode("2");
        Node node3 = g.addNode("3");
        Node node4 = g.addNode("4");
        Node node5 = g.addNode("5");
        g.addEdge("d12", "1", "2");
        g.addEdge("d13", "1", "3");
        g.addEdge("d14", "1", "4");
        g.addEdge("d15", "1", "5");
        g.addEdge("d23", "2", "3");
        g.addEdge("d24", "2", "4");
        g.addEdge("d25", "2", "5");
        g.addEdge("d34", "3", "4");
        g.addEdge("d35", "3", "5");
        g.addEdge("d45", "4", "5");
        //Viewer viewer = g.display();
        return g;
    }

    public Graph testNotCompleteGraph() {

        System.setProperty("org.graphstream.ui", "swing");
        Graph g = new SingleGraph("CompleteGraph");
        g.setAutoCreate(true);
        g.setStrict(false);
        Node node1 = g.addNode(String.valueOf(1));
        Node node2 = g.addNode("2");
        Node node3 = g.addNode("3");
        Node node4 = g.addNode("4");
        Node node5 = g.addNode("5");
        g.addEdge("d12", "1", "2");
        g.addEdge("d23", "2", "3");
        g.addEdge("d24", "2", "4");
        g.addEdge("d25", "2", "5");
        g.addEdge("d34", "3", "4");
        g.addEdge("d35", "3", "5");
        g.addEdge("d45", "4", "5");

        return g;
    }


    public static Graph testCompleteGraph2() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph g = new MultiGraph("CompleteGraph");
        g.setAutoCreate(true);
        g.setStrict(false);
        Node v = g.addNode("v");
        Node w = g.addNode("w");
        Node x = g.addNode("x");
        Node y = g.addNode("y");
        Node z = g.addNode("z");
        g.addEdge("vw", "v", "w").setAttribute("weight", 6);
        g.addEdge("vx", "v", "x").setAttribute("weight", 15);
        g.addEdge("vy", "v", "y").setAttribute("weight", 9);
        g.addEdge("vz", "v", "z").setAttribute("weight", 12);

        g.addEdge("xw", "x", "w").setAttribute("weight", 11);
        g.addEdge("xz", "x", "z").setAttribute("weight", 4);
        g.addEdge("xy", "x", "y").setAttribute("weight", 12);

        g.addEdge("wy", "w", "y").setAttribute("weight", 8);
        g.addEdge("wz", "w", "z").setAttribute("weight", 8);

        g.addEdge("zy", "z", "y").setAttribute("weight", 15);

        return g;

    }
    public Graph testCompleteGraphKruskal() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph g = new MultiGraph("CompleteGraph");
        g.setAutoCreate(true);
        g.setStrict(false);
        Node v = g.addNode("v");
        Node w = g.addNode("w");
        Node x = g.addNode("x");
        Node y = g.addNode("y");
        Node z = g.addNode("z");
        g.addEdge("vw", "v", "w").setAttribute("weight", 6);
        g.addEdge("vx", "v", "x").setAttribute("weight", 15);
        g.addEdge("vy", "v", "y").setAttribute("weight", 9);
        g.addEdge("vz", "v", "z").setAttribute("weight", 12);

        g.addEdge("xw", "x", "w").setAttribute("weight", 11);
        g.addEdge("xz", "x", "z").setAttribute("weight", 4);
        g.addEdge("xy", "x", "y").setAttribute("weight", 12);

        g.addEdge("wy", "w", "y").setAttribute("weight", 8);
        g.addEdge("wz", "w", "z").setAttribute("weight", 8);

        g.addEdge("zy", "z", "y").setAttribute("weight", 15);

        g.setAttribute("shortestPath", "v y x z w v");
        return g;

    }


    public Graph testResultCompleteGraph2ForRoute() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph g = new SingleGraph("CompleteGraph");
        g.setAutoCreate(true);
        g.setStrict(false);
        Node v = g.addNode("v");
        Node w = g.addNode("w");
        Node x = g.addNode("x");
        Node y = g.addNode("y");
        Node z = g.addNode("z");

        g.addEdge("vy", "v", "y").setAttribute("weight", 9);
        g.addEdge("yx", "y", "x").setAttribute("weight", 12);
        g.addEdge("xw", "x", "w").setAttribute("weight", 11);
        g.addEdge("wz", "w", "z").setAttribute("weight", 8);
        g.addEdge("zv", "z", "v").setAttribute("weight", 12);

        return g;
    }

    @Test
    public void generateCompleteMetricGraphTest() {
        int nodeCount = 5;
        Graph g = TSPAlgorithms.generate(nodeCount);
        assertTrue(TSPAlgorithms.isGraphComplete(g));
        for (Node node1 : g) {
            for (Node node2 : g) {
                //Keine abgleichung mit dem selben Knoten
                if (node1.equals(node2)) {
                    continue;
                }
                Double[] node1xy = node1.getAttribute("xy", Double[].class);
                Double[] node2xy = node2.getAttribute("xy", Double[].class);
                //Kein Knoten darf gleich sein
                assertFalse((node1xy[0] == node2xy[0]) && (node1xy[1] == node2xy[1]));
            }
        }
        //Test, ob jeder beliebige Knoten miteinander verbunden ist
        assertTrue(g.nodes().allMatch(node -> node.getDegree() == g.getNodeCount() - 1));
        //Test für die vollständigkeit des Attributes weight
        assertTrue(g.edges().allMatch(edge -> edge.hasAttribute("weight")));

    }

    @Test
    public void isGraphCompleteTest() {
        assertFalse(TSPAlgorithms.isGraphComplete(testNotCompleteGraph()));
        assertTrue(TSPAlgorithms.isGraphComplete(testCompleteGraph()));
    }

    @Test
    public void nearestInsertionTest() {

        Graph g = TSPAlgorithms.nearestInsertion(testCompleteGraph2());
        TSPAlgorithms.routeExist(testCompleteGraph2(), g);

    }

    @Test
    public void minimumSpanningTreeHeuristicTest() {
        String expected = "v w y z x v";
        Graph g = TSPAlgorithms.minimumSpanningTreeHeuristic(testCompleteGraph2());
        TSPAlgorithms.routeExist(testCompleteGraph2(), g);
        assertEquals(expected,g.getAttribute("shortestPath"));

    }

    @Test
    public void kruskalTest() {
        Graph g = TSPAlgorithms.kruskalAlgorithm(testCompleteGraphKruskal());
        TSPAlgorithms.routeExist(testCompleteGraphKruskal(), g);
    }

    @Test
    public void testGetTour() {
        Graph graph = TSPAlgorithms.generate(5);
        Graph mstGraph = TSPAlgorithms.kruskalAlgorithm(graph);
        // kantengewichtung von kruskaltour herausfinden
        int mstLength = TSPAlgorithms.getTourLength(TSPAlgorithms.kruskalAlgorithm(mstGraph));
        int mstTimes2 = 2 * mstLength;
        // gesamte kantengewichtung herausfinden
        int tourLengthNIA = TSPAlgorithms.getTourLength(TSPAlgorithms.nearestInsertion(graph));
        int tourLengthMsh = TSPAlgorithms.getTourLength(TSPAlgorithms.minimumSpanningTreeHeuristic(graph));
       // System.out.println(TSPAlgorithms.getTourLength(TSPAlgorithms.nearestInsertion(graph)));
        assertTrue( tourLengthNIA <= mstTimes2);
        assertTrue( tourLengthMsh <= mstTimes2);
    }



}
