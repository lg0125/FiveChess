package com.fivechess.chessboard;

import com.fivechess.chessrule.FiveChessRule;
import com.fivechess.evaluate.AiEvaluate;
import com.fivechess.evaluate.Dir;
import com.fivechess.evaluate.Point;

import javax.swing.*;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;



public class FiveChessBoard extends JPanel implements MouseListener, ActionListener {
    public static final int sx = 80;//游戏区域方块的起始横坐标
    public static final int sy = 40;//游戏区域方块的起始纵坐标
    public static final int w = 40;//每个小方格的边长
    public static final int rw = 600;//游戏区域方块的边长
    public static final int Rows = 15;//行列数
    public static final int chessmanWidth = 30;//棋子直径
    public static final int radio = 2;

    public static final Dir d1 = new Dir(0, 1); //横
    public static final Dir d2 = new Dir(1, 0); //竖
    public static final Dir d3 = new Dir(1, -1); //撇
    public static final Dir d4 = new Dir(1, 1); //捺

    public static ChessState[][] chessmanArray;//棋盘状态，0 无子，1 白子，2黑子
    static {
        chessmanArray = new ChessState[Rows][Rows];
        for(int j = 0; j < Rows; j++){
            for(int i = 0; i < Rows; i++){
                chessmanArray[j][i] = ChessState.NOTHING;
            }
        }
    }

    public static ArrayList<Point> toJudge = new ArrayList<>();

    public static Role role;
    public static ChessState color;

    private final JButton computerBtn = new JButton("只想赢一局 先手");
    private final JButton playerBtn = new JButton("对手 先手");
    private final JButton conformBtn = new JButton();

    public FiveChessBoard() {
        this.addMouseListener(this);
        this.add(computerBtn);
        this.add(playerBtn);
        this.add(conformBtn);
        this.computerBtn.addActionListener(this);
        this.playerBtn.addActionListener(this);
        this.conformBtn.addActionListener(this);
        this.conformBtn.setVisible(false);
    }

    // 画棋盘
    void DrawBoard(Graphics g){
        g.setColor(new Color(230, 189, 128));
        g.fillRect(sx-20, sy-20, rw, rw);
        g.setColor(Color.GREEN);
        for(int i = 0; i < Rows; i++) {
            g.drawLine(sx - 20 + rw / Rows * (i + 1), sy - 20, sx - 20 + rw / Rows * (i + 1), sy - 20 + rw);
            g.drawLine(sx - 20 , sy - 20 + rw / Rows * (i + 1), sx - 20 + rw, sy - 20 + rw / Rows * (i + 1));
        }
    }

