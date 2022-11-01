package com.fivechess.chessrule;

import com.fivechess.chessboard.ChessState;
import com.fivechess.chessboard.FiveChessBoard;
import com.fivechess.chessboard.Role;
import com.fivechess.evaluate.AiEvaluate;
import com.fivechess.evaluate.Point;
import javax.swing.JButton;


public class FiveHandsNPlay {
    public static final int N = 2;

    private FiveChessBoard chessBoard = null;

    public JButton point1Btn = new JButton();
    public JButton point2Btn = new JButton();
    public JButton playBtn = new JButton("五手两打");

    public Point p1 = null;
    public Point p2 = null;
    public Boolean isP1 = false;

    public Point forbiddenP1 = null;
    public Point forbiddenP2 = null;
    public Point forbiddenP3 = null;

    public void init() {
        assert this.p1 != null;

        // Point(j, i)
        int i = this.p1.y, j = this.p1.x;
        int n = FiveChessBoard.Rows;

        this.forbiddenP1 = new Point(i, j);
        this.forbiddenP2 = new Point(n - 1 - i, n - 1 - j);
        this.forbiddenP3 = new Point(n - 1 - j, n - 1 - i);
    }

    public boolean isInvalid(Point p) {
        assert this.forbiddenP1 != null &&
                this.forbiddenP2 != null &&
                this.forbiddenP3 != null;

        return p.equals(this.forbiddenP1) ||
                p.equals(this.forbiddenP2) ||
                p.equals(this.forbiddenP3);
    }

    public void start(FiveChessBoard fiveChessBoard) {
        assert this.p1 != null
                && this.p2 != null;

        point1Btn.setText("[" + p1.y + "," + p1.x + "]");
        point2Btn.setText("[" + p2.y + "," + p2.x + "]");

        chessBoard = fiveChessBoard;

        chessBoard.removeMouseListener(chessBoard);
        chessBoard.add(playBtn);
        chessBoard.add(point1Btn);
        chessBoard.add(point2Btn);
        playBtn.addActionListener(chessBoard);
        point1Btn.addActionListener(chessBoard);
        point2Btn.addActionListener(chessBoard);

        playBtn.setVisible(true);
        point1Btn.setVisible(false);
        point2Btn.setVisible(false);
    }

    public void end() {
        chessBoard.addMouseListener(chessBoard);

        playBtn.removeActionListener(chessBoard);
        point1Btn.removeActionListener(chessBoard);
        point2Btn.removeActionListener(chessBoard);

        chessBoard.remove(playBtn);
        chessBoard.remove(point1Btn);
        chessBoard.remove(point2Btn);
    }

    public void run() {
        Role role = FiveChessBoard.role;

        if(role == Role.COMPUTER) {
            if(isP1)
                FiveChessBoard.chessmanArray[p1.y][p1.x] = ChessState.WHITE;
            else
                FiveChessBoard.chessmanArray[p2.y][p2.x] = ChessState.WHITE;
            chessBoard.repaint();

            Point computerPoint = AiEvaluate.computerGo();
            chessBoard.setColorAndRepaint(computerPoint);
            chessBoard.handleToJudge(computerPoint);
            chessBoard.stateTrans();
        } else {
            if(isP1)
                FiveChessBoard.chessmanArray[p1.y][p1.x] = ChessState.WHITE;
            else
                FiveChessBoard.chessmanArray[p2.y][p2.x] = ChessState.WHITE;
            chessBoard.repaint();
        }
    }
}
