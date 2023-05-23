package com.tianscar.awt.X11;

import jnr.ffi.Pointer;
import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;

@IgnoreError
public interface Xfixes {

    Xfixes INSTANCE = X11Utils.initXfixes();

    void
    XFixesSetWindowShapeRegion (@In long dpy, @In long win, @In int shape_kind,
                                @In int x_off, @In int y_off, @In Pointer region);
    Pointer
    XFixesCreateRegion (@In long dpy, @In Pointer rectangles, @In int nrectangles);
    void
    XFixesDestroyRegion (@In long dpy, @In Pointer region);

}
