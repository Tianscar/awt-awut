package com.tianscar.awt;

import com.tianscar.awt.X11.X11DragSource;
import com.tianscar.awt.X11.X11Utils;
import com.tianscar.awt.gtk.Gtk3NativeFileDialog;
import com.tianscar.awt.gtk.GtkUtils;
import com.tianscar.awt.windows.Win32Utils;

import java.awt.*;
import java.awt.dnd.DragSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.Map;
import java.util.function.Consumer;

public class AWTFactory {

    static {
        if (isX11()) X11Utils.fixDragAndDropCursors();
    }

    // Generic utilities
    // ----------------------------

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

    public static void invokeAndWait(Runnable runnable) throws InterruptedException, InvocationTargetException {
        if (EventQueue.isDispatchThread()) runnable.run();
        else EventQueue.invokeAndWait(runnable);
    }

    // ----------------------------

    // Native widgets utilities

    public static FileDialog createFileDialog(Frame parent, String title, int mode) {
        if (GtkUtils.isGtk3NativeFileDialogSupported()) return new Gtk3NativeFileDialog(parent, title, mode);
        else return new FileDialog(parent, title, mode);
    }

    public static FileDialog createFileDialog(Frame parent, String title) {
        if (GtkUtils.isGtk3NativeFileDialogSupported()) return new Gtk3NativeFileDialog(parent, title);
        else return new FileDialog(parent, title);
    }

    public static FileDialog createFileDialog(Frame parent) {
        if (GtkUtils.isGtk3NativeFileDialogSupported()) return new Gtk3NativeFileDialog(parent);
        else return new FileDialog(parent);
    }

    public static FileDialog createFileDialog(Dialog parent, String title, int mode) {
        if (GtkUtils.isGtk3NativeFileDialogSupported()) return new Gtk3NativeFileDialog(parent, title, mode);
        else return new FileDialog(parent, title, mode);
    }

    public static FileDialog createFileDialog(Dialog parent, String title) {
        if (GtkUtils.isGtk3NativeFileDialogSupported()) return new Gtk3NativeFileDialog(parent, title);
        else return new FileDialog(parent, title);
    }

    public static FileDialog createFileDialog(Dialog parent) {
        if (GtkUtils.isGtk3NativeFileDialogSupported()) return new Gtk3NativeFileDialog(parent);
        else return new FileDialog(parent);
    }

    // ----------------------------

    // Drag and drop utilities

    public static DragSource createDragSource() {
        if (X11Utils.isX11DragSourceSupported()) return new X11DragSource();
        else return new DragSource();
    }

    // ----------------------------

    // Other native-related utilities

    // ----------------------------

    // Desktop utilities
    // Adopted from:
    // https://github.com/JFormDesigner/FlatLaf/blob/main/flatlaf-extras/src/main/java/com/formdev/flatlaf/extras/FlatDesktop.java
    // https://github.com/dorkbox/Desktop/blob/master/src/dorkbox/desktop/Desktop.java
    // ----------------------------

