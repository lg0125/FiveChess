package com.fivechess.chessrule;

import com.fivechess.chessboard.FiveChessBoard;
import com.fivechess.evaluate.Point;
import com.fivechess.rule.ForBiddenHandType;

/**
 * 禁手规则针对于黑棋(BLACK)
 * */
public class ForbiddenHand {

    private FiveChessBoard chessBoard;
    private Point point;
    private ForBiddenHandType forBiddenHandType;


    public void start(FiveChessBoard fiveChessBoard, Point point) {

    }

    public void run() {

    }

    public void end() {

    }

    private boolean isThreeThree() {

        return false;
    }

    private boolean isFourFour() {

        return false;
    }

    private boolean isLongConnection() {

        return false;
    }


}