    //画棋子
    void DrawChessMan(Graphics g){
        int chessman = chessmanWidth;
        for (int j = 0; j < Rows; j++) {
            for (int i = 0; i < Rows; i++) {
                if (chessmanArray[j][i].equals(ChessState.WHITE)) {
                    g.setColor(Color.WHITE);
                    g.fillOval(sx + w * i - chessmanWidth / radio, sy + w * j - chessmanWidth / radio, chessmanWidth, chessman);
                }else if (chessmanArray[j][i].equals(ChessState.BLACK)) {
                    g.setColor(Color.BLACK);
                    g.fillOval(sx + w * i - chessmanWidth / radio, sy + w * j - chessmanWidth / radio, chessmanWidth, chessman);
                }
            }
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        DrawBoard(g);
        DrawChessMan(g);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == computerBtn || e.getSource() == playerBtn) {
            this.computerBtn.setVisible(false);
            this.playerBtn.setVisible(false);

            this.init();

            int isComputerFirst = (e.getSource() == computerBtn) ? 1 : 0;
            role = switch (isComputerFirst) {
                case 1 -> Role.COMPUTER;
                case 0 -> Role.PLAYER;
                default -> Role.NOTHING;
            };
            color = ChessState.BLACK;

            Point p = new Point(7,7);
            this.setColorAndRepaint(p);
            this.stateTrans();
            this.handleToJudge(p);

            if(isComputerFirst == 0) {
                Point computerPoint = AiEvaluate.computerGo();
                assert computerPoint != null;
                this.setColorAndRepaint(computerPoint);
                this.handleToJudge(computerPoint);
                this.stateTrans();
            }
        } else if (e.getSource() == conformBtn) {
            this.computerBtn.setVisible(true);
            this.playerBtn.setVisible(true);
            this.conformBtn.setVisible(false);
        } else if(e.getSource() == FiveChessRule.THIRD_HAND_EXCHANGE.exchangeBtn) {
            FiveChessRule.THIRD_HAND_EXCHANGE.exchangeBtn.setVisible(false);
            FiveChessRule.THIRD_HAND_EXCHANGE.whiteBtn.setVisible(true);
            FiveChessRule.THIRD_HAND_EXCHANGE.blackBtn.setVisible(true);
        } else if (e.getSource() == FiveChessRule.THIRD_HAND_EXCHANGE.whiteBtn ||
                    e.getSource() == FiveChessRule.THIRD_HAND_EXCHANGE.blackBtn) {
            FiveChessRule.THIRD_HAND_EXCHANGE.whiteBtn.setVisible(false);
            FiveChessRule.THIRD_HAND_EXCHANGE.blackBtn.setVisible(false);

            int isWhite = (e.getSource() == FiveChessRule.THIRD_HAND_EXCHANGE.whiteBtn) ? 1 : 0;
            FiveChessRule.THIRD_HAND_EXCHANGE.select = switch (isWhite) {
                case 1 -> ChessState.WHITE;
                case 0 -> ChessState.BLACK;
                default -> ChessState.NOTHING;
            };

            FiveChessRule.THIRD_HAND_EXCHANGE.run();
            FiveChessRule.THIRD_HAND_EXCHANGE.end();
        } else if(e.getSource() == FiveChessRule.FIVE_HANDS_N_PLAY.playBtn) {
            FiveChessRule.FIVE_HANDS_N_PLAY.playBtn.setVisible(false);
            FiveChessRule.FIVE_HANDS_N_PLAY.point1Btn.setVisible(true);
            FiveChessRule.FIVE_HANDS_N_PLAY.point2Btn.setVisible(true);
        } else if (e.getSource() == FiveChessRule.FIVE_HANDS_N_PLAY.point1Btn ||
                    e.getSource() == FiveChessRule.FIVE_HANDS_N_PLAY.point2Btn) {
            FiveChessRule.FIVE_HANDS_N_PLAY.point1Btn.setVisible(false);
            FiveChessRule.FIVE_HANDS_N_PLAY.point2Btn.setVisible(false);

            FiveChessRule.FIVE_HANDS_N_PLAY.isP1 =
                    e.getSource() == FiveChessRule.FIVE_HANDS_N_PLAY.point1Btn;

            FiveChessRule.FIVE_HANDS_N_PLAY.run();
            FiveChessRule.FIVE_HANDS_N_PLAY.end();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int x = e.getX();
        int y = e.getY();
        int px = (x - (sx - chessmanWidth / radio)) / w;
        int py = (y - (sy - chessmanWidth / radio)) / w;

        if (px >= 0 && px < Rows &&
                py >= 0 && py < Rows &&
                (x - (sx - chessmanWidth / radio)) % w <= chessmanWidth &&
                (y - (sy - chessmanWidth / radio)) % w <= chessmanWidth &&
                chessmanArray[py][px].equals(ChessState.NOTHING)) {
            Point p = new Point(px, py);

            if((AiEvaluate.NUMBER + 1 == 6) && FiveChessRule.FIVE_HANDS_N_PLAY.isInvalid(p)) {
                JOptionPane.showMessageDialog(null,
                        "五手两打的第二手棋子不能下这里", "五手两打之错误",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            this.setColorAndRepaint(p);
            this.handleToJudge(p);
            this.stateTrans();

            if(AiEvaluate.NUMBER == 3) {
                FiveChessRule.THIRD_HAND_EXCHANGE.start(this);
                return;
            } else if(AiEvaluate.NUMBER == 5) {
                FiveChessRule.FIVE_HANDS_N_PLAY.p1 = p;
                FiveChessRule.FIVE_HANDS_N_PLAY.init();
                this.stateTrans();
                return;
            } else if(AiEvaluate.NUMBER == 6) {
                FiveChessRule.FIVE_HANDS_N_PLAY.p2 = p;
                FiveChessRule.FIVE_HANDS_N_PLAY.start(this);
                this.stateTrans();
                return;
            }

            if(AiEvaluate.isEnd(p)) {
                this.resultAndReset("Player获胜");
            } else if (AiEvaluate.NUMBER == Rows * Rows) {
                this.resultAndReset("平局");
            } else {
                Point computerPoint = AiEvaluate.computerGo();
                this.setColorAndRepaint(computerPoint);
                this.handleToJudge(computerPoint);
                this.stateTrans();

                if(AiEvaluate.NUMBER == 3)
                    FiveChessRule.THIRD_HAND_EXCHANGE.start(this);
                else if(AiEvaluate.NUMBER == 5) {
                    FiveChessRule.FIVE_HANDS_N_PLAY.p1 = computerPoint;
                    FiveChessRule.FIVE_HANDS_N_PLAY.init();
                    this.stateTrans();

                    Point newComputerPoint;
                    do {
                        newComputerPoint = AiEvaluate.computerGo();
                    } while (FiveChessRule.FIVE_HANDS_N_PLAY.isInvalid(newComputerPoint));

                    this.setColorAndRepaint(newComputerPoint);
                    this.handleToJudge(newComputerPoint);
                    this.stateTrans();

                    FiveChessRule.FIVE_HANDS_N_PLAY.p2 = newComputerPoint;
                    FiveChessRule.FIVE_HANDS_N_PLAY.start(this);
                    this.stateTrans();
                }

                if(AiEvaluate.isEnd(computerPoint)) {
                    this.resultAndReset("Computer获胜");
                } else if (AiEvaluate.NUMBER == Rows * Rows) {
                    this.resultAndReset("平局");
                }
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    private void init() {
        toJudge.clear();

        chessmanArray = new ChessState[Rows][Rows];
        for(int j = 0; j < Rows; j++){
            for(int i = 0; i < Rows; i++){
                chessmanArray[j][i] = ChessState.NOTHING;
            }
        }
    }

    public void setColorAndRepaint(Point p) {
        chessmanArray[p.y][p.x] = color;
        this.repaint();
        AiEvaluate.NUMBER++;

        switch (AiEvaluate.NUMBER) {
          case 1 -> FiveChessRule.THIRD_HAND_EXCHANGE.p1 = p;
          case 2 -> FiveChessRule.THIRD_HAND_EXCHANGE.p2 = p;
          case 3 -> FiveChessRule.THIRD_HAND_EXCHANGE.p3 = p;
        }
    }

    public void resultAndReset(String text) {
        this.conformBtn.setVisible(true);
        this.conformBtn.setText(text);
        AiEvaluate.NUMBER = 0;
        role = Role.NOTHING;
    }

    public void handleToJudge(Point p) {
        toJudge.remove(p);
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
                    Point point = p.newPoint(len, dir);
                    if (AiEvaluate.isInBoard(point) &&
                            chessmanArray[point.y][point.x].equals(ChessState.NOTHING))
                        if(!toJudge.contains(point)) toJudge.add(point);
                }
            }
        }
    }

    public void stateTrans() {
        color = switch (color) {
            case NOTHING -> ChessState.NOTHING;
            case WHITE -> ChessState.BLACK;
            case BLACK -> ChessState.WHITE;
        };
        role = switch (role) {
            case NOTHING -> Role.NOTHING;
            case PLAYER -> Role.COMPUTER;
            case COMPUTER -> Role.PLAYER;
        };
    }
}
