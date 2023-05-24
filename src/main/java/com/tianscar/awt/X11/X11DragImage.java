package com.tianscar.awt.X11;

import java.awt.*;

public class X11DragImage extends Window {

    private static final long serialVersionUID = 4560214178309007999L;

    private final Image dragImage;

    public Image getDragImage() {
        return dragImage;
    }

    public X11DragImage(Image dragImage) throws HeadlessException {
        super(null);
        X11Utils.checkX11DragSourceSupported();
        setType(Type.POPUP);
        this.dragImage = dragImage;
        setSize(dragImage.getWidth(null), dragImage.getHeight(null));
        setBackground(new Color(0x00000000, true));
        setFocusableWindowState(false);
        setAlwaysOnTop(true);
        addNotify();
        long display = X11Utils.getDisplay();
        if (display == 0) throw new HeadlessException("could not get X display");
        long window = X11Utils.getXWindow(this);
        if (window == 0) throw new IllegalStateException("could not get X window");
        if (!X11Utils.setXWindowType(display, window, "_NET_WM_WINDOW_TYPE_DND"))
            throw new IllegalStateException("set X window type failed");
        if (!X11Utils.allowXWindowInputPassthrough(display, window))
            throw new IllegalStateException("set X window input-passthrough failed");
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (dragImage != null) {
            Graphics2D g2d = ((Graphics2D) g);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g2d.drawImage(dragImage, 0, 0, null);
        }
    }

}
