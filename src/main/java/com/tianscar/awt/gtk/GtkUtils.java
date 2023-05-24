package com.tianscar.awt.gtk;

import com.tianscar.awt.X11.X11Utils;
import jnr.ffi.*;

import java.awt.*;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;

public class GtkUtils {

    private static final boolean isGTKUsePortal;
    static {
        boolean tmp;
        try {
            int usePortalInteger = Integer.parseInt(System.getenv("GTK_USE_PORTAL"));
            tmp = usePortalInteger != 0;
        }
        catch (Exception e) {
            tmp = false;
        }
        isGTKUsePortal = tmp;
    }

    public static boolean isGtkUsePortal() {
        return isGTKUsePortal;
    }

    static Gio initGio() {
        Gio gtk3 = initGtk3();
        if (gtk3 != null) return gtk3;
        Gio gtk2 = initGtk2();
        if (gtk2 != null) return gtk2;
        try {
            return X11Utils.isX11() ? LibraryLoader.create(Gio.class).load("gio-2.0") : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    static Gtk3 initGtk3() {
        try {
            return X11Utils.isX11() ? LibraryLoader.create(Gtk3.class).load("gtk-3") : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    static Gtk2 initGtk2() {
        Gtk2 gtk3 = initGtk3();
        if (gtk3 != null) return gtk3;
        try {
            return X11Utils.isX11() ? LibraryLoader.create(Gtk2.class).load("gtk-x11-2.0") : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean isGtkInitialized() {
        try {
            return (boolean) Class.forName("sun.awt.UNIXToolkit").getDeclaredField("nativeGTKLoaded").get(Toolkit.getDefaultToolkit());
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | ClassCastException ignored) {
            return false;
        }
    }

    public static boolean initializeGtk() {
        try {
            return (boolean) Class.forName("sun.awt.UNIXToolkit").getDeclaredMethod("loadGTK").invoke(Toolkit.getDefaultToolkit());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | ClassCastException ignored) {
            return false;
        }
    }

    public static int getGtkMajorVersion() {
        if (!initializeGtk()) return 0;
        try {
            Enum<?> versionInfo = (Enum<?>) Class.forName("sun.awt.UNIXToolkit")
                    .getDeclaredMethod("getGtkVersion")
                    .invoke(null);
            return (int) (versionInfo.getClass().getDeclaredMethod("getNumber").invoke(versionInfo));
        }
        catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassCastException e) {
            return 0;
        }
    }

    public static boolean checkGtkVersion(int major, int minor, int patch) {
        if (!initializeGtk()) return false;
        if (getGtkMajorVersion() > major) return true;
        try {
            return (boolean) Class.forName("sun.awt.UNIXToolkit")
                    .getDeclaredMethod("checkGtkVersion", int.class, int.class, int.class)
                    .invoke(Toolkit.getDefaultToolkit(), major, minor, patch);
        }
        catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassCastException e) {
            return false;
        }
    }

    public static boolean isGtk3NativeFileDialogSupported() {
        Gtk3 gtk3 = Gtk3.INSTANCE;
        if (gtk3 == null) return false;
        else return checkGtkVersion(3, 2, 0);
    }

    public static void checkGtk3NativeFileDialogSupported() {
        Gtk3 gtk3 = Gtk3.INSTANCE;
        if (gtk3 == null) throw new IllegalStateException("could not load GTK3 library");
        else if (!checkGtkVersion(3, 2, 0)) throw new IllegalStateException("GTK version 3.2.0 required");
    }

    public static boolean gtk2ShowURI(URI uri) {
        checkGtk2ShowURISupported();
        Gtk2 gtk2 = Gtk2.INSTANCE;
        if (gtk2 == null) return false;
        gtk2.gdk_threads_enter();
        try {
            return gtk2.gtk_show_uri(0, uri.toString(), 0, null);
        }
        finally {
            gtk2.gdk_threads_leave();
        }
    }

    public static boolean isGtk2ShowURISupported() {
        Gtk2 gtk2 = Gtk2.INSTANCE;
        if (gtk2 == null) return false;
        return checkGtkVersion(2, 1, 4);
    }

    public static void checkGtk2ShowURISupported() {
        Gtk2 gtk2 = Gtk2.INSTANCE;
        if (gtk2 == null) throw new IllegalStateException("could not load GTK2 library");
        else if (!checkGtkVersion(2, 1, 4)) throw new IllegalStateException("GTK version 2.1.4 required");
    }

    public static boolean gioMoveToTrash(File file) {
        if (file == null || !file.exists()) return false;
        Gio gio = Gio.INSTANCE;
        if (gio == null) return false;
        long gFile = gio.g_file_new_for_path(file.getAbsolutePath());
        try {
            return gio.g_file_trash(gFile, 0, null);
        }
        finally {
            gio.g_object_unref(gFile);
        }
    }

    public static boolean isGioMoveToTrashSupported() {
        return Gio.INSTANCE != null;
    }

}
