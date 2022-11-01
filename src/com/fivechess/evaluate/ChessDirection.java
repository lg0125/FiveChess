package com.fivechess.evaluate;

public class ChessDirection {
    public static final Direction HORIZONTAL = new Direction(0,1); //横
    public static final Direction VERTICAL = new Direction(1,0); //竖
    public static final Direction LEFT_DIAGONAL = new Direction(1,1); //捺
    public static final Direction RIGHT_DIAGONAL = new Direction(1,-1); //撇
}
