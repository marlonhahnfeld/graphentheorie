package gkap.aufgabe1.aufgabe2;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.ui.view.Viewer;

import java.util.*;
import java.util.stream.Collectors;


public class GraphAlgorithms {

    /**
     *
     * Vergleicht zwei Listen und prüft, ob list kleiner als list2 ist
     * @param list1
     * @param list2
     * @return true, if list1 < list2
     */
    public static boolean compareList(List<Integer> list1, List<Integer> list2) {
        int minLength = Math.min(list1.size(), list2.size());
        for (int i = 0; i < minLength; i++) {
            if (list1.get(i) < list2.get(i)) return true;
        }
        if (list1.size() < list2.size()) return true;
        return false;
    }


    /**
     * Gibt das Perfekte Eleminationsschema Reihenfolge des chordalen Graphen an
     * @param graph
     * @return Liste der Nodes in PES
     */
    public static List<Node> pes(Graph graph) {
        if (graph.getNodeCount() <= 0) {
            throw new IllegalArgumentException("Graph hat keine Noten");
        }
        // Map von Knoten und dessen labels
        HashMap<Node, List<Integer>> labels = new HashMap<>();
        List<Node> pesErg = new ArrayList<>();
        // forall v in V do label(v), leeres label für jedes Node anlegen
        graph.nodes().forEach(node -> {
            labels.put(node, new ArrayList<>());
        });
        List<Node> nodes = graph.nodes().toList();
        //for i <- n bis 1
        for (int i = nodes.size(); i > 0; i--) {
            //Größtes Node von jeder Iteration (nach jedem schritt zurücksetzen)
            Node biggest = null;
            //Liste der nicht besuchten Nodes
            List<Node> notVisited = nodes.stream()
                    .filter(n -> !pesErg.contains(n)).toList();
            for (int j = 0; j < notVisited.size(); j++) {
                if (biggest == null) {
                    //NotVisited bereits sortiert durch PES
                    biggest = notVisited.get(j);
                } else {
                    // Vergleich von biggest und Node an Stelle von j
                    if (compareList(labels.get(biggest), labels.get(notVisited.get(j))))
                        biggest = notVisited.get(j);
                }
            }
            if (biggest != null) {
                // update Nachbar-Knoten ohne label
                List<Node> nichtNummierteNachbarn = biggest.neighborNodes()
                        .filter(node -> !pesErg.contains(node)).toList();
                // Für jedes Node der nicht Nummerierten Nachbarn
                for (Node node : nichtNummierteNachbarn) {
                    // Der nicht nummerierte-Nachbar bekommt das label vom Nachbarn
                    List<Integer> label = labels.get(node);
                    label.add(labels.get(node).size(), i);
                    labels.put(node, label);
                }
                pesErg.add(biggest);
            }
        }
        Collections.reverse(pesErg);
        return pesErg;
    }

    /**
     * Prüft, ob der Graph chrodal ist
     * @param graph
     * @return true wenn chordal, ansonsten false
     */

