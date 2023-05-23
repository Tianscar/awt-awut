package com.tianscar.awt.X11;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class XEvent extends Struct {
    protected XEvent(Runtime runtime) {
        super(runtime);
    }
}
