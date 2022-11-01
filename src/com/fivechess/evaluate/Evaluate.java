package com.fivechess.evaluate;

import com.fivechess.chessboard.*;
import com.fivechess.judge.ChessGameJudge;

import java.util.ArrayList;
import java.util.Arrays;

public class Evaluate {
    public static final int SEARCH_DEPTH = 4;
    public static final int MAXN = 1<<28;
    public static final int MINN = -MAXN;

    public ChessBoard chessBoard;

    public ChessGameJudge chessGameJudge;
    public ChessConnection chessConnection;

    public Evaluate(ChessBoard chessBoard) {
        this.chessBoard = chessBoard;
        this.chessGameJudge = new ChessGameJudge(this.chessBoard);
        this.chessConnection = new ChessConnection(this.chessBoard);
    }

    public Position computerGo() {
        TreeNode node = new TreeNode();
        this.dfs(0, node, MINN, MAXN);
        return node.bestChild.position;
    }

    private void dfs(int deep, TreeNode root, int alpha, int beta) {
        if(deep == SEARCH_DEPTH) {
            root.mark = getMark();
            return;
        }

        ArrayList<Position> judgeSet = new ArrayList<>(this.chessBoard.toJudge);
        for(Position pos : judgeSet) {
            TreeNode node = new TreeNode();
            node.setPosition(pos);
            root.addChild(node);

            this.chessBoard.chessmanArray[pos.row][pos.col] =
                    ((deep & 1) == 1) ? ChessState.WHITE : ChessState.BLACK;

            ChessState color = this.chessBoard.chessmanArray[pos.row][pos.col];
            if(this.chessGameJudge.isWinSuccessFive(pos)) {
                root.bestChild = node;
                root.mark = MAXN * switch (color) {
                    case WHITE -> -1;
                    case BLACK -> 1;
                    default -> 0;
                };
                this.chessBoard.chessmanArray[pos.row][pos.col] = ChessState.NOTHING;
                return;
            }

            int count = 0;
            boolean[] flags = new boolean[8];
            Arrays.fill(flags, true);

            for(int index = 1; index <= 4; index++) {
                Direction direction = switch (index) {
                    case 1 -> ChessDirection.HORIZONTAL;
                    case 2 -> ChessDirection.VERTICAL;
                    case 3 -> ChessDirection.LEFT_DIAGONAL;
                    case 4 -> ChessDirection.RIGHT_DIAGONAL;
                    default -> null;
                };

                for(int len = -1; len <= 1; len++) {
                    if(len != 0) {
                        Position nextPos = pos.newPosition(len, direction);
                        if (this.chessBoard.isInBoard(nextPos) &&
                                this.chessBoard.chessmanArray[nextPos.row][nextPos.col] == ChessState.NOTHING) {
                            if (!this.chessBoard.toJudge.contains(nextPos))
                                this.chessBoard.toJudge.add(nextPos);
                            else
                                flags[count] = false;
                        }
                        count++;
                    }
                }
            }

            boolean flag = this.chessBoard.toJudge.contains(pos);
            if(flag) this.chessBoard.toJudge.remove(pos);

            this.dfs(deep + 1, root.getLastChild(), alpha, beta);

            if(flag) this.chessBoard.toJudge.add(pos);
            for(int index = 0; index < 8; index++)
                if(flags[index]) this.chessBoard.toJudge.remove(flag2position(pos, index));
            this.chessBoard.chessmanArray[pos.row][pos.col] = ChessState.NOTHING;

            // alpha beta剪枝
            if((deep & 1) == 1) {
                // min层
                if(root.bestChild == null || root.getLastChild().mark < root.bestChild.mark) {
                    root.bestChild = root.getLastChild();
                    root.mark = root.bestChild.mark;
                    if(root.mark <= MINN) root.mark += deep;
                    beta = Math.min(root.mark, beta);
                }

                if(root.mark <= alpha) return;
            } else {
                // max层
                if(root.bestChild == null || root.getLastChild().mark > root.bestChild.mark){
                    root.bestChild = root.getLastChild();
                    root.mark = root.bestChild.mark;
                    if(root.mark == MAXN) root.mark -= deep;
                    alpha = Math.max(root.mark, alpha);
                }

                if(root.mark >= beta) return;
            }
        }
    }

    private Position flag2position(Position pos, int n) {
        int index = n / 2 + 1;
        int len = (n % 2 == 0) ? -1 : 1;

        Direction direction = switch (index) {
            case 1 -> ChessDirection.HORIZONTAL;
            case 2 -> ChessDirection.VERTICAL;
            case 3 -> ChessDirection.LEFT_DIAGONAL;
            case 4 -> ChessDirection.RIGHT_DIAGONAL;
            default -> null;
        };

        assert direction != null;
        return pos.newPosition(len, direction);
    }

    private int getMark() {
        int result = 0;

        Position pos;
        ChessState color;

        ArrayList<ChessConnType> connList = new ArrayList<>();
        ChessConnType conn;

        for(int row = 0; row < FiveChessBoard.Rows; row++) {
            for(int col = 0; col < FiveChessBoard.Rows; col++) {
                if(this.chessBoard.chessmanArray[row][col] == ChessState.NOTHING) {
                    connList.clear();
                    pos = new Position(row, col);
                    color = this.chessBoard.chessmanArray[row][col];

                    for(int index = 1; index <= 4; index++) {
                        Direction direction = switch (index) {
                            case 1 -> ChessDirection.HORIZONTAL;
                            case 2 -> ChessDirection.VERTICAL;
                            case 3 -> ChessDirection.LEFT_DIAGONAL;
                            case 4 -> ChessDirection.RIGHT_DIAGONAL;
                            default -> null;
                        };

                        conn = this.chessConnection.getChessConnType(pos, color, direction);

                        if(this.isGreaterThree(conn)) {
                            result = MAXN * switch (color) {
                                case NOTHING -> 0;
                                case WHITE -> -1;
                                case BLACK -> 1;
                            };

                            return result;
                        }

                        connList.add(conn);
                    }

                    result += this.getScore(connList) * switch (color) {
                        case NOTHING -> 0;
                        case WHITE -> -1;
                        case BLACK -> 1;
                    };
                }
            }
        }

        return result;
    }

    private int getScore(ArrayList<ChessConnType> connList){
        int aliveFour = 0, spurFour = 0, dieFour = 0;
        int successiveAliveThree = 0, skipAliveThree = 0, dieThree = 0;
        int successiveAliveTwo = 0, skipAliveTwo = 0, dieTwo = 0;

        for(ChessConnType conn : connList) {
            switch (conn) {
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

    private boolean isGreaterThree(ChessConnType connType) {
        return  connType == ChessConnType.SUCCESSIVE_FIVE ||
                connType == ChessConnType.ALIVE_FOUR ||
                connType == ChessConnType.SPUR_FOUR ||
                connType == ChessConnType.DIE_FOUR ||
                connType == ChessConnType.SKIP_ACTIVE_THREE ||
                connType == ChessConnType.SUCCESSIVE_ALIVE_THREE ||
                connType == ChessConnType.DIE_THREE;

    }
}


