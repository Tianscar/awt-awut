package com.tianscar.awt.gtk;

import com.tianscar.awt.AWTFactory;
import jnr.ffi.Runtime;
import sun.awt.SunToolkit;

import java.awt.*;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;

public class Gtk3NativeFileDialog extends FileDialog {

    private static final long serialVersionUID = 5722891644783979481L;
    private static final Gtk3 gtk3 = Gtk3.INSTANCE;

    public Gtk3NativeFileDialog(Frame parent) {
        this(parent, "", LOAD);
    }

    public Gtk3NativeFileDialog(Frame parent, String title) {
        this(parent, title, LOAD);
    }

    public Gtk3NativeFileDialog(Frame parent, String title, int mode) {
        super(parent, title, mode);
        GtkUtils.checkGtk3NativeFileDialogSupported();
    }

    public Gtk3NativeFileDialog(Dialog parent) {
        this(parent, "", LOAD);
    }

    public Gtk3NativeFileDialog(Dialog parent, String title) {
        this(parent, title, LOAD);
    }

    public Gtk3NativeFileDialog(Dialog parent, String title, int mode) {
        super(parent, title, mode);
        GtkUtils.checkGtk3NativeFileDialogSupported();
    }

    private volatile int mode;
    private volatile String dir;
    private volatile String file;
    private volatile File[] files;
    private volatile boolean multipleMode = false;
    private volatile FilenameFilter filter;
    private volatile long gtkFileChooser = 0;

    private Object lock = new byte[0];
    private Object getObjectLock() {
        try {
            return Class.forName("java.awt.Component").getDeclaredField("objectLock").get(this);
        } catch (IllegalAccessException | ClassNotFoundException | NoSuchFieldException e) {
            return lock;
        }
    }

    public void addNotify() {
        synchronized(getTreeLock()) {
            Container parent = getParent();
            if (parent != null && parent.isDisplayable()) {
                parent.addNotify();
            }
            super.addNotify();
        }
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        switch (mode) {
            case LOAD:
            case SAVE:
                this.mode = mode;
                break;
            default:
                throw new IllegalArgumentException("illegal file dialog mode");
        }
    }

    public void setDirectory(String dir) {
        this.dir = (dir != null && dir.equals("")) ? null : dir;
    }

    public String getDirectory() {
        return dir;
    }

    public void setFile(String file) {
        this.file = (file != null && file.equals("")) ? null : file;
    }

    public String getFile() {
        return file;
    }

    public File[] getFiles() {
        synchronized (getObjectLock()) {
            if (files != null) {
                return files.clone();
            } else {
                return new File[0];
            }
        }
    }

    public void setMultipleMode(boolean enable) {
        synchronized (getObjectLock()) {
            multipleMode = enable;
        }
    }

    public boolean isMultipleMode() {
        synchronized (getObjectLock()) {
            return multipleMode;
        }
    }

    public synchronized void setFilenameFilter(FilenameFilter filter) {
        this.filter = filter;
    }

    public FilenameFilter getFilenameFilter() {
        return filter;
    }

