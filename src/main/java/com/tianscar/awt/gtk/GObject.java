package com.tianscar.awt.gtk;

import jnr.ffi.annotations.In;

public interface GObject extends GLib {

    void
    g_object_unref (
            @In long object
    );

}
