package com.tianscar.awt.X11;

import com.tianscar.awt.X11.X11DragSource;
import com.tianscar.awt.X11.X11Utils;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

public class X11DragSourceWithImageTest {

    public static void main(String[] args) {
        X11Utils.fixDragAndDropCursors();
        Frame frame = new Frame();
        frame.setSize(640, 480);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.dispose();
            }
        });
        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                super.keyTyped(e);
                System.exit(0);
            }
        });
        Label label = new Label("In general its a label supports drag and drop with an image.");
        label.setBackground(new Color(0x00FFFFFF, true));
        frame.add(label);
        frame.pack();
        DragSource dragSource = new X11DragSource();
        dragSource.createDefaultDragGestureRecognizer(label, DnDConstants.ACTION_COPY, new DragGestureListener() {
            @Override
            public void dragGestureRecognized(DragGestureEvent dge) {
                Image dragImage = new BufferedImage(label.getWidth(), label.getHeight(), BufferedImage.TYPE_INT_ARGB);
                label.printAll(dragImage.getGraphics());
                Point offset = new Point(0, 0);
                offset.x = -dragImage.getWidth(null) / 2;
                offset.y = -dragImage.getHeight(null) / 2;
                dge.startDrag(null, dragImage, offset, new StringSelection(label.getText()), null);
            }
        });
        EventQueue.invokeLater(() -> frame.setVisible(true));
    }

}
