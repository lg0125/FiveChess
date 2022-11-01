package com.fivechess.evaluate;

import java.util.ArrayList;

public class TreeNode {
    public TreeNode bestChild;
    public ArrayList<TreeNode> children;
    public Position position;
    public int mark;

    public TreeNode() {
        this.children = new ArrayList<>();
        this.position = new Position();
        this.bestChild = null;
        this.mark = 0;
    }

    public void setPosition(Position position) {
        this.position.row = position.row;
        this.position.col = position.col;
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }

    public TreeNode getLastChild() {
        return this.children.get(this.children.size() - 1);
    }
}