    public static boolean isChordal(Graph graph) {
        if (graph.getNodeCount() <= 0) {
            throw new IllegalArgumentException("graph beitzt keine Knoten");
        }
        // PES wird angewendet
        List<Node> pesErg = pes(graph);
        HashMap<Node, List<Node>> av = new HashMap<>();
        // for all v∈V do  set A(v) = empty list
        // Die Knoten werden in geordneter Reihenfolge in einer Liste eingefügt
        pesErg.forEach(node -> av.put(node, new ArrayList<>()));
        List<Node> rNeighbors;
        List<Node> neighbors;
        for (int j = 0; j < pesErg.size(); j++) {
            // Nachbarn des jeweiligen Knotens werden in einer Liste aufgelistet
            Node currentNode = pesErg.get(j); //Node_u
            neighbors = currentNode.neighborNodes().collect(Collectors.toList());
            // j+1, da j mit 0 startet 
            if (j + 1 < pesErg.size()) {
                // Bei jeder Iteration wird immer eine SubList erzeugt ohne das erste Node
                List<Node> neighborNodesOfCurrentNode = pesErg.subList(j + 1, pesErg.size());
                //Nachbarn vom CurrentNode
                rNeighbors = neighborNodesOfCurrentNode.stream().filter(neighbors::contains).collect(Collectors.toList());
            } else {
                rNeighbors = new ArrayList<>();

            }
            //Zeile 6 aus Pseudocode
            //if X̸ != empty
            if (!rNeighbors.isEmpty()) {
                //Bestimme w mit σ (w) = min { σ (x) : x ∈ X }
                Node rNeighborWithMinIndex = rNeighbors.get(0);
                rNeighbors.remove(rNeighborWithMinIndex);
                //A(w) := A(w) ∪ (X \ {w})
                // Kleinsten Nachbarn alle Nachbarn in einem Set damit man keine Duplikate hat
                av.put(rNeighborWithMinIndex, new ArrayList<>(new HashSet<>(rNeighbors)));
            }
            //Zeile 9 aus Pseudocode
            //ifA(u)\ADJ[u]̸=0/then
            List<Node> finalNeigbors = neighbors;
            //Wenn er nicht alle Nachbarn beinhaltet, dann ist der Graph nicht chordal
            if (!(av.get(currentNode).stream().filter(node -> !(finalNeigbors.contains(node))).count() == 0))
                return false;
        }
        return true;
    }

    /**
     * Gibt optimale Färbung eines chordalen Graphen an
     * @param graph
     * @return gefärbten graph
     */
    public static Graph getPerfectColoring(Graph graph) {
        if (graph.getNodeCount() <= 0) {
            throw new IllegalArgumentException("graph besitzt keine Knoten");
        }
        if (graph.getNodeCount() == 1) {
            graph.getNode(0).setAttribute("color", 1);
            return graph;
        }

        //pes
        List<Node> pesErg = pes(graph);
        //reverse damit wir größtes Element an erster Stelle ist (Ausgangspunkt)
        Collections.reverse(pesErg);
        //für jeden knoten das attribut color setzen und als defaultwert erstmal -1 setzen
        pesErg.stream().forEach(n -> n.setAttribute("color", -1));
        //für das erste element color auf 1 setzen
        pesErg.get(0).setAttribute("color", 1);
        //Liste für benutzte farben anlegen
        List<Integer> colorsUsed = new ArrayList<>();
        List<Node> neighbors;
        List<Integer> neighborsColor;
        // Wird um Nachbarfarben zu erleminieren
        List<Integer> colorsUsedCopy = new ArrayList<>();
        colorsUsed.add(1);


        for (int i = 1; i < pesErg.size(); i++) {
            //mit der kopie arbeiten, damit wir keine verfälschung haben
            colorsUsedCopy.addAll(colorsUsed);
            //nachbarn von i hinzufügen, bei denen die farbe nicht -1 ist, also nicht "leer"
            neighbors = pesErg.get(i).neighborNodes().filter(node -> (Integer) node.getAttribute("color") != -1).collect(Collectors.toList());
            //jede nachbarfarbe wird gemappt
            neighborsColor = neighbors.stream().map(n -> (Integer) n.getAttribute("color")).collect(Collectors.toList());
            //alle benutzen farben - nachbarfarben, da ein nachbar nicht die gleiche farbe haben darf
            colorsUsedCopy.removeAll(neighborsColor);


            if (colorsUsedCopy.isEmpty()) {
                //neue farbe anlegen
                colorsUsed.add(colorsUsed.size() + 1);
                //farbe 1 anlegen
                pesErg.get(i).setAttribute("color", colorsUsed.size());
            } else {
                //legt die beste färbung an
                Optional<Integer> min = colorsUsedCopy.stream().min(Integer::compareTo);
                pesErg.get(i).setAttribute("color", min.get());
            }
        }
        return graph;
    }

