package com.fivechess.judge;

import com.fivechess.chessboard.ChessBoard;
import com.fivechess.chessboard.ChessConnType;
import com.fivechess.chessboard.ChessConnection;
import com.fivechess.chessboard.ChessState;
import com.fivechess.evaluate.ChessDirection;
import com.fivechess.evaluate.Direction;
import com.fivechess.evaluate.Position;
import com.fivechess.rule.ForBiddenHandType;

public class ChessGameJudge {

    public ChessBoard chessBoard;


    public ChessGameJudge(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
    }

    public boolean isWinSuccessFive(Position pos) {
        ChessConnection chessConnection = new ChessConnection(this.chessBoard);
        ChessState color = this.chessBoard.chessmanArray[pos.row][pos.col];
        for(int index = 1; index <= 4; index++) {
            Direction direction = switch (index) {
                case 1 -> ChessDirection.HORIZONTAL;
                case 2 -> ChessDirection.VERTICAL;
                case 3 -> ChessDirection.LEFT_DIAGONAL;
                case 4 -> ChessDirection.RIGHT_DIAGONAL;
                default -> null;
            };

            if(chessConnection.getChessConnType(pos, color, direction) == ChessConnType.SUCCESSIVE_FIVE)
                return true;
        }

        return false;
    }

    public boolean isForbiddenHand(ForBiddenHandType forBiddenHandType) {
        return forBiddenHandType != ForBiddenHandType.NOTHING;
    }

}
