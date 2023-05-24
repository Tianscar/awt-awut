package com.tianscar.awt.X11;

import jnr.ffi.NativeLong;
import jnr.ffi.Pointer;
import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;
import jnr.ffi.annotations.Out;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.NativeLongByReference;
import jnr.ffi.byref.PointerByReference;

public interface Xlib extends Xatom, X {

    Xlib INSTANCE = X11Utils.initXlib();

    @IgnoreError
    long XDefaultRootWindow(@In long display);
    @IgnoreError
    long XInternAtom(@In long display, @In CharSequence atom_name, @In boolean only_if_exists);
    @IgnoreError
    int XUngrabKeyboard(@In long display, @In long time);
    @IgnoreError
    int XUngrabPointer(@In long display, @In long time);
    @IgnoreError
    int XSendEvent(@In long display, @In long w, @In boolean propagate, @In NativeLong event_mask, @In XEvent event_send);
    @IgnoreError
    int XGetWindowProperty(@In long display,
                           @In long w,
                           @In long property,
                           @In NativeLong long_offset,
                           @In NativeLong long_length,
                           @In boolean delete,
                           @In long req_type,
                           @Out NativeLongByReference actual_type_return,
                           @Out IntByReference actual_format_return,
                           @Out NativeLongByReference nitems_return,
                           @Out NativeLongByReference bytes_after_return,
                           @Out PointerByReference prop_return);
    @IgnoreError
    int XFree(@In long data);
    @IgnoreError
    int XFlush(@In long display);
    @IgnoreError
    long XCreateFontCursor(@In long display, @In int shape);
    @IgnoreError
    int XChangeWindowAttributes(@In long display, @In long w, @In NativeLong valuemask, @In XSetWindowAttributes attributes);
    @IgnoreError
    int XChangeProperty(
            @In long display,
            @In long w,
            @In long property,
            @In long type,
            @In int format,
            @In int mode,
            @In Pointer data,
            @In int nelements
    );

}