    /**
     * Gibt die kleinstmögliche Anzahl der Färbung des Graphen nach den PEO an
     * @param graph
     * @return  chromatic number
     */
    public static int getChromaticNumber(Graph graph) {
        if (graph.getNodeCount() <= 0) {
            throw new IllegalArgumentException("Graph hat keine Knoten");
        }

        //optimal färben
        getPerfectColoring(graph);
        List<Node> nodes = new ArrayList<>();
        for (Node n : graph
        ) {
            nodes.add(n);
        }
        //colors ist die anzahl der unterschiedlichen farben
        Set<Integer> colors = nodes.stream().map(n -> (Integer) n.getAttribute("color")).collect(Collectors.toSet());
        return colors.size();
    }


    /**
     * Prüft, ob erlaubt gefärbt wurde
     *
     * @param graph
     * @return wenn erlaubt true, ansonsten false
     */
    // Wird nur für die Tests benötigt
    public static boolean isValidColoring(Graph graph) {
        if (graph.getNodeCount() <= 0) {
            throw new IllegalArgumentException("Graph besitzt keine Nodes");
        }
        List<Node> neighbors;
        List<Integer> neighborsColors;
        List<Node> nodes = new ArrayList<>();
        // Alle Nodes werden der ArrayList hinzugefügt
        for (Node n : graph
        ) {
            nodes.add(n);
        }
        // get Alle Farben die verwendet werden in Graph
        for (Node n : nodes
        ) {
            neighbors = n.neighborNodes().collect(Collectors.toList());
            neighborsColors = neighbors.stream().map(nColor -> (Integer) nColor.getAttribute("color")).collect(Collectors.toList());
            // Prüft, ob die Nachbar Knoten die gleiche Farbe besitzen, wenn ja dann false
            for (Integer color : neighborsColors
            ) {
                if (n.getAttribute("color") == color) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Gibt einen random generierten vollständigen Graphen zurück
     * @return vollständigen Graphen
     */
    // Wird  für die Tests benötigt
    public static Graph generateRandomCompleteGraph() {
        Random random = new Random();
        //Anzahl der Knoten werden von 0 bis 50 randomisiert und dann +1, da Graph mindestens 1 Knoten besitzen muss
        int n = random.nextInt(50) + 1;
        Graph graph = new SingleGraph("completeGraph");
        if (n == 1) {
            graph.addNode("1");
            return graph;
        }
        graph.setStrict(false);
        graph.setAutoCreate(true);
        //Benennung der Kanten
        int indexForEdge = 1;
        // Erzeugung der Knoten mit den Kanten
        for (int j = 0; j < n - 1; j++) {
            graph.addEdge((indexForEdge++) + "", Integer.toString(j), Integer.toString(j + 1));
        }
        // Jeder Knoten bekommt zu jedem anderen Knoten eine Kante
        for (int i = n - 1; i >= 2; i--) {
            for (int f = i - 2; f >= 0; f--) {
                graph.addEdge(Integer.toString(indexForEdge++), Integer.toString(i), Integer.toString(f));
            }
        }
        return graph;
    }

    /**
     *
     * @param n Anzahl der Parent Nodes
     * @return ein tree, dessen Node entweder 2 oder 3 KinderNodes besitzt
     */
    // Wird  für die Tests benötigt
    public static Graph generateTreeForChordalGraph(int n) {
        Graph randomGraph = new SingleGraph("RandomGraph");
        randomGraph.setStrict(false);
        randomGraph.setAutoCreate(true);
        if (n <= 0) {
            throw new IllegalArgumentException("Node Anzahl muss größer als 0 sein");
        }
        if (n == 1) {
            randomGraph.addNode("0");
            return randomGraph;
        }
        randomGraph.addNode("0").setAttribute("parent");
        Random random = new Random();
        int nodeIndex = 1;
        int edgeIndex = 1;
        for (int j = 0; j < n; j++) {
            // Wenn 4 oder größer muss Sehne vorhanden sein, dies würde aber dann keinen Baum darstellen
            int numOfSubnodes = random.nextInt(1) + 2; // 2 oder 3
            //add Kanten zu den Knoten
            for (int i = 0; i < numOfSubnodes; i++) {
                randomGraph.addEdge(String.valueOf(edgeIndex++), String.valueOf(j), String.valueOf(nodeIndex));
                randomGraph.getNode(String.valueOf(nodeIndex)).setAttribute("parent", String.valueOf(j));
                nodeIndex++;
            }
        }
        return randomGraph;
    }

    /**
     * Gibt eine Liste der Nodes zurück die mit dem Tree verbunden sind
     * @param list
     * @return
     */
    // Wird  für die Tests benötigt
    private static List<Node> neighbourNodesToConnect(List<Node> list) {
        Set<Node> knoten = new HashSet<>();
        // Bis zwei Nachabrn gefunden wurden, werden Nachbarn von Nachbarn verbunden
        while (knoten.size() < 2) {
            Random rand = new Random();
            int i = rand.nextInt(list.size());
            knoten.add(list.get(i));
        }
        // Die Knoten werden sortiert zurückgegeben
        return knoten.stream().sorted(Comparator.comparing(Node::getIndex)).collect(Collectors.toList());
    }

    /**
     * Gibt einen random generiertem chordalen Graphen zurück
     */
    // Wird  für die Tests benötigt
    public static Graph generateRandomChordalGraph(int n) {
        Graph graph = generateTreeForChordalGraph(n);
        graph.setStrict(false);
        graph.setAutoCreate(true);
        //Anzahl der Nodes, des Tree's
        int numOfNodes = graph.getNodeCount();
        // Maximale Anzahl der Kanten
        int formelMaxKanten = (numOfNodes * (numOfNodes - 1) / 2);
        Random rand = new Random();
        // Formelkante +1, da es immer einen Knoten mehr als Kanten gibt
        int r = rand.nextInt(formelMaxKanten +1);
        // Liste von Nodes mit mehr als 2 Nachbarn
        List<Node> listWithMoreThan2Nodes = graph.nodes().filter(node -> node.neighborNodes().count() >= 2).toList();
        List<Node> childNodes;
        List<Node> toConnectNodes;
        // Ein zufälliges Node aus listWithMoreThan2Nodes wird genommen, und deren Nachbarn miteinander verbunden, bis die maximale Kantenanzahl erreicht wurde
        for (int i = 0; i <  r; i++) {
            int knotenIndex = rand.nextInt(listWithMoreThan2Nodes.size());
            Node choosenNode = listWithMoreThan2Nodes.get(knotenIndex);
            // Prüft ob beide Nachbarn den selben Parent besitzen
            childNodes = choosenNode.neighborNodes().filter(node -> (node.getAttribute("parent")).equals(choosenNode.toString())).toList();
            // Beide Knoten werden mit einer Kante miteinander verbunden
            toConnectNodes = neighbourNodesToConnect(childNodes);
            graph.addEdge(toConnectNodes.get(0).getId() + " " + toConnectNodes.get(1).getId(), toConnectNodes.get(0), toConnectNodes.get(1));
        }
        return graph;
    }


    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
//      // Viewer viewer = generateRandomChordalGraph(3).display();
//      // Viewer viewer1 = generateRandomCompleteGraph().display();
//        Graph viewer2 = generateTreeForChordalGraph(1);
//        System.out.println(getChromaticNumber(viewer2));
//        Viewer viewer = viewer2.display();
//        Graph viewer5 = generateRandomChordalGraph(81);
//        System.out.println(getChromaticNumber(viewer5));
        // Viewer viewer = generateTreeForChordalGraph(3).display();
        //      Viewer viewer1 = generateRandomChordalGraph(3).display();
        //    Viewer viewer2 = generateRandomChordalGraph(6).display();
          //Viewer viewer3 = generateRandomChordalGraph(8).display();
     //   Viewer viewer = generateRandomChordalGraph(10).display();
/*        Graph graph = new SingleGraph("graph");
        graph.setStrict(false);
        graph.setAutoCreate(true);
        graph.addEdge("e1", "1", "2");
        graph.addEdge("e2", "2", "3");
        graph.addEdge("e3", "3", "4");
        graph.addEdge("e4", "4", "2");
        Viewer viewer = graph.display();*/
      //  Viewer viewer = generateRandomChordalGraph(22).display();
        //Viewer viewer = generateRandomChordalGraph(3).display();
    }
}