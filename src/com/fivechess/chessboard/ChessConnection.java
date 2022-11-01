package com.fivechess.chessboard;

import com.fivechess.evaluate.ChessDirection;
import com.fivechess.evaluate.Direction;
import com.fivechess.evaluate.Position;

import java.util.ArrayList;

public class ChessConnection {

    public ChessBoard chessBoard;
    public int count;
    public ArrayList<ChessState> positive;
    public ArrayList<ChessState> negative;

    public ChessConnection(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.count = 1;
        this.positive = new ArrayList<>();
        this.negative = new ArrayList<>();
    }

    public int estimateConnList(ArrayList<ChessConnType> connList) {
        int successiveFive = 0;
        int aliveFour = 0, spurFour = 0, dieFour = 0;
        int successiveAliveThree = 0, skipAliveThree = 0, dieThree = 0;
        int successiveAliveTwo = 0, skipAliveTwo = 0, dieTwo = 0;

        for(ChessConnType conn : connList) {
            switch (conn) {
                case SUCCESSIVE_FIVE -> successiveFive++;

                case ALIVE_FOUR -> aliveFour++;
                case SPUR_FOUR -> spurFour++;
                case DIE_FOUR -> dieFour++;

                case SUCCESSIVE_ALIVE_THREE -> successiveAliveThree++;
                case SKIP_ACTIVE_THREE -> skipAliveThree++;
                case DIE_THREE -> dieThree++;

                case SUCCESSIVE_ALIVE_TWO -> successiveAliveTwo++;
                case SKIP_ALIVE_TWO -> skipAliveTwo++;
                case DIE_TWO -> dieTwo++;
            }
        }

        if(successiveFive >= 1) return 14;
        if (aliveFour >= 1 || dieFour >= 2 || (dieFour >= 1 && successiveAliveThree >= 1)) return 13; //活4 双死4 死4 活3
        if (successiveAliveThree >= 2)return 12; //双活3
        if (dieThree >= 1 && successiveAliveThree >= 1)return 11; //死3 高级活3
        if (dieFour >= 1)return 10; //高级死4
        if (spurFour >= 1)return 9; //低级死4
        if (successiveAliveThree >= 1)return 8; //单活3
        if (skipAliveThree >= 1)return 7; //跳活3
        if (successiveAliveTwo >= 2)return 6; //双活2
        if (successiveAliveTwo >= 1)return 5; //活2
        if (skipAliveTwo >= 1)return 4; //低级活2
        if (dieThree >= 1)return 3; //死3
        if (dieTwo >= 1)return 2; //

        return 1;
    }

    public ArrayList<ChessConnType> getChessConnList(Position pos, ChessState color) {
        ArrayList<ChessConnType> resList = new ArrayList<>();

        ChessConnType conn;
        for(int index = 1; index <= 4; index++) {
            Direction direction = switch (index) {
                case 1 -> ChessDirection.HORIZONTAL;
                case 2 -> ChessDirection.VERTICAL;
                case 3 -> ChessDirection.LEFT_DIAGONAL;
                case 4 -> ChessDirection.RIGHT_DIAGONAL;
                default -> null;
            };

            conn = this.getChessConnType(pos, color, direction);

            resList.add(conn);
        }

        return resList;
    }

    public ChessConnType getChessConnType(Position pos, ChessState color,
                                                  Direction direction) {
        this.scan(pos, color, direction);

        if(this.count > 5)
            return ChessConnType.LONG_CONNECTION;

        switch (this.count) {
            case 5 -> {
                return ChessConnType.SUCCESSIVE_FIVE;
            }

            case 4 -> {
                if(this.negative.get(1) == ChessState.NOTHING &&
                        this.positive.get(1) == ChessState.NOTHING)
                    return ChessConnType.ALIVE_FOUR;

                else if(this.negative.get(1) == ChessState.NOTHING ||
                        this.positive.get(1) == ChessState.NOTHING)
                    return ChessConnType.DIE_FOUR;

                else return ChessConnType.NOTHING;
            }

            case 3 -> {
                if((this.negative.get(1) == ChessState.NOTHING && this.negative.get(2) == color) ||
                        (this.positive.get(1) == ChessState.NOTHING && this.positive.get(2) == color))
                    return ChessConnType.SPUR_FOUR;

                else if(this.negative.get(1) == ChessState.NOTHING &&
                        this.positive.get(1) == ChessState.NOTHING &&
                        (this.negative.get(2) == ChessState.NOTHING || this.positive.get(2) == ChessState.NOTHING))
                    return ChessConnType.SUCCESSIVE_ALIVE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING))
                    return ChessConnType.DIE_THREE;

                else if(this.negative.get(1) == ChessState.NOTHING &&
                        this.positive.get(1) == ChessState.NOTHING)
                    return ChessConnType.DIE_THREE;

                else return ChessConnType.NOTHING;
            }

