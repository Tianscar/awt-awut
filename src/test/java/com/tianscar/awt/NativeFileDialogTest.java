package com.tianscar.awt;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;

public class NativeFileDialogTest {

    public static void main(String[] args) {
        Frame frame = new Frame();
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                FileDialog fileDialog = AWTFactory.createFileDialog(frame);
                fileDialog.setTitle("PNG, JPEG, GIF images");
                fileDialog.setMode(FileDialog.SAVE);
                fileDialog.setFile("image/png, image/jpeg, image/gif");
                fileDialog.setMultipleMode(true);
                fileDialog.setVisible(true);
                System.out.println(Arrays.toString(fileDialog.getFiles()));
            }
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
        frame.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println(e.getX() + ", " + e.getY());
            }
        });
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                frame.setVisible(true);
            }
        });
    }

}
