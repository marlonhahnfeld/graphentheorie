package gkap.aufgabe1.aufgabe1;

import gkap.aufgabe1.aufgabe1.PrueferTreeAlgorithms;
import org.graphstream.graph.Graph;
import org.junit.Test;

import java.util.Arrays;


import static gkap.aufgabe1.aufgabe1.A1TreeTest.treeEquals;
import static gkap.aufgabe1.aufgabe1.PrueferTreeAlgorithms.*;
import static org.junit.Assert.assertEquals;

public class TreeTest {
    @Test
    public void testFromTreeToPrueferCode() {
        long[] prueferCode = new long[]{5, 5, 5, 5, 5, 5, 5, 5, 5};
        Graph expected = PrueferTreeAlgorithms.getGraphFromDisk("C:\\Users\\Admin\\Desktop\\UNIVERSITÄT\\EdekaProjekte\\GKA\\gka\\BSP_PrÅferCode\\BT5.dot");
        System.out.println(String.format("Expected: %s ", Arrays.toString(fromTreeToPrueferCode(expected))));
        System.out.println(String.format("Actual: %s ", Arrays.toString(prueferCode)));
        assertEquals(Arrays.toString(prueferCode), Arrays.toString(fromTreeToPrueferCode(expected)));
    }

    @Test
    public void testFromTreeToPrueferCode1() {
        long[] prueferCode = new long[]{2,3,4,5,6,7};
        Graph expected = PrueferTreeAlgorithms.getGraphFromDisk("C:\\Users\\Admin\\Desktop\\UNIVERSITÄT\\EdekaProjekte\\GKA\\gka\\BSP_PrÅferCode\\BT4.dot");
        long[] longs = fromTreeToPrueferCode(expected);
        System.out.println(String.format("Expected: %s ", Arrays.toString(longs)));
        System.out.println(String.format("Actual: %s ", Arrays.toString(prueferCode)));
        long[] longs1 = fromTreeToPrueferCode(expected);
        assertEquals(Arrays.toString(prueferCode), Arrays.toString(longs1));
    }
    @Test
    public void testFromTreeToPrueferCode2() {
        long[] prueferCode = new long[]{2, 4, 4, 1};
        Graph expected = PrueferTreeAlgorithms.getGraphFromDisk("C:\\Users\\Admin\\Desktop\\UNIVERSITÄT\\EdekaProjekte\\GKA\\gka\\BSP_PrÅferCode\\BT6.dot");
        System.out.println(String.format("Actual: %s ", Arrays.toString(fromTreeToPrueferCode(expected))));
        System.out.println(String.format("Expected: %s ", Arrays.toString(prueferCode)));
        assertEquals(Arrays.toString(prueferCode), Arrays.toString(fromTreeToPrueferCode(expected)));
    }


    
    @Test
    public void testGetGraphFromDisk() {
        Graph g = getGraphFromDisk("C:\\Users\\Admin\\Desktop\\UNIVERSITÄT\\EdekaProjekte\\GKA\\gka\\BSP_PrÅferCode\\BT5.dot");

        String expectedPruferCode = "5,5,5,5,5,5,5,5,5";
        assertEquals(expectedPruferCode, g.getAttribute("code"));
    }



    @Test
    public void testFromPrueferCodeToTree() {
        Graph expected = PrueferTreeAlgorithms.getGraphFromDisk("C:\\Users\\Admin\\Desktop\\UNIVERSITÄT\\EdekaProjekte\\GKA\\gka\\BSP_PrÅferCode\\BT6.dot");

        assert treeEquals(expected, PrueferTreeAlgorithms.fromPrueferCodeToTree(new long[]{2, 4, 4, 1}));

    }


    @Test
    public void testFromPrueferCodeToTree1() {
        Graph expected = PrueferTreeAlgorithms.getGraphFromDisk("C:\\Users\\Admin\\Desktop\\UNIVERSITÄT\\EdekaProjekte\\GKA\\gka\\BSP_PrÅferCode\\BT4.dot");

        assert treeEquals(expected, PrueferTreeAlgorithms.fromPrueferCodeToTree(new long[]{2,3,4,5,6,7}));

    }



    @Test
    public void testFromPrueferCodeToTree2() {
        Graph expected = PrueferTreeAlgorithms.getGraphFromDisk("C:\\Users\\Admin\\Desktop\\UNIVERSITÄT\\EdekaProjekte\\GKA\\gka\\BSP_PrÅferCode\\BT5.dot");

        assert treeEquals(expected, PrueferTreeAlgorithms.fromPrueferCodeToTree(new long[]{5,5,5,5,5,5,5,5,5}));

    }
}
