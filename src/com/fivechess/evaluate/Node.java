package com.fivechess.evaluate;

import java.util.ArrayList;

public class Node {
    public Node bestChild;
    public ArrayList<Node> children;
    public Point point;
    public int mark;

    public Node() {
        this.children = new ArrayList<>();
        this.point = new Point();
        this.bestChild = null;
        this.mark = 0;
    }

    public void setPoint(Point point){
        this.point.x = point.x;
        this.point.y = point.y;
    }

    public void addChild(Node child) {
        this.children.add(child);
    }

    public Node getLastChild() {
        return this.children.get(this.children.size() - 1);
    }
}
