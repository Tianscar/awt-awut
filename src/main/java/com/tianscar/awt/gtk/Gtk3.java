package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;

public interface Gtk3 extends Gtk2 {

    Gtk3 INSTANCE = GtkUtils.initGtk3();

    @IgnoreError
    long
    gtk_file_chooser_native_new (
            @In CharSequence title,
            @In long parent,
            @In GtkFileChooserAction action,
            @In CharSequence accept_label,
            @In CharSequence cancel_label
    );

    @IgnoreError
    GtkResponseType
    gtk_native_dialog_run (
            @In long dialog
    );

    @IgnoreError
    void
    gtk_native_dialog_set_modal (
            @In long dialog,
            @In boolean modal
    );

    @IgnoreError
    void
    gtk_native_dialog_destroy (
            @In long dialog
    );

}
