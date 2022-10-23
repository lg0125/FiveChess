package com.fivechess.evaluate;

public class Point {
    public int x;
    public int y;

    public Point(int x,int y) {
        this.x = x;
        this.y = y;
    }

    public Point() {
        this.x = -1;
        this.y = -1;
    }

    public Point(Point p) {
        this.x = p.x;
        this.y = p.y;
    }

    public Point newPoint(int len, Dir dir) {
        return new Point(x + dir.x * len, y + dir.y * len);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Point point)) return false;
        return point.x == this.x && point.y == this.y;
    }
}
