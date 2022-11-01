package com.fivechess.rule;

import com.fivechess.chessboard.ChessBoard;
import com.fivechess.chessboard.ChessState;
import com.fivechess.chessboard.Role;
import com.fivechess.evaluate.Evaluate;
import com.fivechess.evaluate.Position;

import javax.swing.*;

public class ThirdHandExchange {
    public final JButton exchangeBtn = new JButton("三手可交换");
    public final JButton whiteBtn = new JButton("白棋");
    public final JButton blackBtn = new JButton("黑棋");

    public ChessBoard chessBoard;
    public Evaluate evaluate;

    public ChessState select;

    public void init(ChessBoard chessBoard){
        this.chessBoard = chessBoard;
        this.evaluate = new Evaluate(this.chessBoard);
    }

    public void start() {
        this.chessBoard.removeMouseListener(this.chessBoard);
        this.chessBoard.add(exchangeBtn);
        this.chessBoard.add(whiteBtn);
        this.chessBoard.add(blackBtn);
        
        exchangeBtn.addActionListener(this.chessBoard);
        whiteBtn.addActionListener(this.chessBoard);
        blackBtn.addActionListener(this.chessBoard);

        exchangeBtn.setVisible(true);
        whiteBtn.setVisible(false);
        blackBtn.setVisible(false);
    }

    public void run() {
        if(this.chessBoard.role == Role.COMPUTER) {
            if(this.select == ChessState.WHITE) {
                Position computerPos = this.evaluate.computerGo();
                this.chessBoard.setColorAndRepaint(computerPos);
                this.chessBoard.handleToJudge(computerPos);
                this.chessBoard.stateTrans();

                ChessRule.FIFTH_HAND_N_PLAY.fourList.add(computerPos);
            }
            else
                this.chessBoard.role = Role.PLAYER;
        } else {
            if(this.select == ChessState.BLACK) {
                this.chessBoard.role = Role.COMPUTER;

                Position computerPos = this.evaluate.computerGo();
                this.chessBoard.setColorAndRepaint(computerPos);
                this.chessBoard.handleToJudge(computerPos);
                this.chessBoard.stateTrans();

                ChessRule.FIFTH_HAND_N_PLAY.fourList.add(computerPos);
            }
        }
    }

    public void end() {
        this.chessBoard.addMouseListener(this.chessBoard);

        exchangeBtn.removeActionListener(this.chessBoard);
        whiteBtn.removeActionListener(this.chessBoard);
        blackBtn.removeActionListener(this.chessBoard);

        this.chessBoard.remove(exchangeBtn);
        this.chessBoard.remove(whiteBtn);
        this.chessBoard.remove(blackBtn);
    }
}
