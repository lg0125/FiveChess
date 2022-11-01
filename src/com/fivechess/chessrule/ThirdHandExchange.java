package com.fivechess.chessrule;


import com.fivechess.chessboard.ChessState;
import com.fivechess.chessboard.FiveChessBoard;
import com.fivechess.chessboard.Role;
import com.fivechess.evaluate.AiEvaluate;
import com.fivechess.evaluate.Point;
import javax.swing.JButton;

public class ThirdHandExchange {
    public final JButton exchangeBtn = new JButton("三手可交换");
    public final JButton whiteBtn = new JButton("白棋");
    public final JButton blackBtn = new JButton("黑棋");

    private FiveChessBoard chessBoard = null;
    public ChessState select = null;

    public Point p1 = null;
    public Point p2 = null;
    public Point p3 = null;

    // after 3rd hand by Black side
    // before 4th hand by White side
    public void start(FiveChessBoard fiveChessBoard) {
        this.chessBoard = fiveChessBoard;

        this.chessBoard.removeMouseListener(chessBoard);

        this.chessBoard.add(exchangeBtn);
        this.chessBoard.add(whiteBtn);
        this.chessBoard.add(blackBtn);
        this.exchangeBtn.addActionListener(chessBoard);
        this.whiteBtn.addActionListener(chessBoard);
        this.blackBtn.addActionListener(chessBoard);

        this.exchangeBtn.setVisible(true);
        this.whiteBtn.setVisible(false);
        this.blackBtn.setVisible(false);
    }

    public void run() {
        if(FiveChessBoard.role == Role.COMPUTER) {
            if(this.select == ChessState.WHITE) this.computer();
            else {
                FiveChessBoard.role = Role.PLAYER;

                FiveChessBoard.toJudge.clear();
                this.chessBoard.handleToJudge(p1);
                this.chessBoard.handleToJudge(p3);
            }
        } else {
            if(this.select == ChessState.BLACK) {
                FiveChessBoard.role = Role.COMPUTER;

                FiveChessBoard.toJudge.clear();
                this.chessBoard.handleToJudge(p2);

                this.computer();
            }
        }
    }

    public void end() {
        this.chessBoard.addMouseListener(chessBoard);

        this.exchangeBtn.removeActionListener(chessBoard);
        this.whiteBtn.removeActionListener(chessBoard);
        this.blackBtn.removeActionListener(chessBoard);

        this.chessBoard.remove(exchangeBtn);
        this.chessBoard.remove(whiteBtn);
        this.chessBoard.remove(blackBtn);
    }

    private void computer() {
        Point computerPoint = AiEvaluate.computerGo();

        this.chessBoard.setColorAndRepaint(computerPoint);
        this.chessBoard.handleToJudge(computerPoint);
        this.chessBoard.stateTrans();
    }

}
