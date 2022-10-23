package com.fivechess.test;

import com.fivechess.chessboard.ChessState;
import com.fivechess.evaluate.Dir;
import com.fivechess.evaluate.Point;

import java.util.HashSet;

public class ToJudgeTest {
    public static final int Rows = 15;
    public static final Dir d1 = new Dir(0, 1); //横
    public static final Dir d2 = new Dir(1, 0); //竖
    public static final Dir d3 = new Dir(1, -1); //撇
    public static final Dir d4 = new Dir(1, 1); //捺

    public static HashSet<Point> toJudge = new HashSet<>();
    public static ChessState[][] chessmanArray;//棋盘状态，0 无子，1 白子，2黑子
    static {
        chessmanArray = new ChessState[Rows][Rows];
        for(int j = 0; j < Rows; j++){
            for(int i = 0; i < Rows; i++){
                chessmanArray[j][i] = ChessState.NOTHING;
            }
        }
    }

    public static void main(String[] args) {
        Point p = new Point(7,7);
        chessmanArray[p.y][p.x] = ChessState.BLACK;
        ToJudgeTest.handleToJudge(p);
        for(Point point : toJudge) {
            System.out.println("[" + point.y + "," + point.x + "]");
        }
    }

    public static void handleToJudge(Point p) {
        toJudge.remove(p);
        for(int index = 1; index <= 4; index++) {
            Dir dir = switch (index) {
                case 1 -> d1;
                case 2 -> d2;
                case 3 -> d3;
                case 4 -> d4;
                default -> null;
            };
            for(int len = -1; len <= 1; len++) {
                if(len != 0) {
                    Point point = p.newPoint(len, dir);
                    if (point.x >= 0 && point.x < Rows &&
                            point.y >= 0 && point.y < Rows &&
                            chessmanArray[point.y][point.x].equals(ChessState.NOTHING))
                        toJudge.add(point);
                }
            }
        }
    }
}
