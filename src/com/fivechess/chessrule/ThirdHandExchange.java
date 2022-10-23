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
        chessBoard = fiveChessBoard;

        chessBoard.removeMouseListener(chessBoard);

        chessBoard.add(exchangeBtn);
        chessBoard.add(whiteBtn);
        chessBoard.add(blackBtn);
        exchangeBtn.addActionListener(chessBoard);
        whiteBtn.addActionListener(chessBoard);
        blackBtn.addActionListener(chessBoard);

        exchangeBtn.setVisible(true);
        whiteBtn.setVisible(false);
        blackBtn.setVisible(false);
    }

    public void run() {
        if(FiveChessBoard.role == Role.COMPUTER) {
            if(select == ChessState.WHITE) this.computer();
            else {
                FiveChessBoard.role = Role.PLAYER;

                FiveChessBoard.toJudge.clear();
                chessBoard.handleToJudge(p1);
                chessBoard.handleToJudge(p3);
            }
        } else {
            if(select == ChessState.BLACK) {
                FiveChessBoard.role = Role.COMPUTER;

                FiveChessBoard.toJudge.clear();
                chessBoard.handleToJudge(p2);

                this.computer();
            }
        }
    }

    public void end() {
        chessBoard.addMouseListener(chessBoard);

        exchangeBtn.removeActionListener(chessBoard);
        whiteBtn.removeActionListener(chessBoard);
        blackBtn.removeActionListener(chessBoard);

        chessBoard.remove(exchangeBtn);
        chessBoard.remove(whiteBtn);
        chessBoard.remove(blackBtn);
    }

    private void computer() {
        Point computerPoint = AiEvaluate.computerGo();
        chessBoard.setColorAndRepaint(computerPoint);
        chessBoard.handleToJudge(computerPoint);
        chessBoard.stateTrans();
    }

}