            case 2 -> {
                if((this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == color &&
                                this.negative.get(3) == color) &&
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == color &&
                                this.positive.get(3) == color))
                        return ChessConnType.SPUR_FOUR;

                else if(this.negative.get(1) == ChessState.NOTHING &&
                        this.positive.get(1) == ChessState.NOTHING &&
                        ((this.negative.get(2) == color && this.negative.get(3) == ChessState.NOTHING) ||
                                (this.positive.get(2) == color && this.positive.get(3) == ChessState.NOTHING)))
                        return ChessConnType.SKIP_ACTIVE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(3) == ChessState.NOTHING &&
                                this.negative.get(2) == color ) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(3) == ChessState.NOTHING &&
                                this.positive.get(2) == color))
                        return ChessConnType.DIE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING) &&
                        (this.negative.get(2) == color ||
                                this.positive.get(2) == color))
                        return ChessConnType.DIE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                            this.negative.get(2) == ChessState.NOTHING &&
                            this.negative.get(3) == color) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING &&
                                this.positive.get(3) == color))
                        return ChessConnType.DIE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                            this.positive.get(1) == ChessState.NOTHING &&
                            this.positive.get(2) == ChessState.NOTHING &&
                            this.positive.get(3) == ChessState.NOTHING) ||
                        (this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING &&
                                this.negative.get(3) == ChessState.NOTHING))
                        return ChessConnType.SUCCESSIVE_ALIVE_TWO;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                            this.negative.get(2) == ChessState.NOTHING &&
                            this.negative.get(3) == ChessState.NOTHING) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                            this.positive.get(2) == ChessState.NOTHING &&
                            this.positive.get(3) == ChessState.NOTHING))
                        return ChessConnType.DIE_TWO;

                else return ChessConnType.NOTHING;
            }

            case 1 -> {
                if((this.negative.get(1) == ChessState.NOTHING &&
                            this.negative.get(2) == color &&
                            this.negative.get(3) == color &&
                            this.negative.get(4) == color) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == color &&
                                this.positive.get(3) == color &&
                                this.positive.get(4) == color))
                        return ChessConnType.SPUR_FOUR;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                    this.positive.get(1) == ChessState.NOTHING) &&
                        ((this.negative.get(2) == color &&
                                    this.negative.get(3) == color &&
                                    this.negative.get(4) == ChessState.NOTHING) ||
                                (this.positive.get(2) == color &&
                                        this.positive.get(3) == color &&
                                        this.positive.get(4) == ChessState.NOTHING)))
                        return ChessConnType.SKIP_ACTIVE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING) &&
                        ((this.negative.get(2) == color &&
                                        this.negative.get(3) == color ) ||
                                (this.positive.get(2) == color &&
                                        this.positive.get(3) == color)))
                        return ChessConnType.DIE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                            this.negative.get(4) == ChessState.NOTHING &&
                            this.negative.get(2) == color &&
                            this.negative.get(3) == color) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(4) == ChessState.NOTHING &&
                                this.positive.get(2) == color &&
                                this.positive.get(3) == color))
                        return ChessConnType.DIE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                            this.negative.get(2) == ChessState.NOTHING &&
                            this.negative.get(3) == color &&
                            this.negative.get(4) == color) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING &&
                                this.positive.get(3) == color &&
                                this.positive.get(4) == color))
                        return ChessConnType.DIE_THREE;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                            this.negative.get(3) == ChessState.NOTHING &&
                            this.negative.get(2) == color &&
                            this.negative.get(4) == color) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(3) == ChessState.NOTHING &&
                                this.positive.get(2) == color &&
                                this.positive.get(4) == color))
                        return ChessConnType.DIE_THREE;

                else if ((this.negative.get(1) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING  &&
                                this.positive.get(2) == color  &&
                                this.positive.get(3) == ChessState.NOTHING) &&
                        (this.positive.get(4) == ChessState.NOTHING ||
                                this.negative.get(2) == ChessState.NOTHING))
                    return ChessConnType.SKIP_ALIVE_TWO;

                else if ((this.positive.get(1) == ChessState.NOTHING &&
                                this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == color &&
                                this.negative.get(3) == ChessState.NOTHING) &&
                        (this.negative.get(4) == ChessState.NOTHING ||
                                this.positive.get(2) == ChessState.NOTHING))
                    return ChessConnType.SKIP_ALIVE_TWO;

                else if ((this.positive.get(1) == ChessState.NOTHING &&
                                this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING &&
                                this.negative.get(3) == color &&
                                this.negative.get(4) == ChessState.NOTHING) ||
                        (this.negative.get(1) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING &&
                                this.positive.get(3) == color &&
                                this.positive.get(4) == ChessState.NOTHING))
                    return ChessConnType.SKIP_ALIVE_TWO;

                else if ((this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == color &&
                                this.negative.get(3) == ChessState.NOTHING &&
                                this.negative.get(4) == ChessState.NOTHING) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == color &&
                                this.positive.get(3) == ChessState.NOTHING &&
                                this.positive.get(4) == ChessState.NOTHING))
                    return ChessConnType.DIE_TWO;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING &&
                                this.negative.get(2) == color) ||
                        (this.negative.get(1) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING &&
                                this.positive.get(2) == color))
                    return ChessConnType.DIE_TWO;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING &&
                                this.negative.get(3) == color &&
                                this.negative.get(4) == ChessState.NOTHING) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING &&
                                this.positive.get(3) == color &&
                                this.positive.get(4) == ChessState.NOTHING))
                    return ChessConnType.DIE_TWO;

                else if((this.positive.get(1) == ChessState.NOTHING &&
                                this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING &&
                                this.negative.get(3) == color) ||
                        (this.negative.get(1) == ChessState.NOTHING &&
                                this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING &&
                                this.positive.get(3) == color))
                    return ChessConnType.DIE_TWO;

                else if((this.negative.get(1) == ChessState.NOTHING &&
                                this.negative.get(2) == ChessState.NOTHING &&
                                this.negative.get(3) == ChessState.NOTHING &&
                                this.negative.get(4) == color) ||
                        (this.positive.get(1) == ChessState.NOTHING &&
                                this.positive.get(2) == ChessState.NOTHING &&
                                this.positive.get(3) == ChessState.NOTHING &&
                                this.positive.get(4) == color))
                    return ChessConnType.DIE_TWO;

                else return ChessConnType.NOTHING;
            }
        }

        return ChessConnType.NOTHING;
    }


    private void scan(Position pos, ChessState color,
                      Direction direction) {
        this.count = 1;
        this.positive.clear();
        this.negative.clear();

        this.positive.add(color);
        this.negative.add(color);

        Position negativePos, positivePos, curPos;
        ChessState oppositeColor = switch (color) {
            case NOTHING -> ChessState.NOTHING;
            case WHITE -> ChessState.BLACK;
            case BLACK -> ChessState.WHITE;
        };

        //计算连子数量
        curPos = pos.newPosition(-1, direction);
        negativePos = pos;
        while(this.chessBoard.isInBoard(curPos) &&
                this.chessBoard.chessmanArray[curPos.row][curPos.col] == color) {
            this.count++;
            negativePos = curPos;
            curPos = curPos.newPosition(-1, direction);
        }

        curPos = pos.newPosition(1, direction);
        positivePos = pos;
        while(this.chessBoard.isInBoard(curPos) &&
                this.chessBoard.chessmanArray[curPos.row][curPos.col] == color) {
            this.count++;
            positivePos = curPos;
            curPos = curPos.newPosition(1, direction);
        }

        //计算连子左右棋子情况
        for(int index = 1; index <= 4; index++) {
            curPos = negativePos.newPosition(-index, direction);
            if(this.chessBoard.isInBoard(curPos))
                this.negative.add(this.chessBoard.chessmanArray[curPos.row][curPos.col]);
            else
                this.negative.add(oppositeColor);

            curPos = positivePos.newPosition(index,direction);
            if(this.chessBoard.isInBoard(curPos))
                this.positive.add(this.chessBoard.chessmanArray[curPos.row][curPos.col]);
            else
                this.positive.add(oppositeColor);
        }
    }
}

