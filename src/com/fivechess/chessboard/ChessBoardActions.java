package com.fivechess.chessboard;

import com.fivechess.evaluate.Position;

public interface ChessBoardActions {
    void setColorAndRepaint(Position pos);
    void resultAndReset(String text);
    void handleToJudge(Position pos);
    void stateTrans();
    boolean isInBoard(Position pos);

    void goFirst(Role firstRole);

    void goFifthHandNPlay(Role executeRole, Position position);

}
