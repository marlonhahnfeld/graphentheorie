package gkap.aufgabe1.aufgabe3;

import org.graphstream.algorithm.Kruskal;
import org.graphstream.algorithm.generator.FullGenerator;
import org.graphstream.algorithm.generator.GridGenerator;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.Graphs;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.*;
import java.util.stream.Collectors;

import static org.graphstream.algorithm.Toolkit.randomNode;


public class TSPAlgorithms {

    /**
     * berechnet den kürzesten Pfad anhand der miminalen Spannbaum Heuristik
     *
     * @param g
     * @return graph
     */
    public static Graph minimumSpanningTreeHeuristic(Graph g) {
        if (g.getNodeCount() == 0) {
            throw new IllegalArgumentException("Graph besitzt keine Knoten");
        }
        if (!isGraphComplete(g)) {
            throw new IllegalArgumentException("Graph ist kein vollständiger Graph");
        }
        Graph graph = new MultiGraph("TSP");
        graph.setAutoCreate(true);
        graph.setStrict(false);
        //mst
        Graph spanningTree = kruskalAlgorithm(g);
        //Kanten verdoppeln
        doubleEdges(spanningTree);
        //Eulertour
        List<Node> tour = eulerTour(spanningTree);
        //aus der tour doppelt besuchte knoten entfernen
        List<Node> mshtour = new ArrayList<>(new LinkedHashSet<>(tour));
        //endknoten hinzufügen, welcher gleichzeitig der startknoten ist
        mshtour.add(mshtour.get(0));
        // neue Tour als Graphen erstellen
        for (int i = 0; i < mshtour.size() - 1; i++) {
            graph.addEdge(String.valueOf(i), mshtour.get(i).toString(), mshtour.get(i + 1).toString())
                    .setAttribute("weight", g.getNode(mshtour.get(i).getId()).getEdgeBetween(g.getNode(mshtour.get(i + 1).getId())).getAttribute("weight"));
        }
        int weight = graph.edges().mapToInt(x -> (Integer) x.getAttribute("weight")).sum();
        graph.setAttribute("weight", weight);
        String listString = mshtour.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
        graph.setAttribute("shortestPath", listString);
        return graph;
    }

    /**
     * Erzeugt einen MST mithilfe von Kruskal
     *
     * @param graph
     * @return
     */
    public static Graph kruskalAlgorithm(Graph graph) {
        if (graph.getNodeCount() == 0) {
            throw new IllegalArgumentException("graph has no nodes");
        }
        Graph clone = Graphs.clone(graph);
        Kruskal kruskal = new Kruskal("weight", "edgeInTree");
        kruskal.init(graph);
        kruskal.compute();
        graph.edges().filter(x -> !((Boolean) x.getAttribute("edgeInTree"))).forEach(n -> clone.removeEdge(n.getId()));
        return clone;
    }

    /**
     * Verdoppelt alle Kanten für den MST
     *
     * @param graph
     */
    public static void doubleEdges(Graph graph) {
        if (graph.getNodeCount() == 0 || graph == null) {
            throw new IllegalArgumentException("graph has no nodes or does not exist");
        }
        for (Edge e : graph.edges().toList()) {
            graph.addEdge("0+" + (graph.getEdgeCount() + 1), e.getSourceNode(), e.getTargetNode()).setAttribute("weight", e.getAttribute("weight"));
        }
    }


    /**
     * Erzeugt eine Eulertour für den gegebenen Graphen
     *
     * @param graph
     * @return
     */
    public static List<Node> eulerTour(Graph graph) {
        if (graph.getNodeCount() == 0) {
            throw new IllegalArgumentException("graph has no nodes");
        }
        // graph clonen damit wir ihn verändern können
        Graph g = Graphs.clone(graph);
        //StartNode festlegen
        Node startnode = g.getNode(0);
        //Liste für Eulerkreis erstellen und startknoten einfügen
        List<Node> eulerkreis = new ArrayList<>();
        eulerkreis.add(startnode);
        // einmalig für schleife initialisieren
        Node node = startnode;
        List<Node> usedNodes = new ArrayList<>();
        Node node2;

        int c = g.getEdgeCount();
        for (int i = 0; i < c; i++) {
            // node2 auswählen und filtern, dass node2 bei mehreren Nachbarn weiter geht und nicht zurück.
            if (!usedNodes.containsAll(node.neighborNodes().toList())) {
                node2 = node.neighborNodes().filter(n -> !usedNodes.contains(n)).findAny().get();
            } else {
                node2 = node.neighborNodes().findAny().get();
            }
            // gefundenen Nachbarknoten in Eulerkreis fügen
            eulerkreis.add(node2);
            // benutzte Kante aus g entfernen um sicherzustellen dass keine Kante doppelt benutzt wird
            Edge e = node.getEdgeBetween(node2);
            g.removeEdge(e);

            usedNodes.add(node);
            node = node2;
        }
        return eulerkreis;
    }


