package com.fivechess.evaluate;

import com.fivechess.chessboard.ChessBoard;

public class Position {
    public int row;
    public int col;

    public Position() {}

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public void setPosition(int row, int col){
        this.row = row;
        this.col = col;
    }

    public Position newPosition(int len, Direction direction) {
        return new Position(this.row + direction.row * len, this.col + direction.col * len);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this) return true;
        if(!(obj instanceof Position position)) return false;
        return position.row == this.row && position.col == this.col;
    }

    @Override
    public String toString() {
        char c = 'A';
        int n = ChessBoard.Rows;
        return "[" + (n - this.row) + "," + (char)(c + this.col) + "]";
    }
}
