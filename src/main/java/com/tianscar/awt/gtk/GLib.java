package com.tianscar.awt.gtk;

import jnr.ffi.annotations.In;

public interface GLib {

    void
    g_free (
            @In long mem
    );

    void
    g_slist_free (
            @In GSList list
    );

}