    public static Boolean isGraphComplete(Graph g) {
        Boolean b = true;
        int edgeCount = (int) g.edges().count();
        int nodeCount = g.getNodeCount();
        int formel = ((nodeCount * (nodeCount - 1)) / 2);
        if (edgeCount != formel) {
            b = false;
        }
        return b;
    }


    /**
     * Generiert einen kompletten metrischen Graphen mithilfe der Dreiecksungleichhung
     *
     * @param nodeCount Die Anzahl der Knoten, die der Graph besitzen soll
     * @return Einen Graphen, welcher die Dreiecksungleichung erfüllt
     */
    public static Graph generate(int nodeCount) {
        //multigraph weil doubleEdges aufgerufen wird
        Graph graph = new MultiGraph("CompleteMetricGraph");


        // Full generator generiert vollständigen Graphen
        FullGenerator gen;
        gen = new FullGenerator();

        gen.addNodeLabels(true);
        gen.addSink(graph);

        // fügt den ersten Knoten in den Graphen
        gen.begin();
        // einfügen der Knoten bis nodeCount-1
        for (int i = 1; i < nodeCount; i++) {
            gen.nextEvents();
        }
        gen.end();

        // errechnet die dreiecksungleichung mithilfe der manhattan metric
        //jedem node x und y koordinaten hinzufügen
        setCoordinates(graph);
        setWeights(graph);

        return graph;
    }

    /**
     * Legt zufällig die xy koordinaten für jeden knoten des graphen fest. jede kombination eindeutig
     *
     * @param graph Graphen, bei dem knoten xy koordinaten haben
     */
    private static void setCoordinates(Graph graph) {
        //Erzeugt ein gitter welches für die manhattan metrik benutzt wird
        //die gitter dimension basiert auf der anzahl der nodes
        GridGenerator gen = new GridGenerator();
        Graph grid = new SingleGraph("Grid");

        gen.addSink(grid);
        gen.begin();
        // bei 3 nodes z.B 3x3 grid
        for (int i = 1; i < graph.getNodeCount(); i++) {
            gen.nextEvents();
        }
        gen.end();

        //kopiert xy attribute eines zufälligen knoten auf dem gitter und fügt es einem knoten hinzu
        graph.nodes().forEach(node -> node.setAttribute("xy", grid.removeNode(randomNode(grid)).getAttribute("xy")));
    }

    /**
     * setzt die weight attribute für alle kanten des graphen mithilfe der xy koordinaten der knoten durch die manhattan metric
     *
     * @param graph Graphen dessen kanten gewichtet werden sollen
     */
    private static void setWeights(Graph graph) {
        graph.edges().forEach(edge -> {
            int distance = getDistance(edge);
            edge.setAttribute("weight", distance);
        });
    }

    /**
     * Gibt die Distanz zweier Knoten mithilfe der manhattan metric zurück
     *
     * @param edge Die zu berechnende Kante
     * @return Die Distanz der Kante
     */
    private static int getDistance(Edge edge) {
        Double[] node1coordinates = edge.getNode0().getAttribute("xy", Double[].class);
        Double[] node2Coordinates = edge.getNode1().getAttribute("xy", Double[].class);

        //manhatten distanz zwischen punkten x1y1 und x2y2 = x1-x2 +y1-y2
        return (int) (Math.abs(node1coordinates[0] - node2Coordinates[0]) + Math.abs(node1coordinates[1] - node2Coordinates[1]));
    }


