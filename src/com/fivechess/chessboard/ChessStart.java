package com.fivechess.chessboard;

import com.fivechess.evaluate.Position;
import com.fivechess.rule.ChessRule;

public class ChessStart {

    public ChessBoard chessBoard;

    public ChessStart(ChessBoard chessBoard) { this.chessBoard = chessBoard; }

    public static final String [] START = {
            "长星局","峡月局","恒星局","水月局","流星局",
            "云月局", "浦月局","岚月局","银月局","明星局",
            "斜月局", "名月局","彗星局","寒星局","溪月局",
            "疏星局", "花月局","残月局", "雨月局","金星局",
            "松月局", "丘月局","新月局","瑞星局","山月局","游星局"
    };

    public void run(String select) {
        Position pos1 = new Position(7, 7);
        this.chessBoard.handleToJudge(pos1);
        this.chessBoard.setColorAndRepaint(pos1);
        this.chessBoard.stateTrans();

        ChessRule.FIFTH_HAND_N_PLAY.fourList.add(pos1);

        switch (select) {
            case "长星局" -> goTwoChess(8,6,9,5);
            case "峡月局" -> goTwoChess(8,6,9,6);
            case "恒星局" -> goTwoChess(8,6,9,7);
            case "水月局" -> goTwoChess(8,6,9,8);
            case "流星局" -> goTwoChess(8,6,9,9);
            case "云月局" -> goTwoChess(8,6,8,7);
            case "浦月局" -> goTwoChess(8,6,8,8);
            case "岚月局" -> goTwoChess(8,6,8,9);
            case "银月局" -> goTwoChess(8,6,7,8);
            case "明星局" -> goTwoChess(8,6,7,9);
            case "斜月局" -> goTwoChess(8,6,6,8);
            case "名月局" -> goTwoChess(8,6,6,9);
            case "彗星局" -> goTwoChess(8,6,5,9);

            case "寒星局" -> goTwoChess(7,6,7,5);
            case "溪月局" -> goTwoChess(7,6,8,5);
            case "疏星局" -> goTwoChess(7,6,9,5);
            case "花月局" -> goTwoChess(7,6,8,6);
            case "残月局" -> goTwoChess(7,6,9,6);
            case "雨月局" -> goTwoChess(7,6,8,7);
            case "金星局" -> goTwoChess(7,6,9,7);
            case "松月局" -> goTwoChess(7,6,7,8);
            case "丘月局" -> goTwoChess(7,6,8,8);
            case "新月局" -> goTwoChess(7,6,9,8);
            case "瑞星局" -> goTwoChess(7,6,7,9);
            case "山月局" -> goTwoChess(7,6,8,9);
            case "游星局" -> goTwoChess(7,6,9,9);
        }
    }

    private void goTwoChess(int col1, int row1, int col2, int row2) {
        Position pos2 = new Position(row1, col1);
        this.chessBoard.handleToJudge(pos2);
        this.chessBoard.setColorAndRepaint(pos2);
        this.chessBoard.stateTrans();

        ChessRule.FIFTH_HAND_N_PLAY.fourList.add(pos2);

        Position pos3 = new Position(row2, col2);
        this.chessBoard.handleToJudge(pos3);
        this.chessBoard.setColorAndRepaint(pos3);
        this.chessBoard.stateTrans();

        ChessRule.FIFTH_HAND_N_PLAY.fourList.add(pos3);

        System.out.println(ChessRule.FIFTH_HAND_N_PLAY.fourList);

        //开启 三手可交换
        ChessRule.THIRD_HAND_EXCHANGE.start();
    }
}
