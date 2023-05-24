package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;

public interface Gdk extends GObject {

    @IgnoreError
    void
    gdk_threads_enter ();

    @IgnoreError
    void
    gdk_threads_leave ();

}
