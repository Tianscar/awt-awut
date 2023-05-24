package com.tianscar.awt.X11;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;


public interface Xcursor {

    Xcursor INSTANCE = X11Utils.initXcursor();

    @IgnoreError
    XcursorImage XcursorImageCreate(@In int width, @In int height);
    @IgnoreError
    void XcursorImageDestroy(@In XcursorImage image);
    @IgnoreError
    long XcursorImageLoadCursor(@In long dpy, @In XcursorImage image);
    @IgnoreError
    long XcursorLibraryLoadCursor(@In long dpy, @In CharSequence name);
    @IgnoreError
    boolean XcursorSupportsARGB(@In long dpy);

}
