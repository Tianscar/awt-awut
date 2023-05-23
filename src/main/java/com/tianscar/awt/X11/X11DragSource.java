package com.tianscar.awt.X11;

import java.awt.*;
import java.awt.datatransfer.FlavorMap;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.*;

public class X11DragSource extends DragSource {

    private static final long serialVersionUID = -4721172941532726528L;

    private volatile X11DragImage dragImage = null;
    private volatile Point imageOffset = null;

    public X11DragSource() throws HeadlessException {
        DragSourceAdapter adapter = new DragSourceAdapter() {
            @Override
            public void dragMouseMoved(DragSourceDragEvent dsde) {
                Point location = dsde.getLocation();
                if (imageOffset != null) {
                    location.x += imageOffset.x;
                    location.y += imageOffset.y;
                }
                dragImage.setLocation(location);
                super.dragMouseMoved(dsde);
            }
            @Override
            public void dragDropEnd(DragSourceDropEvent dsde) {
                if (dragImage != null) {
                    dragImage.dispose();
                    dragImage = null;
                    imageOffset = null;
                }
                super.dragDropEnd(dsde);
            }
        };
        addDragSourceListener(adapter);
        addDragSourceMotionListener(adapter);
    }

    @Override
    public void startDrag(DragGestureEvent trigger,
                          Cursor dragCursor,
                          Image dragImage,
                          Point imageOffset,
                          Transferable transferable,
                          DragSourceListener dsl,
                          FlavorMap flavorMap) throws InvalidDnDOperationException {
        super.startDrag(trigger, dragCursor, dragImage, imageOffset, transferable, dsl, flavorMap);
        this.dragImage = new X11DragImage(dragImage);
        this.imageOffset = imageOffset;
        Point location = MouseInfo.getPointerInfo().getLocation();
        if (imageOffset != null) {
            location.x += imageOffset.x;
            location.y += imageOffset.y;
            this.dragImage.setLocation(location);
        }
        EventQueue.invokeLater(() -> X11DragSource.this.dragImage.setVisible(true));
    }

}
