package com.tianscar.awt.gtk;

import jnr.ffi.annotations.In;

public interface Gtk extends Gdk, GObject {

    void
    gtk_window_present (@In long window);

    boolean
    gtk_main_iteration();

    boolean
    gtk_events_pending();

}