    /**
     * berechnet den kürzesten pfad anhand nearest Insertion
     *
     * @param graph
     * @return
     */
    public static Graph nearestInsertion(Graph graph) {
        if (graph.getNodeCount() == 0) {
            throw new IllegalArgumentException("Graph besitzt keine Knoten");
        }
        if (!isGraphComplete(graph)) {
            throw new IllegalArgumentException("Der eingegebene Graph ist kein vollständiger Graph");
        }

        Graph singleGraph = new SingleGraph("TSP2");
        singleGraph.setAutoCreate(true);
        singleGraph.setStrict(false);
        //Starknoten initialiserung
        Node node = graph.getNode(0);
        List<Node> way = new ArrayList<>();
        //Start und endknoten
        way.add(node);
        way.add(node);

        for (int k = 0; k < graph.getNodeCount() - 1; k++) {
            // alle ausgehenden Kanten von den besuchten Knoten holen
            Set<Edge> edges = new HashSet<>();
            for (Node v : way) {
                edges.addAll(v.leavingEdges().toList());
            }
            // Wenn in dem Weg der Ausgangsknoten und Ziel Knoten benutzt wurde, kann die Kante entfernt werden
            edges.removeIf(e -> way.contains(e.getSourceNode()) && way.contains(e.getTargetNode()));

            // Edge mit kleinster Kantenbewertung finden um neues node zu finden
            Edge min = Collections.min(edges, Comparator.comparing(x -> (int) x.getAttribute("weight")));

            //Wenn eine Kante einen Startknoten hat, also verbunden ist, dann wird die verbindung mithilfe der kante gefunden (Der Zielknoten)
            if (way.contains(min.getSourceNode())) {
                node = min.getTargetNode();
            }

            //Wenn kein Startknoten mit der Kante verbunden wird, wird ein Startknoten festgelegt
            else {
                node = min.getSourceNode();
            }

            // Neue Edge so einfügen, dass wir den kleinsten Weg bekommen
            List<Integer> possibleWeights = new ArrayList<>();

            if (way.size() < 3) {
                //Neuer Knoten, da bisher nur Start und Enknoten V,V vorhanden. also wird dieser zwischen diese beiden angelegt
                way.add(1, node);
                //Wenn bereits 3 Knoten, also V,Z,V vorhanden sind, dann wird die Position ermittelt
            } else {
                //jeder mögliche Pfad wird in possiblepaths einfügen (mit dem jeweiligen neuen Knoten)
                //Bsp.: V X Y V
                //      V Y X V
                for (int i = 0; i < way.size() - 1; i++) {
                    int weigth = 0;

                    //Gewicht ermitteln zwischen Knoten i und aktuellem Knoten
                    weigth += Integer.parseInt(way.get(i).getEdgeBetween(node).getAttribute("weight").toString());

                    //Gewicht ermitteln zwischen aktuellem Knoten und Knoten i+1
                    weigth += Integer.parseInt(node.getEdgeBetween(way.get(i + 1)).getAttribute("weight").toString());

                    //Pfad erstellen nach neuem Knoten (Jede Möglichkeit mit neuem Knoten)
                    for (int j = i + 1; j < way.size() - 1; j++) {
                        weigth += Integer.parseInt(way.get(j).getEdgeBetween(way.get(j + 1)).getAttribute("weight").toString());
                    }
                    possibleWeights.add(weigth);
                }

                //index ermitteln / günstigste postion ermitteln und den neuen Knoten dort einfügen
                int minWeight = Collections.min(possibleWeights);
                singleGraph.setAttribute("weight", minWeight);
                //Knoten hinzufügen zum Pfad
                way.add(possibleWeights.indexOf(minWeight) + 1, node);
            }

        }

        //besuchte Knoten in einen TSP Graphen umwandeln
        for (int i = 0; i < way.size() - 1; i++) {
            singleGraph.addEdge(String.valueOf(i), way.get(i).toString(), way.get(i + 1).toString())
                    .setAttribute("weight", graph.getNode(way.get(i).getId()).getEdgeBetween(graph.getNode(way.get(i + 1).getId())).getAttribute("weight"));
        }
        //shortestPath wird als String dargestellt und gespeichert
        String listString = way.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
        //shortestPath hinzufügen
        singleGraph.setAttribute("shortestPath", listString);

        return singleGraph;
    }


    public static Integer getTourLength(Graph g) {
        int counter = g.edges().mapToInt(edge -> (int) edge.getAttribute("weight")).sum();
        return counter;
    }

    /**
     * überprüft, ob die Route in dem Vergleichsgraphen vorhanden ist
     *
     * @param graph
     * @param routeGraph
     * @return
     */

    public static boolean routeExist(Graph graph, Graph routeGraph) {
        String route = (String) routeGraph.getAttribute("shortestPath");
        List<String> nodeIndexs = Arrays.stream(route.split(" ")).toList();
        for (int i = 0; i + 1 < nodeIndexs.size(); i++) {
            if (!graph.getNode(nodeIndexs.get(i)).hasEdgeToward(nodeIndexs.get(i + 1))) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {

        long startZeit;
        long stopZeit;
        long ergZeit;
        long gesamt = 0;

        int start = 100;

        for (int i = 0; i < 20; i++) {
            start += 20;
            Graph bspGraph = generate(start);

            startZeit = System.currentTimeMillis();
            Graph mshGraph = minimumSpanningTreeHeuristic(bspGraph);
            stopZeit = System.currentTimeMillis();
            ergZeit = stopZeit - startZeit;
            gesamt += ergZeit;
            System.out.println(i + " = Erg MSH: (" + ergZeit + " ms) - Rundreise: " +
                    TSPAlgorithms.getTourLength(mshGraph) + " Doppl. min. Spannbaum = " +
                    TSPAlgorithms.getTourLength(kruskalAlgorithm(mshGraph)) * 2);

            startZeit = System.currentTimeMillis();
            Graph niaGraph = nearestInsertion(bspGraph);
            stopZeit = System.currentTimeMillis();
            ergZeit = stopZeit - startZeit;
            gesamt += ergZeit;
            System.out.println(i + " = Erg NIA: (" + ergZeit / 1000 + " s) - Rundreise: " +
                    TSPAlgorithms.getTourLength(niaGraph));

            System.out.println("-------------------------------------");

        }
        System.out.println(" Erg gesamt: " + gesamt + " = " + (gesamt / 1000) / 60 + " min");
    }


}