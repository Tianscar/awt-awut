package com.tianscar.awt.X11;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;


@IgnoreError
public interface Xcursor {

    Xcursor INSTANCE = X11Utils.initXcursor();

    XcursorImage XcursorImageCreate(@In int width, @In int height);
    void XcursorImageDestroy(@In XcursorImage image);
    long XcursorImageLoadCursor(@In long dpy, @In XcursorImage image);
    long XcursorLibraryLoadCursor(@In long dpy, @In CharSequence name);
    boolean XcursorSupportsARGB(@In long dpy);

}
