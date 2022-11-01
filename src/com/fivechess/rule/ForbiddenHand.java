package com.fivechess.rule;

import com.fivechess.chessboard.*;
import com.fivechess.evaluate.Position;

import java.util.ArrayList;

public class ForbiddenHand {

    public ChessBoard chessBoard;
    public ChessConnection chessConnection;

    public void init(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.chessConnection = new ChessConnection(this.chessBoard);
    }

    public ForBiddenHandType run(Position pos) {
        ChessState color = this.chessBoard.chessmanArray[pos.row][pos.col];

        int longConnection = 0;
        int aliveFour = 0, spurFour = 0, dieFour = 0;
        int successiveAliveThree = 0, skipAliveThree = 0;

        ArrayList<ChessConnType> connList =
                this.chessConnection.getChessConnList(pos, color);

        for(ChessConnType conn : connList) {
            switch (conn) {
                case LONG_CONNECTION -> longConnection++;

                case ALIVE_FOUR -> aliveFour++;
                case SPUR_FOUR -> spurFour++;
                case DIE_FOUR -> dieFour++;

                case SUCCESSIVE_ALIVE_THREE -> successiveAliveThree++;
                case SKIP_ACTIVE_THREE -> skipAliveThree++;
            }
        }

        if((successiveAliveThree + skipAliveThree >= 2) ||
                (successiveAliveThree + skipAliveThree == 2) &&
                        (aliveFour == 1 || spurFour == 1 || dieFour == 1))
            return ForBiddenHandType.THREE_THREE;

        if(aliveFour + spurFour + dieFour >= 2)
            return ForBiddenHandType.FOUR_FOUR;

        if(longConnection >= 1)
            return ForBiddenHandType.LONG_CONNECTION;

        return ForBiddenHandType.NOTHING;
    }
}