    private static void asyncSkipAllBytes(InputStream in) {
        new Thread(() -> {
            try {
                byte[] buffer = new byte[1024];
                int n = 0;
                while (n != -1) n = in.read(buffer);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private static final File xdgOpenBin;
    static {
        File xdgOpen = null;
        if (!isWindows() && !isMac()) {
            String path = System.getenv("PATH");
            if (path != null) {
                String[] paths = path.split(":");
                for (String p : paths) {
                    File file;
                    if ((file = new File(p, "xdg-open")).canExecute()) {
                        xdgOpen = file;
                        break;
                    }
                }
            }
        }
        xdgOpenBin = xdgOpen;
    }

    private static boolean xdgOrGtkOpenURI(URI uri) {
        if (xdgOpenBin.canExecute()) {
            try {
                Process process = new ProcessBuilder("xdg-open", uri.toString()).start();
                asyncSkipAllBytes(process.getInputStream());
                return true;
            }
            catch (IOException e) {
                if (GtkUtils.isGtk2ShowURISupported()) return GtkUtils.gtk2ShowURI(uri);
                else {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        else return false;
    }

    public static boolean open(File file) {
        if (file == null || !file.exists()) return false;
        else if (!isWindows() && !isMac()) return xdgOrGtkOpenURI(file.toURI());
        else if (isDesktopActionSupported("OPEN")) {
            invokeLater(() -> {
                try {
                    java.awt.Desktop.getDesktop().open(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        else return false;
    }

    public static boolean edit(File file) {
        if (file == null || !file.exists()) return false;
        else if (!isWindows() && !isMac()) return xdgOrGtkOpenURI(file.toURI());
        else if (isDesktopActionSupported("EDIT")) {
            invokeLater(() -> {
                try {
                    java.awt.Desktop.getDesktop().edit(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        else return false;
    }

    public static boolean print(File file) {
        if (file == null || !file.exists()) return false;
        else if (!isWindows() && !isMac()) return xdgOrGtkOpenURI(file.toURI());
        else if (isDesktopActionSupported("PRINT")) {
            invokeLater(() -> {
                try {
                    java.awt.Desktop.getDesktop().print(file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        else return false;
    }

    public static boolean browseFileDirectory(File file) {
        if (file == null || !file.exists()) return false;
        else if (isMac()) {
            try {
                return (boolean) Class.forName("com.apple.eio.FileManager")
                        .getDeclaredMethod("revealInFinder", File.class).invoke(null, file);
            }
            catch (Exception e) {
                return false;
            }
        }
        else if (!isWindows()) return xdgOrGtkOpenURI(file.getParentFile().toURI());
        else if (isDesktopActionSupported("BROWSE_FILE_DIR")) {
            invokeLater(() -> {
                try {
                    java.awt.Desktop.class.getDeclaredMethod("browseFileDirectory", File.class)
                            .invoke(java.awt.Desktop.getDesktop(), file);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        else if (isDesktopActionSupported("OPEN")) {
            invokeLater(() -> {
                try {
                    java.awt.Desktop.getDesktop().open(file.getParentFile());
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        else return false;
    }

    public static boolean moveToTrash(File file) {
        if (file == null || !file.exists()) return false;
        else if (isDesktopActionSupported("MOVE_TO_TRASH")) {
            invokeLater(() -> {
                try {
                    java.awt.Desktop.class.getMethod("moveToTrash", File.class)
                            .invoke(java.awt.Desktop.getDesktop(), file);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            });
            return true;
        }
        else if (isMac()) {
            try {
                return (boolean) Class.forName("com.apple.eio.FileManager")
                        .getDeclaredMethod("moveToTrash", File.class).invoke(null, file);
            }
            catch (Exception e) {
                return false;
            }
        }
        else if (Win32Utils.isShellApiMoveToTrashSupported()) return Win32Utils.shellApiMoveToTrash(file);
        else if (GtkUtils.isGioMoveToTrashSupported()) return GtkUtils.gioMoveToTrash(file);
        else return false;
    }

    public static boolean browse(URI uri) {
        if (uri == null) return false;
        else if (!isWindows() && !isMac()) return xdgOrGtkOpenURI(uri);
        else if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                invokeLater(() -> {
                    try {
                        desktop.browse(uri);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return true;
            }
            else return false;
        }
        else return false;
    }

    public static boolean mail(URI mailtoURI) {
        if (mailtoURI == null) return false;
        else if (!isWindows() && !isMac()) return xdgOrGtkOpenURI(mailtoURI);
        else if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.MAIL)) {
                invokeLater(() -> {
                    try {
                        desktop.mail(mailtoURI);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                return true;
            }
            else return false;
        }
        else return false;
    }

    public static boolean enableSuddenTermination() {
        if (!isDesktopActionSupported("APP_SUDDEN_TERMINATION")) return false;

        Object desktopOrApplication = getDesktopOrApplication();
        if (desktopOrApplication == null) return false;
        try {
            desktopOrApplication.getClass().getMethod("enableSuddenTermination").invoke(desktopOrApplication);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean disableSuddenTermination() {
        if (!isDesktopActionSupported("APP_SUDDEN_TERMINATION")) return false;

        Object desktopOrApplication = getDesktopOrApplication();
        if (desktopOrApplication == null) return false;
        try {
            desktopOrApplication.getClass().getMethod("disableSuddenTermination").invoke(desktopOrApplication);
            return true;
        } catch (InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
    }

    /**
     * Checks whether the given action is supported on the current platform.
     */
    private static boolean isDesktopActionSupported(String action) {
        if (isJava9()) {
            try {
                return java.awt.Desktop.isDesktopSupported() &&
                        java.awt.Desktop.getDesktop().isSupported(Enum.valueOf(java.awt.Desktop.Action.class, action));
            }
            catch (Exception e) {
                return false;
            }
        }
        else return isMac();
    }

    /**
     * Sets a handler to show a custom About dialog.
     * <p>
     * Useful for macOS to enable menu item "MyApp &gt; About".
     * <p>
     * Uses:
     * <ul>
     * <li>Java 8 on macOS: com.apple.eawt.Application.getApplication().setAboutHandler(com.apple.eawt.AboutHandler)
     * <li>Java 9+: java.awt.Desktop.getDesktop().setAboutHandler(java.awt.desktop.AboutHandler)
     * </ul>
     */
    public static boolean setAboutHandler(Runnable aboutHandler) {
        if (!isDesktopActionSupported("APP_ABOUT")) return false;

        String handlerClassName;
        if (isJava9()) handlerClassName = "java.awt.desktop.AboutHandler";
        else if(isMac()) handlerClassName = "com.apple.eawt.AboutHandler";
        else return false;

        return setHandler("setAboutHandler", handlerClassName, aboutHandler);
    }

    /**
     * Sets a handler to show a custom Preferences dialog.
     * <p>
     * Useful for macOS to enable menu item "MyApp &gt; Preferences".
     * <p>
     * Uses:
     * <ul>
     * <li>Java 8 on macOS: com.apple.eawt.Application.getApplication().setPreferencesHandler(com.apple.eawt.PreferencesHandler)
     * <li>Java 9+: java.awt.Desktop.getDesktop().setPreferencesHandler(java.awt.desktop.PreferencesHandler)
     * </ul>
     */
    public static boolean setPreferencesHandler(Runnable preferencesHandler) {
        if (!isDesktopActionSupported("APP_PREFERENCES")) return false;

        String handlerClassName;
        if (isJava9()) handlerClassName = "java.awt.desktop.PreferencesHandler";
        else if(isMac()) handlerClassName = "com.apple.eawt.PreferencesHandler";
        else return false;

        return setHandler( "setPreferencesHandler", handlerClassName, preferencesHandler );
    }

    private static boolean setHandler(String setHandlerMethodName, String handlerClassName, Runnable handler) {
        Object desktopOrApplication = getDesktopOrApplication();
        if (desktopOrApplication == null) return false;
        try {
            Class<?> handlerClass = Class.forName( handlerClassName );

            desktopOrApplication.getClass().getMethod(setHandlerMethodName, handlerClass)
                    .invoke(desktopOrApplication, Proxy.newProxyInstance(AWTFactory.class.getClassLoader(),
                            new Class[] { handlerClass },
                            (proxy, method, args) -> {
                                // Use invokeLater to release the listener firing for the case
                                // that the action listener shows a modal dialog.
                                // This (hopefully) prevents application hunging.
                                invokeLater(handler);
                                return null;
                            }));
        }
        catch (ClassNotFoundException | InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            return false;
        }
        return true;
    }

    /**
     * Sets a handler which is invoked when the application should quit.
     * The handler must invoke either {@link QuitResponse#performQuit} or
     * {@link QuitResponse#cancelQuit}.
     * <p>
     * Useful for macOS to get notified when user clicks menu item "MyApp &gt; Quit".
     * <p>
     * Uses:
     * <ul>
     * <li>Java 8 on macOS: com.apple.eawt.Application.getApplication().setQuitHandler(com.apple.eawt.QuitHandler)
     * <li>Java 9+: java.awt.Desktop.getDesktop().setQuitHandler(java.awt.desktop.QuitHandler)
     * </ul>
     */
    public static boolean setQuitHandler(Consumer<QuitResponse> quitHandler) {
        if (!isDesktopActionSupported("APP_QUIT_HANDLER")) return false;

        String handlerClassName;
        if (isJava9()) handlerClassName = "java.awt.desktop.QuitHandler";
        else if(isMac()) handlerClassName = "com.apple.eawt.QuitHandler";
        else return false;

        Object desktopOrApplication = getDesktopOrApplication();
        if (desktopOrApplication == null) return false;
        try {
            Class<?> handlerClass = Class.forName(handlerClassName);

            desktopOrApplication.getClass().getMethod("setQuitHandler", handlerClass)
                    .invoke(desktopOrApplication, Proxy.newProxyInstance(AWTFactory.class.getClassLoader(),
                            new Class[] { handlerClass },
                            (proxy, method, args) -> {
                                Object response = args[1];
                                String responseClass = isJava9() ? "java.awt.desktop.QuitResponse" : "com.apple.eawt.QuitResponse";
                                quitHandler.accept( new QuitResponse() {
                                    @Override
                                    public void performQuit() {
                                        try {
                                            Class.forName( responseClass ).getMethod("performQuit").invoke(response);
                                        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                                                 NoSuchMethodException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                    @Override
                                    public void cancelQuit() {
                                        try {
                                            Class.forName( responseClass ).getMethod( "cancelQuit" ).invoke( response );
                                        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                                                 NoSuchMethodException e) {
                                            e.printStackTrace();
                                        }
                                    }

                                });
                                return null;
                            }));
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
            return false;
        }
        return true;
    }

    private static Object getDesktopOrApplication() {
        if (isJava9()) {
            if (java.awt.Desktop.isDesktopSupported()) return java.awt.Desktop.getDesktop();
            else return null;
        }
        else if (isMac()) {
            try {
                return Class.forName("com.apple.eawt.Application").getMethod("getApplication").invoke(null);
            } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                return null;
            }
        }
        else return null;
    }

    public interface QuitResponse {
        void performQuit();
        void cancelQuit();
    }

    // ---------------------------

}
