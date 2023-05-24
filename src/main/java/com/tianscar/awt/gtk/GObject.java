package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;

public interface GObject extends GLib {

    @IgnoreError
    void
    g_object_unref (
            @In long object
    );

}
