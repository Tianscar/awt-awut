package com.tianscar.awt.X11;

import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

import java.awt.*;

class X11SystemCursor extends Cursor {

    private static final long serialVersionUID = -2724726940047545807L;

    X11SystemCursor(String name, long pData) {
        super(name);
        SunToolkit.awtLock();
        try {
            AWTAccessor.getCursorAccessor().setPData(this, pData);
        } finally {
            SunToolkit.awtUnlock();
        }
    }

}
