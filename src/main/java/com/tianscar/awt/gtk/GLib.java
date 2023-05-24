package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;

public interface GLib {

    @IgnoreError
    void
    g_free (
            @In long mem
    );

    @IgnoreError
    void
    g_slist_free (
            @In GSList list
    );

}
