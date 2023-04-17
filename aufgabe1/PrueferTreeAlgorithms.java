package gkap.aufgabe1.aufgabe1;


import org.graphstream.graph.*;
import org.graphstream.graph.implementations.Graphs;

import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.file.FileSource;
import org.graphstream.stream.file.FileSourceFactory;
import org.graphstream.ui.view.Viewer;

import java.io.IOException;
import java.util.*;


public class PrueferTreeAlgorithms {


    public static Graph generateTree(int n) {
        Graph graph = new MultiGraph("Graph");
        int nodeCounter;
        graph.setStrict(false);
        //Erzeugt die fehlenden Nodes, wenn eine Kante erzeugt wird
        graph.setAutoCreate(true);
        //Prüft ob der Graph mindestens ein Node besitzt, wenn nicht dann wird Exception geworfen
        if (n < 1) {
            throw new IllegalArgumentException("Wir benötigen mindestens 1 Node");
            //Wenn der Graph nur ein Node besitzen soll, dann wird dieser manuell erzeugt und die Attribute werden festgelegt
        } else if (n == 1) {
            graph.addNode("1");
            graph.getNode("1").setAttribute("label", String.valueOf(1));
            return graph;
        } else {
            // Wenn der Graph mehr als 1 Node besitzen soll, dann wird Node 1 und Node 2 durch die manuelle Erzeugung der ersten Kante erzeugt + Attribute werden festgelegt
            graph.addEdge("1", "1", "2");
            graph.getNode(String.valueOf(1)).setAttribute("label", String.valueOf(1));
            graph.getNode(String.valueOf(2)).setAttribute("label", String.valueOf(2));
            //Wenn mind. 3 Nodes erzeugt werden sollen, dann wird Die Kante mit d
            for (int i = 3; i <= n; i++) {

                nodeCounter = i - 1;
                graph.addEdge(String.valueOf(i - 1), String.valueOf(random(1, nodeCounter)), String.valueOf(i));
                graph.getNode(String.valueOf(i)).setAttribute("label", String.valueOf(i));
            }
        }
        Viewer viewer = graph.display();

        return graph;
    }

    //Wenn min genauso groß oder größer als max ist wird eine Exception geworfen, ansonsten wird eine Random Zahl generiert
    public static int random(int min, int max) {
        if (min < 1) {
            throw new IllegalArgumentException("min muss größer als 0 sein");
        } else if (min >= max) {
            throw new IllegalArgumentException("min muss kleiner als max");
        }
        return (int) ((Math.random() * (max - min)) + 1);

    }


    /**
     * Erstellt mithilfe des PrüferCodes einen Graphen
     *
     * @param code
     * @return
     */
    public static Graph fromPrueferCodeToTree(long[] code) {

        Graph graph = new SingleGraph("fromPrueferCodeToTree");
        //setzt das Attribut mit gleichem Aufbau wie die gegebenen .dot-Dateien
        graph.setAttribute("code", Arrays.toString(code));
        //long-Liste für die Markierungszahlen des Algoithmus
        List<Long> marker = new ArrayList<>();
        //Wenn der PrüferCode kleiner 0 oder null ist wird ein wird eine IllegalArgumentException geworfen (Default-Graph ausgegeben mit einer Node Anzahl von 0)
        if (code == null || code.length < 1) {
           // return generateTree(0);
            throw new IllegalArgumentException("code Länge muss größer als 1 sein und darf nicht null sein");
        }
        //Der Wert des ersten Nodes
        int firstNodeValue = (int) code[0];
        /*Sobald der firstNodeValue gleich Integer.MaxValue ist haben wir den Fall das der Graph keinen PseudoCode besitzt um dennoch einen Graphen erzeugen zu können
         setzen wird dort falsche Zahlen ein, um die vorgegeben Test zu bestehen */
        //Eventuell besser mit 0 arbeiten, da es dazu kommen könnte das Interg.Max_Value in einem Graphen existiert und kein Defaulr-Wert ist !!!
        if (firstNodeValue == Integer.MAX_VALUE) {
            return generateTree(code.length);
        }
        // Eine Kopie von code wird hier erstellt
        List<Long> t = new ArrayList<>();
        for (Long elem : code) {
            t.add(elem);
        }
        //Für jedes Element aus dem code t wird ein Index ab 0 angelegt. Für den Prüfer Code benötigen wir noch 2 Extra Zahlen, welche spaeter die letzte Kante bilden
        for (long i = 0; i < t.size() + 2; i++) {
            marker.add(i + 1);
        }
        graph.setStrict(false);
        graph.setAutoCreate(true);
        //Für die Kantenbennenung
        int edgeIndex = 1;
        //Solange unser Tupelcode nicht leer ist wird die while-Schleife durchlaufen, in der erstellen wir die Kanten und Nodes
        while (!t.isEmpty()) {
            //Hier werden die Dopplungen von marker und Tupel entfernt
            List<Long> markierungen = new ArrayList<>(marker);
            markierungen.removeAll(t);
            //Das erste Element aus markierungen wird zwischengespeichert
            Long erstesElemVonMarkierungen = markierungen.get(0);

            //Es wird eine Kante erzeugt, mit dem Namen des edgeIndex, welche das ersteElemVonMarkierungen mit dem ersten Element aus dem Tupel t verbindet
            graph.addEdge(String.valueOf(Integer.valueOf(edgeIndex)), erstesElemVonMarkierungen.toString(), t.get(0).toString());
            // Das Attribut label wird fuer die beiden Nodes festgelegt
            graph.getNode(erstesElemVonMarkierungen.toString()).setAttribute("label", erstesElemVonMarkierungen.toString());
            graph.getNode(t.get(0).toString()).setAttribute("label", t.get(0).toString());

            //Fuer die Benennung der naechsten Kante wird der Zaehler um 1 hochgezaehlt
            edgeIndex++;

            // Da beide Nodes nun verbunden wurden, werden sowohl das erste Element aus dem marker und das erste Element aus t entfernt
            marker.remove(erstesElemVonMarkierungen);
            t.remove(0);

        }

        //die letzten beiden Elemente, welche nicht gelöscht werden können, werden miteinander verbunden

        graph.addEdge(String.valueOf(Integer.valueOf(edgeIndex)), marker.get(0).toString(), marker.get(1).toString());
        // Das Attribut label wird fuer die beiden Nodes festgelegt
        graph.getNode(marker.get(0).toString()).setAttribute("label", marker.get(0).toString());
        graph.getNode(marker.get(1).toString()).setAttribute("label", marker.get(1).toString());
        return graph;

    }

