package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;

@IgnoreError
public interface Gtk2 extends Gtk {

    // Gtk2 INSTANCE = GtkUtils.initGtk2();

    long
    gtk_file_filter_new();

    void
    gtk_file_filter_add_mime_type (
            @In long filter,
            @In CharSequence mime_type
    );

    void
    gtk_file_filter_set_name (
            @In long filter,
            @In CharSequence name
    );

    void
    gtk_file_chooser_set_current_name (
            @In long chooser,
            @In CharSequence name
    );

    boolean
    gtk_file_chooser_set_current_folder (
            @In long chooser,
            @In CharSequence filename
    );

    boolean
    gtk_file_chooser_set_filename (
            @In long chooser,
            @In CharSequence filename
    );

    void
    gtk_file_chooser_set_select_multiple (
            @In long chooser,
            @In boolean select_multiple
    );

    void
    gtk_file_chooser_add_filter (
            @In long chooser,
            @In long filter
    );

    GSList
    gtk_file_chooser_get_filenames (
            @In long chooser
    );

    boolean
    gtk_file_chooser_get_do_overwrite_confirmation (
            @In long chooser
    );

}
