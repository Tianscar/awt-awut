package com.tianscar.awt.gtk;

import com.tianscar.awt.X11.X11Utils;
import jnr.ffi.*;
import jnr.ffi.Runtime;
import jnr.ffi.byref.PointerByReference;

import java.awt.*;
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

    public static boolean isGtkLoaded() {
        try {
            return (boolean) Class.forName("sun.awt.UNIXToolkit").getDeclaredField("nativeGTKLoaded").get(Toolkit.getDefaultToolkit());
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | ClassCastException ignored) {
            return false;
        }
    }

    public static boolean isGtkAvailable() {
        try {
            return (boolean) Class.forName("sun.awt.UNIXToolkit").getDeclaredField("nativeGTKAvailable").get(Toolkit.getDefaultToolkit());
        } catch (IllegalAccessException | NoSuchFieldException | ClassNotFoundException | ClassCastException ignored) {
            return false;
        }
    }

    public static boolean loadGtk() {
        try {
            return (boolean) Class.forName("sun.awt.UNIXToolkit").getDeclaredMethod("loadGTK").invoke(Toolkit.getDefaultToolkit());
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException | ClassCastException ignored) {
            return false;
        }
    }

    public static int getGtkMajorVersion() {
        if (!loadGtk()) return 0;
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
        if (!loadGtk()) return false;
        try {
            return (boolean) Class.forName("sun.awt.UNIXToolkit")
                    .getDeclaredMethod("checkGtkVersion", int.class, int.class, int.class)
                    .invoke(Toolkit.getDefaultToolkit(), major, minor, patch);
        }
        catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException | ClassCastException e) {
            return false;
        }
    }

    public static boolean showURI(URI uri) {
        if (!loadGtk() || checkGtkVersion(2, 1, 4)) return false;
        Gtk2 gtk2 = Gtk2.INSTANCE;
        if (gtk2 == null) return false;
        gtk2.gdk_threads_enter();
        try {
            PointerByReference errorRef = new PointerByReference();
            gtk2.gtk_show_uri(0, uri.toString(), 0, errorRef);
            Pointer errPtr = errorRef.getValue();
            if (errPtr == null || errPtr.address() == 0) return true;
            else {
                GError gError = new GError(Runtime.getRuntime(gtk2));
                gError.useMemory(errPtr);
                System.err.println(gError.message.get().getString(0));
                gtk2.g_free(errPtr.address());
                return false;
            }
        }
        finally {
            gtk2.gdk_threads_leave();
        }
    }

}
