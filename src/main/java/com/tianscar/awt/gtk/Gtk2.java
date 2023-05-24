package com.tianscar.awt.gtk;

import jnr.ffi.annotations.IgnoreError;
import jnr.ffi.annotations.In;
import jnr.ffi.byref.PointerByReference;

public interface Gtk2 extends Gtk {

    Gtk2 INSTANCE = GtkUtils.initGtk2();

    @IgnoreError
    long
    gtk_file_filter_new();

    @IgnoreError
    void
    gtk_file_filter_add_mime_type (
            @In long filter,
            @In CharSequence mime_type
    );

    @IgnoreError
    void
    gtk_file_filter_set_name (
            @In long filter,
            @In CharSequence name
    );

    @IgnoreError
    void
    gtk_file_chooser_set_current_name (
            @In long chooser,
            @In CharSequence name
    );

    @IgnoreError
    boolean
    gtk_file_chooser_set_current_folder (
            @In long chooser,
            @In CharSequence filename
    );

    @IgnoreError
    boolean
    gtk_file_chooser_set_filename (
            @In long chooser,
            @In CharSequence filename
    );

    @IgnoreError
    void
    gtk_file_chooser_set_select_multiple (
            @In long chooser,
            @In boolean select_multiple
    );

    @IgnoreError
    void
    gtk_file_chooser_add_filter (
            @In long chooser,
            @In long filter
    );

    @IgnoreError
    GSList
    gtk_file_chooser_get_filenames (
            @In long chooser
    );

    @IgnoreError
    boolean
    gtk_file_chooser_get_do_overwrite_confirmation (
            @In long chooser
    );

    @IgnoreError
    boolean
    gtk_show_uri(
            @In long screen,
            @In CharSequence uri,
            @In long timestamp,
            PointerByReference error
    );

}
