package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;
import jnr.ffi.byref.PointerByReference;

public interface Gio extends GLib, GObject {

    Gio INSTANCE = GtkUtils.initGio();

    @IgnoreError
    boolean
    g_file_trash (
            @In long file,
            @In long cancellable,
            @In PointerByReference error
    );

    @IgnoreError
    long
    g_file_new_for_path (
            @In CharSequence path
    );

}
