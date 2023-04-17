package gkap.aufgabe1.aufgabe2;


import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static gkap.aufgabe1.aufgabe1.PrueferTreeAlgorithms.generateTree;
import static gkap.aufgabe1.aufgabe2.GraphAlgorithms.*;
import static org.junit.Assert.*;

public class GraphAlgorithmsTest {

    /**
     * Tests zum überprüfen, ob list1 kleiner als list2 ist
     */
    @Test
    public void testCompareList() {
        assertFalse(compareList(List.of(11, 1), List.of(10)));
        assertFalse(compareList(List.of(7, 10), List.of(7, 10)));
        assertTrue(compareList(List.of(10), List.of(7, 10)));
        assertTrue(compareList(List.of(10), List.of(11)));
    }

    /**
     * Test für den LEX-BFS, ob in richtiger Reihenfolge eleminiert wird
     */
    @Test
    public void testLexBFS_1() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph graph = new SingleGraph("test");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        Node node1 = graph.addNode(String.valueOf(1));
        Node node2 = graph.addNode("2");
        Node node3 = graph.addNode("3");
        Node node4 = graph.addNode("4");
        Node node5 = graph.addNode("5");
        graph.addEdge("1", 3, 0);
        graph.addEdge("2", 1, 3);
        graph.addEdge("3", 0, 4);
        graph.addEdge("4", 1, 4);
        graph.addEdge("5", 3, 4);
        graph.addEdge("6", 1, 2);
        List<Node> ordering = List.of(node3, node2, node5, node4, node1);
        assertEquals(ordering, pes(graph));
    }

    /**
     * Test für den LEX-BFS, ob in richtiger Reihenfolge eleminiert wird
     */
    @Test
    public void testLexBFS_2() {
        System.setProperty("org.graphstream.ui", "swing");
        Graph g = new SingleGraph("test");
        g.setAutoCreate(true);
        g.setStrict(false);
        Node node1 = g.addNode(String.valueOf(1));
        Node node2 = g.addNode("2");
        Node node3 = g.addNode("3");
        Node node4 = g.addNode("4");
        Node node5 = g.addNode("5");
        g.addEdge("d1", "1", "2");
        g.addEdge("d2", "2", "3");
        g.addEdge("d3", "3", "4");
        g.addEdge("d4", "4", "1");
        g.addEdge("d5", "3", "1");
        g.addEdge("d6", "1", "5");
        List<Node> ordering = List.of(node5, node4, node3, node2, node1);
        assertEquals(ordering, pes(g));
    }

    /**
     * Prüft, ob der eingebenen Graph chordal ist
     */
    @Test
    public void testIsChordal_1() {
        Graph graph1 = new SingleGraph("test");
        graph1.setStrict(false);
        graph1.setAutoCreate(true);
        graph1.addNode("1");
        graph1.addNode("2");
        graph1.addNode("3");
        graph1.addNode("4");
        graph1.addNode("5");
        graph1.addEdge("1", 3, 0);
        graph1.addEdge("2", 1, 3);
        graph1.addEdge("3", 0, 4);
        graph1.addEdge("4", 1, 4);
        graph1.addEdge("6", 1, 2);
        boolean result = isChordal(graph1);
        assertFalse(result);
    }

    /**
     * Prüft, ob der eingebenen Graph chordal ist
     */
    @Test
    public void testIsChordal_2() {
        Graph graph2 = new SingleGraph("test");
        graph2.setStrict(false);
        graph2.setAutoCreate(true);
        graph2.addEdge("ab", "a", "b");
        graph2.addEdge("ac", "a", "c");
        graph2.addEdge("bc", "b", "c");
        graph2.addEdge("bd", "b", "d");
        graph2.addEdge("be", "b", "e");
        graph2.addEdge("dc", "d", "c");
        graph2.addEdge("de", "d", "e");
        graph2.addEdge("ce", "c", "e");
        boolean result = isChordal(graph2);
        assertTrue(result);
    }

    /**
     * Testet, ob ein random genierter chordalerGraph chordal ist
     */
    @Test
    public void testIsChordalWithRandomChordalGraph() {
        Random random = new Random();
        for (int i = 0; i < 10; i++) {
            int numOfParentNode = random.nextInt(500) + 1;
            Graph randomChordalGraph = generateRandomChordalGraph(numOfParentNode);
            assertTrue(isChordal(randomChordalGraph));
        }
    }

    /**
     * Testet, ob die chromatische Zahl übereinstimmt
     */
    @Test
    public void testChromaticNumbers() {
        Graph graph = new SingleGraph("graph");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.addEdge("e1", "1", "2");
        graph.addEdge("e2", "2", "3");
        graph.addEdge("e3", "3", "4");
        graph.addEdge("e4", "4", "2");
        List<Integer> chromaticNumbersGraphs = new ArrayList<>();
        //Jede Zahl der Chromatischen Zahl ist eine Farbe
        List<Integer> chromaticNumbers = List.of(3);
        chromaticNumbersGraphs.add(getChromaticNumber(graph));
        Assert.assertEquals(chromaticNumbers, chromaticNumbersGraphs);

    }

    /**
     * Testet, ob die chromatische Zahl des random generierten vollständigen Graph korrekt ist
     */
    @Test
    public void testGetChromaticNumberRandomCompleteGraphs() {
        for (int i = 0; i < 10; i++) {
            Graph g = generateRandomCompleteGraph();
            assertEquals(g.getNodeCount(), getChromaticNumber(g));
        }
    }

    /**
     * Testet, ob optimal gefärbt wurde
     */
    @Test
    public void testGetOptimalColoringisValidColoring() {
        Graph graph = new SingleGraph("graph");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.addEdge("e1", "1", "2");
        graph.addEdge("e2", "2", "3");
        graph.addEdge("e3", "3", "4");
        graph.addEdge("e4", "4", "2");
        assertTrue(isValidColoring(getPerfectColoring(graph)));
    }

    /**
     * Testet, ob der Graph richtig gefärbt wurde, dann true
     */
    @Test
    public void testisValidColoringTrue() {
            Graph graph1 = generateTree(50);
            Graph graph2 = generateRandomCompleteGraph();
            assertTrue(isValidColoring(getPerfectColoring(graph1)));
            assertTrue(isValidColoring(getPerfectColoring(graph2)));

    }

    /**
     * * Testet, ob der Graph falsch gefärbt wurde, dann false
     */
    @Test
    public void testisValidColoringFalse() {
        Graph graph = new SingleGraph("test");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.addEdge("e1", "1", "2");
        graph.addEdge("e2", "2", "3");
        for (Node n : graph
        ) {
            n.setAttribute("color", 1);
        }
        assertFalse(isValidColoring(graph));
    }


}
