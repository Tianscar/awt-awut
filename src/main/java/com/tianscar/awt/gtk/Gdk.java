package com.tianscar.awt.gtk;

public interface Gdk extends GObject {

    void
    gdk_threads_enter ();

    void
    gdk_threads_leave ();

}
