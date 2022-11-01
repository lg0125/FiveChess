package com.fivechess.chessboard;


import com.fivechess.evaluate.Direction;
import com.fivechess.evaluate.Position;

public class ChessConnectionJudge {

    public ChessBoard chessBoard;

    public ChessConnectionJudge(ChessBoard chessBoard) { this.chessBoard = chessBoard; }

    public ConnectionJudgeResult isSuccessiveFive(Position pos, ChessState color,
                                    Direction direction) {
        int count = 1, length = 4;

        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);

        count += positiveScan.addition + negativeScan.addition;

        if(count == 5)
            return new ConnectionJudgeResult(true, ChessConnectionType.SUCCESSIVE_FIVE);
        else
            return new ConnectionJudgeResult(false, ChessConnectionType.SUCCESSIVE_FIVE);
    }

    public ConnectionJudgeResult isSuccessiveLong(Position pos, ChessState color,
                                    Direction direction) {
        int count = 1, length = 5;

        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);

        count += positiveScan.addition + negativeScan.addition;

        if(count >= 6)
            return new ConnectionJudgeResult(true, ChessConnectionType.SUCCESSIVE_LONG);
        else
            return new ConnectionJudgeResult(false, ChessConnectionType.SUCCESSIVE_LONG);
    }

    public ConnectionJudgeResult isAliveFour(Position pos, ChessState color,
                               Direction direction) {
        int count = 1, length = 4;
        boolean countFlag = false, twoEndsAreNothingFlag = false;
        
        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);
        ChessState positiveScanColor = positiveScan.stopColor,
                negativeScanColor = negativeScan.stopColor;
        
        count += positiveScan.addition + negativeScan.addition;
        if(count == 4) countFlag = true;
        
        if(positiveScanColor == ChessState.NOTHING &&
                negativeScanColor == ChessState.NOTHING)
            twoEndsAreNothingFlag = true;

        if(countFlag && twoEndsAreNothingFlag)
            return new ConnectionJudgeResult(true, ChessConnectionType.ALIVE_FOUR);
        else
            return new ConnectionJudgeResult(false,ChessConnectionType.ALIVE_FOUR);
    }

    public ConnectionJudgeResult isSpurFour(Position pos, ChessState color,
                              Direction direction) {
        int count = 1, length = 4;
        boolean countFlag = false, twoEndsAreOppositeNothingFlag = false;
        ChessState oppositeColor = switch (color) {
            case WHITE -> ChessState.BLACK;
            case BLACK -> ChessState.WHITE;
            case NOTHING -> ChessState.NOTHING;
        };
        
        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);
        ChessState positiveScanColor = positiveScan.stopColor,
                negativeScanColor = negativeScan.stopColor;
        int positiveScanStopLen = positiveScan.stopLen,
                negativeScanStopLen = negativeScan.stopLen;

        
        if((positiveScanColor == oppositeColor &&
                negativeScanColor == ChessState.NOTHING) ||
                (positiveScanColor == ChessState.NOTHING &&
                        negativeScanColor == oppositeColor))
            twoEndsAreOppositeNothingFlag = true;
        count += positiveScan.addition + negativeScan.addition;
        if(count == 4) countFlag = true;
        if(twoEndsAreOppositeNothingFlag && countFlag)
            return new ConnectionJudgeResult(true, ChessConnectionType.SPUR_FOUR);

        if(!countFlag) {
            Position newPos;
            if(positiveScanColor == ChessState.NOTHING) {
                for(int positiveIndex = 1; ; positiveIndex++) {
                    newPos = pos.newPosition(positiveScanStopLen + positiveIndex, direction);
                    if(this.chessBoard.chessmanArray[newPos.row][newPos.col] == color) count++;
                    else break;
                }
            } else {
                for(int positiveIndex = 1; ; positiveIndex++) {
                    newPos = pos.newPosition(negativeScanStopLen - positiveIndex, direction);
                    if(this.chessBoard.chessmanArray[newPos.row][newPos.col] == color) count++;
                    else break;
                }
            }
            if(count == 4) countFlag = true;
        }
        if(countFlag) new ConnectionJudgeResult(true, ChessConnectionType.SPUR_FOUR);

        return new ConnectionJudgeResult(false, ChessConnectionType.SPUR_FOUR);
    }

    public ConnectionJudgeResult isDieFour(Position pos, ChessState color,
                             Direction direction) {
        int count = 1, length = 4;
        boolean countFlag = false, twoEndsAreOppositeFlag = false;
        ChessState oppositeColor = switch (color) {
            case WHITE -> ChessState.BLACK;
            case BLACK -> ChessState.WHITE;
            case NOTHING -> ChessState.NOTHING;
        };

        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);
        ChessState positiveScanColor = positiveScan.stopColor,
                negativeScanColor = negativeScan.stopColor;


        count += positiveScan.addition + negativeScan.addition;
        if(count == 4) countFlag = true;

        if(positiveScanColor == oppositeColor && negativeScanColor == oppositeColor)
            twoEndsAreOppositeFlag = true;

        if(countFlag && twoEndsAreOppositeFlag)
            return new ConnectionJudgeResult(true, ChessConnectionType.DIE_FOUR);

        return new ConnectionJudgeResult(false, ChessConnectionType.DIE_FOUR);
    }

    public ConnectionJudgeResult isSuccessiveAliveThree(Position pos, ChessState color,
                                          Direction direction) {
        int count = 1, length = 4;
        boolean countFlag = false, twoEndsAreNothingFlag = false, isAliveFourFlag = false;
        Position pos1 = null, pos2 = null;

        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);
        ChessState positiveScanColor = positiveScan.stopColor,
                negativeScanColor = negativeScan.stopColor;
        int positiveScanStopLen = positiveScan.stopLen,
                negativeScanStopLen = negativeScan.stopLen;

        count += positiveScan.addition + negativeScan.addition;
        if(count == 3) countFlag = true;

        if(positiveScanColor == ChessState.NOTHING &&
                negativeScanColor == ChessState.NOTHING) {
            twoEndsAreNothingFlag = true;
            pos1 = pos.newPosition(positiveScanStopLen, direction);
            pos2 = pos.newPosition(negativeScanStopLen, direction);
        }

        if(pos1 != null && pos2 != null) {
            boolean flag1 = this.isAliveFour(pos1, color, direction).result;
            boolean flag2 = this.isAliveFour(pos2, color, direction).result;
            if(flag1 || flag2) isAliveFourFlag = true;
        }

        if(countFlag && twoEndsAreNothingFlag && isAliveFourFlag)
            return new ConnectionJudgeResult(true, ChessConnectionType.SUCCESSIVE_ALIVE_THREE);

        return new ConnectionJudgeResult(false, ChessConnectionType.SUCCESSIVE_ALIVE_THREE);
    }
    public ConnectionJudgeResult isSkipAliveThree(Position pos, ChessState color,
                                    Direction direction) {
        int count = 1, length = 4;
        boolean countFlag = false, twoEndsAreNothingFlag = false, isAliveFourFlag = false;
        Position chessPos = null;

        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);
        ChessState positiveScanColor = positiveScan.stopColor,
                negativeScanColor = negativeScan.stopColor;
        int positiveScanStopLen = positiveScan.stopLen,
                negativeScanStopLen = negativeScan.stopLen;

        if(positiveScanColor == ChessState.NOTHING &&
                negativeScanColor == ChessState.NOTHING)
            twoEndsAreNothingFlag = true;

        count += positiveScan.addition + positiveScan.addition;

        if(count < 3) {
            Position newPos;
            for(int positiveIndex = 1; ; positiveIndex++) {
                newPos = pos.newPosition(positiveScanStopLen + positiveIndex, direction);
                if(this.chessBoard.chessmanArray[newPos.row][newPos.col] == color) {
                    count++;
                    chessPos = pos.newPosition(positiveScanStopLen, direction);
                }
                else break;
            }
            for(int negativeIndex = 1; ; negativeIndex++) {
                newPos = pos.newPosition(negativeScanStopLen - negativeIndex, direction);
                if(this.chessBoard.chessmanArray[newPos.row][newPos.col] == color) {
                    count++;
                    chessPos = pos.newPosition(negativeScanStopLen, direction);
                }
                else break;
            }
            if(count == 3) countFlag = true;
        }

        if(chessPos != null) {
            isAliveFourFlag = this.isAliveFour(chessPos, color, direction).result;
        }

        if(countFlag && twoEndsAreNothingFlag && isAliveFourFlag)
            return new ConnectionJudgeResult(true, ChessConnectionType.SKIP_ALIVE_THREE);


        return new ConnectionJudgeResult(false, ChessConnectionType.SKIP_ALIVE_THREE);
    }
    public ConnectionJudgeResult isSleepThree(Position pos, ChessState color,
                                Direction direction) {
        int count = 1, length = 4;
        boolean countFlag = false, twoEndsAreOppositeNothingFlag = false, isSpurFourFlag = false;
        Position chessPos = null;
        ChessState oppositeColor = switch (color) {
            case WHITE -> ChessState.BLACK;
            case BLACK -> ChessState.WHITE;
            case NOTHING -> ChessState.NOTHING;
        };

        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);
        ChessState positiveScanColor = positiveScan.stopColor,
                negativeScanColor = negativeScan.stopColor;
        int positiveScanStopLen = positiveScan.stopLen,
                negativeScanStopLen = negativeScan.stopLen;

        count += positiveScan.addition + negativeScan.addition;
        if(count == 3) countFlag = true;

        if(countFlag) {
            if(positiveScanColor == oppositeColor && negativeScanColor == ChessState.NOTHING) {
                twoEndsAreOppositeNothingFlag = true;
                chessPos = pos.newPosition(negativeScan.stopLen, direction);
            } else if(positiveScanColor == ChessState.NOTHING && negativeScanColor == oppositeColor) {
                twoEndsAreOppositeNothingFlag = true;
                chessPos = pos.newPosition(positiveScan.stopLen, direction);
            }

            if(chessPos != null) isSpurFourFlag = this.isSpurFour(chessPos, color, direction).result;

            if(twoEndsAreOppositeNothingFlag && isSpurFourFlag)
                return new ConnectionJudgeResult(true, ChessConnectionType.SLEEP_THREE);
        } else {
            Position goPos;
           if(positiveScanColor == ChessState.NOTHING) {
               goPos = pos.newPosition(positiveScanStopLen, direction);
               isSpurFourFlag = this.isSpurFour(goPos, color, direction).result;
           }
           if(negativeScanColor == ChessState.NOTHING) {
               goPos = pos.newPosition(negativeScanStopLen, direction);
               isSpurFourFlag = this.isSpurFour(goPos, color, direction).result;
           }
           if(isSpurFourFlag) return new ConnectionJudgeResult(true, ChessConnectionType.SLEEP_THREE);
        }

        return new ConnectionJudgeResult(false, ChessConnectionType.SLEEP_THREE);
    }
    public ConnectionJudgeResult isDieThree(Position pos, ChessState color,
                              Direction direction) {
        int count = 1, length = 4;
        boolean countFlag = false, twoEndsAreOppositeFlag = false;
        ChessState oppositeColor = switch (color) {
            case WHITE -> ChessState.BLACK;
            case BLACK -> ChessState.WHITE;
            case NOTHING -> ChessState.NOTHING;
        };

        ConnectScanResult positiveScan, negativeScan;
        positiveScan = this.positiveDirectionScan(pos, color, direction, length);
        negativeScan = this.negativeDirectionScan(pos, color, direction, length);
        ChessState positiveScanColor = positiveScan.stopColor,
                negativeScanColor = negativeScan.stopColor;
        int positiveScanStopLen = positiveScan.stopLen,
                negativeScanStopLen = negativeScan.stopLen;

        count += positiveScan.addition + negativeScan.addition;

        if(count == 3) countFlag = true;
        if(positiveScanColor == oppositeColor && negativeScanColor == oppositeColor)
            twoEndsAreOppositeFlag = true;
        if(countFlag && twoEndsAreOppositeFlag) return new ConnectionJudgeResult(true, ChessConnectionType.DIE_THREE);

        if(!twoEndsAreOppositeFlag) {
            if(positiveScanColor == ChessState.NOTHING) {
                Position chessPos = pos.newPosition(positiveScanStopLen, direction);
                boolean isPositiveDieFourFlag = this.isDieFour(chessPos, color, direction).result;
                if(isPositiveDieFourFlag)
                    return new ConnectionJudgeResult(true, ChessConnectionType.DIE_THREE);
            } else {
                Position chessPos = pos.newPosition(negativeScanStopLen, direction);
                boolean isNegativeDieFourFlag = this.isDieFour(chessPos, color, direction).result;
                if(isNegativeDieFourFlag)
                    return new ConnectionJudgeResult(true, ChessConnectionType.DIE_THREE);
            }
        }

        return new ConnectionJudgeResult(false, ChessConnectionType.DIE_THREE);
    }

    public ConnectionJudgeResult isSuccessiveAliveTwo(Position pos, ChessState color,
                                        Direction direction) {
        return null;
    }
    public ConnectionJudgeResult isSkipAliveTwo(Position pos, ChessState color,
                                  Direction direction) {
        return null;
    }
    public ConnectionJudgeResult isAdvancedSkipAliveTwo(Position pos, ChessState color,
                                          Direction direction) {
        return null;
    }
    public ConnectionJudgeResult isSleepTwo(Position pos, ChessState color,
                              Direction direction) {
        return null;
    }
    public ConnectionJudgeResult isDieTwo(Position pos, ChessState color,
                            Direction direction) {
        return null;
    }


    private ConnectScanResult positiveDirectionScan(Position pos, ChessState color,
                                      Direction direction, int length) {
        int addition = 0;
        int stopLen = 0;
        ChessState stopColor = ChessState.NOTHING;

        Position curPos;
        for(int len = 1; len <= length; len++) {
            curPos = pos.newPosition(len, direction);
            if(this.isInBoard(curPos) &&
                    this.chessBoard.chessmanArray[curPos.row][curPos.col] == color)
                addition++;
            else {
                if(!(this.chessBoard.chessmanArray[curPos.row][curPos.col] == color)) {
                    stopLen = len;
                    stopColor = this.chessBoard.chessmanArray[curPos.row][curPos.col];
                }
                break;
            }
        }

        return new ConnectScanResult(addition, stopLen, stopColor);
    }

    private ConnectScanResult negativeDirectionScan(Position pos, ChessState color,
                                         Direction direction, int length) {
        int addition = 0;
        int stopLen = 0;
        ChessState stopColor = ChessState.NOTHING;

        Position curPos;

        for(int len = -1; len >= -length; len--) {
            curPos = pos.newPosition(len, direction);
            if(this.isInBoard(curPos) &&
                    this.chessBoard.chessmanArray[curPos.row][curPos.col] == color)
                addition++;
            else {
                if(!(this.chessBoard.chessmanArray[curPos.row][curPos.col] == color)) {
                    stopLen = len;
                    stopColor = this.chessBoard.chessmanArray[curPos.row][curPos.col];
                }
                break;
            }
        }

        return new ConnectScanResult(addition, stopLen, stopColor);
    }

    private boolean isInBoard(Position pos) {
        return  pos.row >= 0 &&
                pos.row < ChessBoard.Rows &&
                pos.col >= 0 &&
                pos.col < ChessBoard.Rows;
    }
}

class ConnectScanResult {
    int addition;
    int stopLen;
    ChessState stopColor;

    public ConnectScanResult(int addition, int stopLen, ChessState stopColor) {
        this.addition = addition;
        this.stopLen = stopLen;
        this.stopColor = stopColor;
    }
}

class ConnectionJudgeResult {
    boolean result;
    ChessConnectionType type;
    
    public ConnectionJudgeResult(boolean result, ChessConnectionType type) {
        this.result = result;
        this.type = type;
    }
}