    @Override
    public void setVisible(boolean b) {
        SunToolkit.awtLock();
        try {
            if (b) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (!GtkUtils.initializeGtk() && GtkUtils.isGtkInitialized()) throw new IllegalStateException("cannot init GTK");
                        gtk3.gdk_threads_enter();
                        try {
                            gtkFileChooser = gtk3.gtk_file_chooser_native_new(
                                    getTitle(),
                                    0,
                                    getMode() == SAVE ?
                                            GtkFileChooserAction.GTK_FILE_CHOOSER_ACTION_SAVE :
                                            GtkFileChooserAction.GTK_FILE_CHOOSER_ACTION_OPEN,
                                    null, null);
                            if (getMode() == LOAD) gtk3.gtk_file_chooser_set_select_multiple(gtkFileChooser, isMultipleMode());
                            gtk3.gtk_native_dialog_set_modal(gtkFileChooser, true);
                            gtk3.gtk_file_chooser_get_do_overwrite_confirmation(gtkFileChooser);
                            String dirname = getDirectory();
                            String filename = getFile();
                            if (dirname != null) {
                                File file = new File(dirname);
                                if (file.isDirectory()) {
                                    gtk3.gtk_file_chooser_set_current_folder(gtkFileChooser, dirname);
                                }
                                else {
                                    gtk3.gtk_file_chooser_set_current_folder(gtkFileChooser, file.getParent());
                                    if (getMode() == LOAD) gtk3.gtk_file_chooser_set_filename(gtkFileChooser, file.getAbsolutePath());
                                    else gtk3.gtk_file_chooser_set_current_name(gtkFileChooser, file.getName());
                                }
                            }
                            if (filename != null && !filename.equals("")) {
                                long gtkFileFilter = gtk3.gtk_file_filter_new();
                                String[] mimes = filename.replaceAll(" ", "").split(",");
                                for (String mime : mimes) {
                                    if (mime != null && !mime.equals("")) gtk3.gtk_file_filter_add_mime_type(gtkFileFilter, mime);
                                }
                                if (!GtkUtils.isGtkUsePortal() || !AWTFactory.isKDE()) {
                                    StringBuilder displayText = new StringBuilder();
                                    int maxMimes = 3;
                                    for (int i = 0; i < mimes.length; i ++) {
                                        if (mimes[i] != null && !mimes[i].equals("")) displayText.append(mimes[i]);
                                        maxMimes --;
                                        if (maxMimes < 0) {
                                            displayText = new StringBuilder("All supported files");
                                            break;
                                        }
                                        else if (i < mimes.length - 1) displayText.append(", ");
                                    }
                                    gtk3.gtk_file_filter_set_name(gtkFileFilter, displayText);
                                    gtk3.gtk_file_chooser_add_filter(gtkFileChooser, gtkFileFilter);
                                    for (String mime : mimes) {
                                        long extFilter = gtk3.gtk_file_filter_new();
                                        gtk3.gtk_file_filter_add_mime_type(extFilter, mime);
                                        gtk3.gtk_file_filter_set_name(extFilter, mime);
                                        gtk3.gtk_file_chooser_add_filter(gtkFileChooser, extFilter);
                                    }
                                }
                                else gtk3.gtk_file_chooser_add_filter(gtkFileChooser, gtkFileFilter);
                            }
                            GtkResponseType response = gtk3.gtk_native_dialog_run(gtkFileChooser);
                            while (gtk3.gtk_events_pending()) {
                                gtk3.gtk_main_iteration();
                            }
                            Set<File> fileSet = new HashSet<>();
                            if (response == GtkResponseType.GTK_RESPONSE_ACCEPT) {
                                GSList list = gtk3.gtk_file_chooser_get_filenames(gtkFileChooser);
                                GSList file = list;
                                while (file != null) {
                                    fileSet.add(new File(file.data.get().getString(0)));
                                    gtk3.g_free(file.data.get().address());
                                    if (file.next.get() != null) {
                                        GSList gsList = new GSList(Runtime.getRuntime(gtk3));
                                        gsList.useMemory(file.next.get());
                                        file = gsList;
                                    }
                                    else file = null;
                                }
                                gtk3.g_slist_free(list);
                            }
                            quit(false);
                            synchronized (getObjectLock()) {
                                files = fileSet.toArray(new File[0]);
                            }
                        }
                        finally {
                            gtk3.gdk_threads_leave();
                            Gtk3NativeFileDialog.super.setVisible(false);
                        }
                    }
                };
                if (isModal()) {
                    EventQueue.invokeLater(() -> {
                        Dialog dummy = new Dialog(getOwner());
                        dummy.setUndecorated(true);
                        dummy.setBackground(new Color(0x00000000, true));
                        dummy.setSize(0, 0);
                        dummy.setModalityType(getModalityType());
                        dummy.setModalExclusionType(getModalExclusionType());
                        EventQueue.invokeLater(dummy::dispose);
                        dummy.setVisible(true);
                    });
                    runnable.run();
                }
                else new Thread(runnable).start();
            }
            else {
                quit(true);
                super.setVisible(false);
            }
        } finally {
            SunToolkit.awtUnlock();
        }
    }

    @Override
    public void dispose() {
        quit(true);
        super.dispose();
    }

    private void quit(boolean gtkThread) {
        if (gtkThread) gtk3.gdk_threads_enter();

        try {
            if (gtkFileChooser != 0) {
                gtk3.gtk_native_dialog_destroy(gtkFileChooser);
                gtk3.g_object_unref(gtkFileChooser);
                gtkFileChooser = 0;
            }
        }
        finally {
            if (gtkThread) gtk3.gdk_threads_leave();
        }

    }

    @Override
    public void toFront() {
        if (isVisible()) {
            if (gtkFileChooser != 0) {
                gtk3.gdk_threads_enter();
                try {
                    gtk3.gtk_window_present(gtkFileChooser);
                }
                finally {
                    gtk3.gdk_threads_leave();
                }
            }
            Container parent = getParent();
            if (parent instanceof Dialog) {
                ((Dialog) parent).toFront();
            }
        }
    }

    private void readObject(ObjectInputStream s) throws ClassNotFoundException, IOException {
        s.defaultReadObject();

        // 1.1 Compatibility: "" is not converted to null in 1.1
        if (dir != null && dir.equals("")) {
            dir = null;
        }
        if (file != null && file.equals("")) {
            file = null;
        }
    }

    protected String paramString() {
        String str = super.paramString();
        str += ", dir= " + dir;
        str += ", file= " + file;
        return str + ((mode == LOAD) ? ", load" : ", save");
    }

}
