package com.tianscar.awt.X11;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class XcursorImage extends Struct {

    public XcursorImage(Runtime runtime) {
        super(runtime);
    }

    public final Unsigned32 version = new Unsigned32();
    public final Unsigned32 size = new Unsigned32();
    public final Unsigned32 width = new Unsigned32();
    public final Unsigned32 height = new Unsigned32();
    public final Unsigned32 xhot = new Unsigned32();
    public final Unsigned32 yhot = new Unsigned32();
    public final Unsigned32 delay = new Unsigned32();
    public final Pointer pixels = new Pointer();

}
