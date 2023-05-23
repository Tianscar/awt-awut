package com.tianscar.awt.X11;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class XRectangle extends Struct {

    public final Signed16 x = new Signed16();
    public final Signed16 y = new Signed16();
    public final Unsigned16 width = new Unsigned16();
    public final Unsigned16 height = new Unsigned16();

    public XRectangle(Runtime runtime) {
        super(runtime);
    }

}
