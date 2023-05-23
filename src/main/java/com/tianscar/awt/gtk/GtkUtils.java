package com.tianscar.awt.gtk;

import com.tianscar.awt.X11.X11Utils;
import jnr.ffi.LibraryLoader;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class GtkUtils {

    private static final boolean isKDE = X11Utils.isX11() && System.getenv("KDE_FULL_SESSION") != null;
    public static boolean isKDE() {
        return isKDE;
    }
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

}
