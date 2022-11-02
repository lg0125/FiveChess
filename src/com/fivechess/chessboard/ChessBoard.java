package com.fivechess.chessboard;

import com.fivechess.evaluate.ChessDirection;
import com.fivechess.evaluate.Direction;
import com.fivechess.evaluate.Evaluate;
import com.fivechess.evaluate.Position;
import com.fivechess.rule.ChessRule;

import javax.swing.JOptionPane;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

public class ChessBoard extends JPanel
        implements MouseListener, ActionListener, ChessBoardActions {
    public static final int sx = 80;//游戏区域方块的起始横坐标
    public static final int sy = 40;//游戏区域方块的起始纵坐标
    public static final int w = 40;//每个小方格的边长
    public static final int rw = 600;//游戏区域方块的边长
    public static final int Rows = 15;//行列数
    public static final int chessmanWidth = 30;//棋子直径
    public static final int radio = 2;

    public static final JButton startBtn = new JButton("开始下棋");
    public static final JButton againBtn = new JButton("再来一局");

    public ChessStart chessStart = new ChessStart(this);
    public Evaluate evaluate = new Evaluate(this);

    public Integer option;
    public Role role;

    public Integer step = 0;
    public Integer playerFifthHandNPlay = 1;
    public ChessState color = ChessState.BLACK;
    public ArrayList<Position> toJudge = new ArrayList<>();
    public ChessState[][] chessmanArray = new ChessState[Rows][Rows];//棋盘状态，0 无子，1 白子，2黑子


    public ChessBoard() {
        this.addMouseListener(this);

        this.add(startBtn);
        this.add(againBtn);

        startBtn.addActionListener(this);
        againBtn.addActionListener(this);

        startBtn.setVisible(true);
        againBtn.setVisible(false);

        //initialization
        for(int j = 0; j < Rows; j++){
            for(int i = 0; i < Rows; i++){
                this.chessmanArray[j][i] = ChessState.NOTHING;
            }
        }

        ChessRule.FORBIDDEN_HAND.init(this);
        ChessRule.THIRD_HAND_EXCHANGE.init(this);
        ChessRule.FIFTH_HAND_N_PLAY.init(this);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        this.drawBoard(g);
        this.drawAix(g);
        this.drawChessMan(g);
    }

    // 画棋盘
    public void drawBoard(Graphics g){
        g.setColor(new Color(230, 189, 128));
        g.fillRect(sx-20, sy-20, rw, rw);
        g.setColor(Color.GREEN);
        for(int i = 0; i < Rows; i++) {
            g.drawLine(sx - 20 + rw / Rows * (i + 1), sy - 20,
                    sx - 20 + rw / Rows * (i + 1), sy - 20 + rw);
            g.drawLine(sx - 20 , sy - 20 + rw / Rows * (i + 1),
                    sx - 20 + rw, sy - 20 + rw / Rows * (i + 1));
        }
    }

    //画坐标
    public void drawAix(Graphics g) {
        String[] Y_AIX = {
                "15", "14", "13", "12", "11",
                "10", "9", "8", "7", "6",
                "5", "4", "3", "2", "1"
        };
        String[] X_AIX = {
                "A", "B", "C", "D", "E",
                "F", "G", "H", "I", "J",
                "K", "L", "M", "N", "O"
        };
        
        g.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        g.setColor(Color.BLACK);
        int xAixXOffset = -25, xAixYOffset = 20, yAixXOffset = -20, yAixYOffset = -10;
        for(int i = 0; i < Rows; i++) {
            g.drawString(X_AIX[i], sx - 20 + rw / Rows * (i + 1) + xAixXOffset,
                    sy - 20 + rw + xAixYOffset);
            g.drawString(Y_AIX[i], sx - 20 + yAixXOffset,
                    sy - 20 + rw / Rows * (i + 1) + yAixYOffset);
        }
    }

    //画棋子
    public void drawChessMan(Graphics g){
        int chessman = chessmanWidth;
        for (int row = 0; row < Rows; row++) {
            for (int col = 0; col < Rows; col++) {
                if (chessmanArray[row][col] == ChessState.WHITE) {
                    g.setColor(Color.WHITE);
                    g.fillOval(sx + w * col - chessmanWidth / radio,
                            sy + w * row - chessmanWidth / radio, chessmanWidth, chessman);
                }else if (chessmanArray[row][col] == ChessState.BLACK) {
                    g.setColor(Color.BLACK);
                    g.fillOval(sx + w * col - chessmanWidth / radio,
                            sy + w * row - chessmanWidth / radio, chessmanWidth, chessman);
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == startBtn ) {
            startBtn.setVisible(false);

            this.option = JOptionPane.showConfirmDialog(null,
                    "只想赢一局 是否先手", "五子棋", JOptionPane.YES_NO_OPTION);

            switch (option) {
                case JOptionPane.YES_OPTION -> this.goFirst(Role.COMPUTER);
                case JOptionPane.NO_OPTION -> this.goFirst(Role.PLAYER);
            }
        } else if (e.getSource() == againBtn) {
            againBtn.setVisible(false);

            // Reset
            this.step = 0;
            this.playerFifthHandNPlay = 1;
            this.color = ChessState.BLACK;
            this.toJudge.clear();
            this.chessmanArray = new ChessState[Rows][Rows];
            for(int row = 0; row < Rows; row++){
                for(int col = 0; col < Rows; col++){
                    this.chessmanArray[row][col] = ChessState.NOTHING;
                }
            }
            // Reset
            ChessRule.FIFTH_HAND_N_PLAY.reset();


            //再来一局 反转
            switch (option) {
                case JOptionPane.YES_OPTION -> this.goFirst(Role.PLAYER);
                case JOptionPane.NO_OPTION -> this.goFirst(Role.COMPUTER);
            }
        } else if(e.getSource() == ChessRule.THIRD_HAND_EXCHANGE.exchangeBtn) {
            ChessRule.THIRD_HAND_EXCHANGE.exchangeBtn.setVisible(false);
            ChessRule.THIRD_HAND_EXCHANGE.whiteBtn.setVisible(true);
            ChessRule.THIRD_HAND_EXCHANGE.blackBtn.setVisible(true);
        } else if((e.getSource() == ChessRule.THIRD_HAND_EXCHANGE.whiteBtn) ||
                (e.getSource() == ChessRule.THIRD_HAND_EXCHANGE.blackBtn)) {
            ChessRule.THIRD_HAND_EXCHANGE.whiteBtn.setVisible(false);
            ChessRule.THIRD_HAND_EXCHANGE.blackBtn.setVisible(false);

            int isWhite = (e.getSource() == ChessRule.THIRD_HAND_EXCHANGE.whiteBtn) ? 1 : 0;
            ChessRule.THIRD_HAND_EXCHANGE.select = switch (isWhite) {
                case 1 -> ChessState.WHITE;
                case 0 -> ChessState.BLACK;
                default -> ChessState.NOTHING;
            };

            ChessRule.THIRD_HAND_EXCHANGE.run();
            ChessRule.THIRD_HAND_EXCHANGE.end();
        } else if(e.getSource() == ChessRule.FIFTH_HAND_N_PLAY.playBtn) {
            ChessRule.FIFTH_HAND_N_PLAY.playBtn.setVisible(false);


            ChessRule.FIFTH_HAND_N_PLAY.run();
            ChessRule.FIFTH_HAND_N_PLAY.end();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        int xCol = e.getX();
        int yRow = e.getY();
        int pxCol = (xCol - (sx - chessmanWidth / radio)) / w;
        int pyRow = (yRow - (sy - chessmanWidth / radio)) / w;

        Position pos = new Position(pyRow, pxCol);
        if (this.isInBoard(pos) &&
                (xCol - (sx - chessmanWidth / radio)) % w <= chessmanWidth &&
                (yRow - (sy - chessmanWidth / radio)) % w <= chessmanWidth &&
                chessmanArray[pyRow][pxCol] == ChessState.NOTHING) {
            //五手N打 打点
            if(this.color == ChessState.BLACK && this.step == 5 &&
                    this.playerFifthHandNPlay < ChessRule.FIFTH_HAND_N_PLAY.N) {
                if(ChessRule.FIFTH_HAND_N_PLAY.forbiddenPosList.contains(pos))
                    JOptionPane.showMessageDialog(null,
                            "五手两打的棋子不能下这里", "五手两打",
                            JOptionPane.ERROR_MESSAGE);
                else
                    this.goFifthHandNPlay(Role.PLAYER, pos);

                return;
            }

            this.setColorAndRepaint(pos);
            this.handleToJudge(pos); //对手五手N打要扩充处理域
            this.stateTrans();

            if(this.step < 5)
                ChessRule.FIFTH_HAND_N_PLAY.fourList.add(pos);

            //五手N打 指定N值
            if(this.color == ChessState.WHITE && this.step == 5) {
                ChessRule.FIFTH_HAND_N_PLAY.N = (Integer) JOptionPane.showInputDialog(null,
                        "请指定五手N打的N值", "五手N打", JOptionPane.WARNING_MESSAGE, null,
                        ChessRule.FIFTH_HAND_N_PLAY.N_VALUES, ChessRule.FIFTH_HAND_N_PLAY.N_VALUES[0]);
                ChessRule.FIFTH_HAND_N_PLAY.nPosList.add(pos);
                ChessRule.FIFTH_HAND_N_PLAY.handleForbiddenPos(pos, Role.PLAYER);

                this.stateTrans();
                return;
            }

            if(this.evaluate.chessGameJudge.isWinSuccessFive(pos)) {
                this.resultAndReset("对手 获胜");
            } else if(this.evaluate.chessGameJudge
                    .isForbiddenHand(ChessRule.FORBIDDEN_HAND.run(pos))) {
                this.resultAndReset("对手出现禁手 => 只想赢一局获胜");
            } else if (this.step == Rows * Rows) {
                this.resultAndReset("平局");
            } else {

                Position computerPos;
                do {
                    computerPos = this.evaluate.computerGo();
                } while (this.evaluate.chessGameJudge
                            .isForbiddenHand(ChessRule.FORBIDDEN_HAND.run(pos)));

                this.setColorAndRepaint(computerPos);
                this.handleToJudge(computerPos);
                this.stateTrans();

                if(this.step < 5)
                    ChessRule.FIFTH_HAND_N_PLAY.fourList.add(computerPos);

                this.goFifthHandNPlay(Role.COMPUTER, computerPos);

                if(this.evaluate.chessGameJudge.isWinSuccessFive(computerPos)) {
                    this.resultAndReset("只想赢一局 获胜");
                } else if (this.step == Rows * Rows) {
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

    @Override
    public void setColorAndRepaint(Position pos) {
        this.chessmanArray[pos.row][pos.col] = this.color;
        this.repaint();
        this.step++;
    }

    @Override
    public void resultAndReset(String text) {
        JOptionPane.showConfirmDialog(null, text, "五子棋", JOptionPane.DEFAULT_OPTION);
        againBtn.setVisible(true);
    }

    @Override
    public void handleToJudge(Position pos) {
        this.toJudge.remove(pos);
        for(int index = 1; index <= 4; index++) {
            Direction direction = switch (index) {
                case 1 -> ChessDirection.HORIZONTAL;
                case 2 -> ChessDirection.VERTICAL;
                case 3 -> ChessDirection.LEFT_DIAGONAL;
                case 4 -> ChessDirection.RIGHT_DIAGONAL;
                default -> null;
            };

            Position curPos;
            for(int len = -1; len <= 1; len++) {
                if(len != 0) {
                    curPos = pos.newPosition(len, direction);

                    if (this.isInBoard(curPos) &&
                            this.chessmanArray[curPos.row][curPos.col] == ChessState.NOTHING)
                        if (!this.toJudge.contains(curPos))
                            this.toJudge.add(curPos);
                }
            }
        }
    }

    @Override
    public void stateTrans() {
        this.color = switch (this.color) {
            case NOTHING -> ChessState.NOTHING;
            case WHITE -> ChessState.BLACK;
            case BLACK -> ChessState.WHITE;
        };
        this.role = switch (this.role) {
            case NOTHING -> Role.NOTHING;
            case PLAYER -> Role.COMPUTER;
            case COMPUTER -> Role.PLAYER;
        };
    }

    @Override
    public boolean isInBoard(Position pos) {
        return  pos.row >= 0 &&
                pos.row < ChessBoard.Rows &&
                pos.col >= 0 &&
                pos.col < ChessBoard.Rows;
    }

    @Override
    public void goFirst(Role firstRole) {
        switch (firstRole) {
            case COMPUTER -> this.role = Role.COMPUTER;
            case PLAYER -> this.role = Role.PLAYER;
        }

        String select = (String) JOptionPane.showInputDialog(null,
                "请指定一个开局", "指定开局", JOptionPane.WARNING_MESSAGE, null,
                ChessStart.START, ChessStart.START[0]);
        this.chessStart.run(select);
    }

    @Override
    public void goFifthHandNPlay(Role executeRole, Position position) {
        switch (executeRole) {
            case PLAYER -> {
                    this.chessmanArray[position.row][position.col] = this.color;
                    this.repaint();

                    this.handleToJudge(position);
                    ChessRule.FIFTH_HAND_N_PLAY.nPosList.add(position);
                    ChessRule.FIFTH_HAND_N_PLAY.handleForbiddenPos(position, executeRole);

                    this.playerFifthHandNPlay++;
                    if(this.playerFifthHandNPlay.equals(ChessRule.FIFTH_HAND_N_PLAY.N)) {
                        this.stateTrans();
                        ChessRule.FIFTH_HAND_N_PLAY.start();
                    }
            }

            case COMPUTER -> {
                if(this.step == 5) {
                    this.stateTrans();
                    this.handleToJudge(position);
                    ChessRule.FIFTH_HAND_N_PLAY.nPosList.add(position);
                    ChessRule.FIFTH_HAND_N_PLAY.handleForbiddenPos(position, executeRole);

                    ChessRule.FIFTH_HAND_N_PLAY.N = (Integer) JOptionPane.showInputDialog(null,
                            "请指定五手N打的N值", "五手N打", JOptionPane.WARNING_MESSAGE, null,
                            ChessRule.FIFTH_HAND_N_PLAY.N_VALUES, ChessRule.FIFTH_HAND_N_PLAY.N_VALUES[0]);

                    Position curPos;
                    for(int index = 2; index <= ChessRule.FIFTH_HAND_N_PLAY.N; index++) {
                        curPos = this.evaluate.computerGo();

                        this.handleToJudge(curPos);
                        this.chessmanArray[curPos.row][curPos.col] = this.color;

                        ChessRule.FIFTH_HAND_N_PLAY.nPosList.add(curPos);
                        ChessRule.FIFTH_HAND_N_PLAY.handleForbiddenPos(curPos, executeRole);
                    }
                    this.repaint();
                    this.stateTrans();

                    ChessRule.FIFTH_HAND_N_PLAY.start();
                }
            }
        }
    }
}


