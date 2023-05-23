package com.tianscar.awt;

import com.tianscar.awt.X11.X11Utils;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Map;

public class AWTUtils {

    public static Map<String, Object> getDesktopProperties() {
        try {
            Field field = Toolkit.class.getDeclaredField("desktopProperties");
            field.setAccessible(true);
            return (Map<String, Object>) field.get(Toolkit.getDefaultToolkit());
        } catch (NoSuchFieldException | IllegalAccessException |ClassCastException e) {
            return null;
        }
    }

    private static final boolean isWindows, isMac, isLinux, isSolaris, isAix;
    private static final boolean isUnix;
    private static final boolean isJava9;
    private static final boolean isKDE = X11Utils.isX11() && System.getenv("KDE_FULL_SESSION") != null;
    static {
        final String os = System.getProperty("os.name").trim().toLowerCase();
        isWindows = os.contains("win");
        isMac = os.contains("mac") || os.contains("osx");
        isLinux = os.contains("nux");
        isSolaris = os.contains("sunos") || os.contains("solaris");
        isAix = os.contains("aix");
        isUnix = !isWindows;
        boolean hasModule;
        try {
            Class.forName("java.lang.Module");
            hasModule = true;
        }
        catch (ClassNotFoundException e) {
            hasModule = false;
        }
        isJava9 = hasModule;
    }

    public static boolean isJava9() {
        return isJava9;
    }

    public static boolean isWindows() {
        return isWindows;
    }

    public static boolean isMac() {
        return isMac;
    }

    public static boolean isLinux() {
        return isLinux;
    }

    public static boolean isAix() {
        return isAix;
    }

    public static boolean isSolaris() {
        return isSolaris;
    }

    public static boolean isUnix() {
        return isUnix;
    }

    public static boolean isX11() {
        return X11Utils.isX11();
    }

    public static boolean isKDE() {
        return isKDE;
    }

    public static void invokeLater(Runnable runnable) {
        if (EventQueue.isDispatchThread()) runnable.run();
        else EventQueue.invokeLater(runnable);
    }

}
