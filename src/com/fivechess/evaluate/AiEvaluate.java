package com.fivechess.evaluate;

import com.fivechess.chessboard.ChessState;
import com.fivechess.chessboard.FiveChessBoard;

import java.util.ArrayList;
import java.util.Arrays;

public class AiEvaluate {
    public static int NUMBER = 0;
    public static int SEARCH_DEPTH = 4;
    public static final int MAXN = 1<<28;
    public static final int MINN = -MAXN;

    public static final Dir d1 = new Dir(1, 0); //横
    public static final Dir d2 = new Dir(0, 1); //竖
    public static final Dir d3 = new Dir(-1, 1); //撇
    public static final Dir d4 = new Dir(1, 1); //捺

    public static Point computerGo() {
        Node node = new Node();
        dfs(0, node, MINN, MAXN);
        return node.bestChild.point;
    }

    // Alpha Beta Dfs
    private static void dfs(int deep, Node root, int alpha, int beta) {
        if(deep == SEARCH_DEPTH) {
            root.mark = getMark();
            return;
        }

        ArrayList<Point> judgeSet = new ArrayList<>(FiveChessBoard.toJudge);
        for(Point point : judgeSet) {
            Node node = new Node();
            node.setPoint(point);

            root.addChild(node);

            FiveChessBoard.chessmanArray[point.y][point.x] =
                    ((deep & 1) == 1) ? ChessState.WHITE : ChessState.BLACK;

            ChessState color = FiveChessBoard.chessmanArray[point.y][point.x];
            if(isEnd(point)) {
                root.bestChild = node;
                root.mark = MAXN * switch (color) {
                    case WHITE -> -1;
                    case BLACK -> 1;
                    default -> 0;
                };
                FiveChessBoard.chessmanArray[point.y][point.x] = ChessState.NOTHING;
                return;
            }

            int count = 0;
            boolean[] flags = new boolean[8];
            Arrays.fill(flags, true);

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
                        Point next = point.newPoint(len, dir);
                        if(next.x >= 0 && next.x < FiveChessBoard.Rows &&
                                next.y >= 0 && next.y < FiveChessBoard.Rows &&
                                FiveChessBoard.chessmanArray[next.y][next.x] == ChessState.NOTHING) {
                            if (!FiveChessBoard.toJudge.contains(next)) FiveChessBoard.toJudge.add(next);
                            else flags[count] = false;
                        }
                        count ++;
                    }
                }
            }

            boolean flag = FiveChessBoard.toJudge.contains(point);
            if(flag) FiveChessBoard.toJudge.remove(point);

            dfs(deep + 1, root.getLastChild(), alpha, beta);

            if(flag) FiveChessBoard.toJudge.add(point);
            for(int i = 0; i < 8; i++) if(flags[i]) FiveChessBoard.toJudge.remove(flag2point(point, i));
            FiveChessBoard.chessmanArray[point.y][point.x] = ChessState.NOTHING;

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

    private static Point flag2point(Point p,int n) { // n : 0 - 7
        int index = n / 2 + 1;
        int len = (n % 2 == 0) ? -1 : 1;
        Dir dir = switch (index) {
            case 1 -> d1;
            case 2 -> d2;
            case 3 -> d3;
            case 4 -> d4;
            default -> null;
        };
        assert dir != null;
        return p.newPoint(len, dir);
    }

    private static int getMark() {
        int result = 0;

        int count, x, y;
        boolean flag1, flag2;
        Point p, point = null;
        ChessState color;
        for(int row = 0; row < FiveChessBoard.Rows; row++) {
            for(int col = 0; col < FiveChessBoard.Rows; col++) {
                if(FiveChessBoard.chessmanArray[row][col] != ChessState.NOTHING) {
                    count = 1;
                    flag1 = false;
                    flag2 = false;
                    x = col;
                    y = row;

                    p = new Point(x, y);
                    color = FiveChessBoard.chessmanArray[y][x];
                    for(int index = 1; index <= 4; index++) {
                        Dir dir = switch (index) {
                            case 1 -> d1;
                            case 2 -> d2;
                            case 3 -> d3;
                            case 4 -> d4;
                            default -> null;
                        };

                        for(int len = -1; ; len--) {
                            point = p.newPoint(len, dir);
                            if(isInBoard(point) &&
                                    FiveChessBoard.chessmanArray[point.y][point.x] == color)
                                count++;
                            else break;
                        }
                        if(isInBoard(point) && FiveChessBoard.chessmanArray[point.y][point.x] == ChessState.NOTHING)
                            flag1 = true;

                        if(count >= 3) {
                            result = MAXN * switch (color) {
                                case NOTHING -> 0;
                                case WHITE -> -1;
                                case BLACK -> 1;
                            };
                            return result;
                        }

                        // +
                        for(int len = 1; ; len++) {
                            point = p.newPoint(len, dir);
                            if(isInBoard(point) && FiveChessBoard.chessmanArray[point.y][point.x] == color)
                                count++;
                            else break;
                        }
                        if(isInBoard(point) && FiveChessBoard.chessmanArray[point.y][point.x] == ChessState.NOTHING)
                            flag2 = true;

                        if(flag1 && flag2) {
                            result += switch (color) {
                                case BLACK -> count * count;
                                case WHITE -> - (count * count);
                                case NOTHING -> 0;
                            };
                        } else if (flag1 || flag2) {
                            result += switch (color) {
                                case BLACK -> count * count / 4;
                                case WHITE -> - (count * count) / 4;
                                case NOTHING -> 0;
                            };
                        }

                        if(count >= 5) {
                            result = MAXN * switch (color) {
                                case NOTHING -> 0;
                                case WHITE -> -1;
                                case BLACK -> 1;
                            };
                            return result;
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean isEnd(Point p){
        ChessState color = FiveChessBoard.chessmanArray[p.y][p.x];
        for(int i = 1; i <= 4; i++) {
            Dir dir = switch (i) {
                case 1 -> d1;
                case 2 -> d2;
                case 3 -> d3;
                case 4 -> d4;
                default -> null;
            };

            int count = 1;

            Point point;
            for(int len = 1; len <= 4; len++) {
                point = p.newPoint(len, dir);
                if(isInBoard(point) &&
                        FiveChessBoard.chessmanArray[point.y][point.x] == color)
                    count++;
                else break;
            }
            if(count >= 5) return true;

            for(int len = -1; len >= -4; len--) {
                point = p.newPoint(len, dir);
                if(isInBoard(point) &&
                        FiveChessBoard.chessmanArray[point.y][point.x] == color)
                    count++;
                else break;
            }
            if(count >= 5) return true;
        }

        return false;
    }

    public static boolean isInBoard(Point p) {
        return  p.x >= 0 &&
                p.x < FiveChessBoard.Rows &&
                p.y >= 0 &&
                p.y < FiveChessBoard.Rows;
    }
}
