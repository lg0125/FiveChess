package com.fivechess.rule;

import com.fivechess.chessboard.*;
import com.fivechess.evaluate.Position;


import javax.swing.*;
import java.util.ArrayList;


public class FifthHandNPlay {
    public final Integer[] N_VALUES = {2, 3, 4, 5};
    public final JButton playBtn = new JButton("五手两打");

    public ChessBoard chessBoard;
    public ChessConnection chessConnection;

    public Integer N = 2;
    public Position select;
    public ArrayList<Position> nPosList = new ArrayList<>();
    public ArrayList<Position> fourList = new ArrayList<>();
    public ArrayList<Position> forbiddenPosList = new ArrayList<>();


    public void init(ChessBoard chessBoard){
        this.chessBoard = chessBoard;
        this.chessConnection = new ChessConnection(this.chessBoard);
    }

    public void reset() {
        this.nPosList.clear();
        this.forbiddenPosList.clear();
        this.fourList.clear();
    }

    public void start() {
        this.chessBoard.removeMouseListener(this.chessBoard);
        this.chessBoard.add(playBtn);

        this.playBtn.addActionListener(this.chessBoard);
        this.playBtn.setVisible(true);

        this.chessBoard.updateUI(); //刷新
    }

    public void run() {
        Role role = this.chessBoard.role;

        System.out.println("nPosList := " + this.nPosList);
        System.out.println("fourList := " + this.fourList);

        switch (role) {
            case COMPUTER -> {
                int score, minScore = 15;
                Position minPosition = null;
                ArrayList<Position> otherPosList;
                ArrayList<ChessConnType> connList;

                for(Position position : this.nPosList) {
                    otherPosList = new ArrayList<>(this.nPosList);
                    otherPosList.remove(position);

                    for(Position temp : otherPosList)
                        this.chessBoard.chessmanArray[temp.row][temp.col] = ChessState.NOTHING;

                    connList = this.chessConnection.getChessConnList(position, ChessState.BLACK);
                    score = this.chessConnection.estimateConnList(connList);
                    if(score < minScore) {
                        minScore = score;
                        minPosition = position;
                    }

                    for(Position temp : otherPosList)
                        this.chessBoard.chessmanArray[temp.row][temp.col] = ChessState.BLACK;
                }

                this.select = minPosition;

                otherPosList = new ArrayList<>(this.nPosList);
                otherPosList.remove(this.select);
                for(Position temp : otherPosList)
                    this.chessBoard.chessmanArray[temp.row][temp.col] = ChessState.NOTHING;
                this.chessBoard.repaint();

                // Reset
                this.chessBoard.toJudge.clear();
                for(Position position : this.fourList)
                    this.chessBoard.handleToJudge(position);
                this.chessBoard.handleToJudge(this.select);

                Position computerPos = this.chessBoard.evaluate.computerGo();
                this.chessBoard.setColorAndRepaint(computerPos);
                this.chessBoard.handleToJudge(computerPos);
                this.chessBoard.stateTrans();
            }

            case PLAYER -> {
                String[] nPosStrArr = new String[this.nPosList.size()];
                for(int i = 0; i < this.nPosList.size(); i++) {
                    nPosStrArr[i] = this.nPosList.get(i).toString();
                }

                String posStr = (String) JOptionPane.showInputDialog(null,
                        "请指定五手N打的黑棋的唯一位置", "五手N打", JOptionPane.WARNING_MESSAGE, null,
                        nPosStrArr, nPosStrArr[0]);

                for(Position position : this.nPosList) {
                    if (posStr.equals(position.toString()))
                        this.select = position;
                    else
                        this.chessBoard.chessmanArray[position.row][position.col] = ChessState.NOTHING;
                }
                this.chessBoard.repaint();

                // Reset
                this.chessBoard.toJudge.clear();
                for(Position position : this.fourList)
                    this.chessBoard.handleToJudge(position);
                this.chessBoard.handleToJudge(this.select);
            }
        }
    }

    public void handleForbiddenPos(Position pos, Role role) {
        int n = ChessBoard.Rows;
        Position pos1, pos2, pos3;

        pos1 = new Position(pos.col, pos.row);
        pos2 = new Position(n - 1 - pos.col, n - 1 - pos.row);
        pos3 = new Position(n - 1 - pos.row, n - 1 - pos.col);

        switch (role) {
            case PLAYER -> {
                this.forbiddenPosList.add(pos1);
                this.forbiddenPosList.add(pos2);
                this.forbiddenPosList.add(pos3);
            }

            case COMPUTER -> {
                this.chessBoard.toJudge.remove(pos1);
                this.chessBoard.toJudge.remove(pos2);
                this.chessBoard.toJudge.remove(pos3);
            }
        }
    }

    public void end() {
        this.reset();
        this.playBtn.removeActionListener(this.chessBoard);
        this.chessBoard.remove(playBtn);
        this.chessBoard.addMouseListener(this.chessBoard);
    }
}
