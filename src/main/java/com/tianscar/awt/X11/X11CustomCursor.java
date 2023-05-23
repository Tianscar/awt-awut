package com.tianscar.awt.X11;

import com.kenai.jffi.MemoryIO;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;
import sun.awt.AWTAccessor;
import sun.awt.CustomCursor;
import sun.awt.SunToolkit;

import java.awt.*;

class X11CustomCursor extends CustomCursor {

    private static final long serialVersionUID = 4990969689338375446L;

    public X11CustomCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException {
        super(cursor, hotSpot, name);
    }

    @Override
    protected void createNativeCursor(Image im, int[] pixels, int width, int height, int xHotSpot, int yHotSpot) {
        SunToolkit.awtLock();
        try {
            long display = X11Utils.getDisplay();
            if (display == 0) {
                AWTAccessor.getCursorAccessor().setPData(this, 0);
                return;
            }
            Xcursor xcursor = Xcursor.INSTANCE;
            if (xcursor == null) throw new IllegalStateException("could not load xcursor library");
            long pNativePixels = MemoryIO.getInstance().allocateMemory(pixels.length * 4L, false);
            Pointer nativePixels = Pointer.wrap(Runtime.getRuntime(xcursor), pNativePixels);
            nativePixels.put(0, pixels, 0, pixels.length);
            XcursorImage xCursorImage = xcursor.XcursorImageCreate(width, height);
            xCursorImage.xhot.set(xHotSpot);
            xCursorImage.yhot.set(yHotSpot);
            xCursorImage.pixels.set(nativePixels);
            long pData = xcursor.XcursorImageLoadCursor(display, xCursorImage);
            MemoryIO.getInstance().freeMemory(pNativePixels);
            xcursor.XcursorImageDestroy(xCursorImage);
            AWTAccessor.getCursorAccessor().setPData(this, pData);
        } finally {
            SunToolkit.awtUnlock();
        }
    }

}
