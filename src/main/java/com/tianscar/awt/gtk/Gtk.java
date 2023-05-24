package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;

public interface Gtk extends Gio, Gdk, GObject {

    @IgnoreError
    void
    gtk_window_present (@In long window);

    @IgnoreError
    boolean
    gtk_main_iteration();

    @IgnoreError
    boolean
    gtk_events_pending();

}
