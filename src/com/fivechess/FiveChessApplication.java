package com.fivechess;

import com.fivechess.chessboard.FiveChessBoard;

import javax.swing.*;

public class FiveChessApplication {
    public static void main(String[] args) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.add(new FiveChessBoard());
        frame.setSize(800, 800);
        frame.setVisible(true);
    }
}