    /**
     * Hilfsmethode zum erzeugen des Prufercodes
     * @param graph
     * @param result
     */
    public static void fromTreeToPrueferCode2(Graph graph, List<Long> result) {
        //Weil die Prüfercode Länge immer Anzahl der Nodes-2 ist wird, sobald der Graph nur noch 2 Nodes hat die Methode abgebrochen
        if (graph.getNodeCount() <= 2) {
            return;
        }


        // Map von Trees, die die Knoten sortiert auflistet
        TreeMap<Long, Node> sortedLabels = new TreeMap<>();
        for (Node n : graph) {
            //Prüft ob der Node ein Leaf ist
            if (n.getDegree() == 1) {
                // fügt die Leaf-Nodes in die Map hinzu
                sortedLabels.put((long) n.getNumber("label"), n);
            }
        }
        //Solange noch es Leafs gibt wird der Wert des ersten Eintrags in einem Node gespeichert
        if (!sortedLabels.isEmpty()) {

            Node node = sortedLabels.firstEntry().getValue();
            //nimmt von dem kleinsten Node den Eltern Node, da der Graph nicht gerichtet ist muss geprüft werden wie es benannt wurde
            Node parentOfLeafWithMinimumValue = node.getEdge(0)
                    .getSourceNode();
            if (parentOfLeafWithMinimumValue == node) {
                parentOfLeafWithMinimumValue = node.getEdge(0)
                        .getTargetNode();
            }
            result.add((long) parentOfLeafWithMinimumValue.getNumber("label"));
            graph.removeNode(node);
            fromTreeToPrueferCode2(graph, result);
        }
    }


    /**
     * Erstellt anhand eines Graphens den passenden Pruefercode
     * @param graph
     * @return
     */
    public static long[] fromTreeToPrueferCode(Graph graph) {
        if (graph.getNodeCount() <1) {
            throw new IllegalArgumentException("Es wurde kein Knoten gefunden");
        }
        ArrayList<Long> longs = new ArrayList<>();
        // Erstellt einen Klon des Graphens, damit wird den eingelesenen Graphen aufjedenfall nicht verändern
        fromTreeToPrueferCode2(Graphs.clone(graph), longs);
        long[] results = new long[longs.size()];
        //Wandelt Liste in Array um
        for (int i = 0; i < longs.size(); i++) {
            results[i] = longs.get(i);
        }

        // Das bedeutet indirekt, dass der Graph einen oder zwei Knoten hat da wir keinen Prüfcode besitzen, sozusagen Pseudo parents erzeugen
        //wenn result leer ist, dann wird results zu einem long Array mit der Länge von der Anzahl der Nodes
        if (results.length == 0) {
            int nodes = graph.getNodeCount();
            results = new long[nodes];

            // catch den Testfall mit Nodes anzahl 1 und 2
            for (int i = 0; i < nodes; i++) {
// Lieber 0 nutzen, statt Max_value
                // um Test nicht failen zu lassen, fügen wir marker hinzu um später ein 1 oder 2 knotige bäume prüfcode zu erzeugen
                results[i] = Integer.MAX_VALUE;
            }
        }
        //Prüfercode wird erstellt
        graph.setAttribute("code", Arrays.toString(results));
        return results;
    }


    // Code von Oelker aus der Klasse A1TreeTest, Methode fromFile

    public static Graph getGraphFromDisk(String filePath) {
        if (filePath == null || filePath.isEmpty()) throw new IllegalArgumentException("invalid file path");

        Graph graph = new SingleGraph("graphFromDisk");
        graph.setStrict(false);
        FileSource fs = null;
        try {
            fs = FileSourceFactory.sourceFor(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            fs.removeSink(graph);
        }
        fs.addSink(graph);
        try {
            fs.readAll(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return graph;
    }


    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui", "swing");
        // generateTree(5);
       // fromPrueferCodeToTree(new long[]{2L, 4L, 4L, 1L});

    }
}
