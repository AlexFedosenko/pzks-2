package core;

import java.util.ArrayList;

/**
 * User: Alex
 * Date: 05.03.13
 * Time: 23:25
 */
public class Node {

    public ArrayList<Node> parents;
    public ArrayList<Node> children;
    public ArrayList<Integer> upLinks;
    public ArrayList<Integer> downLinks;
    public int value;

    public Node() {
        parents = new ArrayList<>();
        children = new ArrayList<>();
        upLinks = new ArrayList<>();
        downLinks = new ArrayList<>();
        value = 0;
    }


}
