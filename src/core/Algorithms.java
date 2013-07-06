package core;

import core.Node;

/**
 * User: Alex
 * Date: 06.03.13
 * Time: 0:11
 */
public class Algorithms {

    public  Algorithms() {

    }

    public int criticalToEnd(boolean time, Node node) {
        if (node.children.isEmpty()) {
            if (time) {
                return node.value;
            } else {
                return 1;
            }
        }
        int max = 0;
        for (Node child : node.children) {
            int t = criticalToEnd(time, child);
            if (time) {
                t += node.value;
            } else {
                t++;
            }
            if (t > max) {
                max = t;
            }
        }
        return max;
    }

    public int criticalFromStart(boolean time, Node node) {
        if (node.parents.isEmpty()) {
            return 0;
        }
        int max = 0;
        for (Node parent : node.parents) {
            int t = criticalFromStart(time, parent);
            if (time) {
                t += parent.value;
            } else {
                t++;
            }
            if (t > max) {
                max = t;
            }
        }
        return max;
    }

}
