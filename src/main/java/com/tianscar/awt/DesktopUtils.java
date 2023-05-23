package com.tianscar.awt;

import com.tianscar.awt.gtk.GtkUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.net.URI;
import java.util.function.Consumer;

/**
 * Adapted from:<br/>
 * <a href="https://github.com/JFormDesigner/FlatLaf/blob/main/flatlaf-extras/src/main/java/com/formdev/flatlaf/extras/FlatDesktop.java">FlatLaf/FlatDesktop.java</a>
 * <a href="https://github.com/dorkbox/Desktop/blob/master/src/dorkbox/desktop/Desktop.java">Dorkbox/Desktop/Desktop.java</a>
 */
public class DesktopUtils {

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
        if (!AWTUtils.isWindows() && !AWTUtils.isMac()) {
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

    public static boolean open(File file) {
        if (file == null || !file.exists()) return false;
        if (xdgOpenBin.canExecute()) {
            try {
                Process process = new ProcessBuilder("xdg-open", file.getAbsolutePath()).start();
                asyncSkipAllBytes(process.getInputStream());
                return true;
            }
            catch (IOException e) {
                if (GtkUtils.isGtkAvailable()) return GtkUtils.showURI(file.toURI());
                else {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        else if (AWTUtils.isMac()) {
            try {
                Process process = new ProcessBuilder("open", file.getAbsolutePath()).start();
                asyncSkipAllBytes(process.getInputStream());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.OPEN)) {
                AWTUtils.invokeLater(() -> {
                    try {
                        desktop.open(file);
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

    public static boolean browse(URI uri) {
        if (uri == null) return false;
        if (xdgOpenBin.canExecute()) {
            try {
                Process process = new ProcessBuilder("xdg-open", uri.toString()).start();
                asyncSkipAllBytes(process.getInputStream());
                return true;
            } catch (IOException e) {
                if (GtkUtils.isGtkAvailable()) return GtkUtils.showURI(uri);
                else {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        else if (AWTUtils.isMac()) {
            try {
                Process process = new ProcessBuilder("open", uri.toString()).start();
                asyncSkipAllBytes(process.getInputStream());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.BROWSE)) {
                AWTUtils.invokeLater(() -> {
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
        if (xdgOpenBin.canExecute()) {
            try {
                Process process = new ProcessBuilder("xdg-open", mailtoURI.toString()).start();
                asyncSkipAllBytes(process.getInputStream());
                return true;
            } catch (IOException e) {
                if (GtkUtils.isGtkAvailable()) return GtkUtils.showURI(mailtoURI);
                else {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        else if (AWTUtils.isMac()) {
            try {
                Process process = new ProcessBuilder("open", mailtoURI.toString()).start();
                asyncSkipAllBytes(process.getInputStream());
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        else if (java.awt.Desktop.isDesktopSupported()) {
            java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
            if (desktop.isSupported(java.awt.Desktop.Action.MAIL)) {
                AWTUtils.invokeLater(() -> {
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

    /**
     * Checks whether the given action is supported on the current platform.
     */
    private static boolean isSupported(String action) {
        if (AWTUtils.isJava9()) {
            try {
                return java.awt.Desktop.getDesktop().isSupported(Enum.valueOf(java.awt.Desktop.Action.class, action));
            }
            catch (Exception e) {
                return false;
            }
        }
        else return AWTUtils.isMac();
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
        if (!isSupported("APP_ABOUT")) return false;

        String handlerClassName;
        if (AWTUtils.isJava9()) handlerClassName = "java.awt.desktop.AboutHandler";
        else if(AWTUtils.isMac()) handlerClassName = "com.apple.eawt.AboutHandler";
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
        if (!isSupported("APP_PREFERENCES")) return false;

        String handlerClassName;
        if (AWTUtils.isJava9()) handlerClassName = "java.awt.desktop.PreferencesHandler";
        else if(AWTUtils.isMac()) handlerClassName = "com.apple.eawt.PreferencesHandler";
        else return false;

        return setHandler( "setPreferencesHandler", handlerClassName, preferencesHandler );
    }

    private static boolean setHandler(String setHandlerMethodName, String handlerClassName, Runnable handler) {
        Object desktopOrApplication = getDesktopOrApplication();
        if (desktopOrApplication == null) return false;
        try {
            Class<?> handlerClass = Class.forName( handlerClassName );

            desktopOrApplication.getClass().getMethod(setHandlerMethodName, handlerClass)
                    .invoke(desktopOrApplication, Proxy.newProxyInstance(DesktopUtils.class.getClassLoader(),
                            new Class[] { handlerClass },
                            (proxy, method, args) -> {
                                // Use invokeLater to release the listener firing for the case
                                // that the action listener shows a modal dialog.
                                // This (hopefully) prevents application hunging.
                                AWTUtils.invokeLater(handler);
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
        if (!isSupported("APP_QUIT_HANDLER")) return false;

        String handlerClassName;
        if (AWTUtils.isJava9()) handlerClassName = "java.awt.desktop.QuitHandler";
        else if(AWTUtils.isMac()) handlerClassName = "com.apple.eawt.QuitHandler";
        else return false;

        Object desktopOrApplication = getDesktopOrApplication();
        if (desktopOrApplication == null) return false;
        try {
            Class<?> handlerClass = Class.forName(handlerClassName);

            desktopOrApplication.getClass().getMethod("setQuitHandler", handlerClass)
                    .invoke(desktopOrApplication, Proxy.newProxyInstance(DesktopUtils.class.getClassLoader(),
                            new Class[] { handlerClass },
                            (proxy, method, args) -> {
                                Object response = args[1];
                                String responseClass = AWTUtils.isJava9() ? "java.awt.desktop.QuitResponse" : "com.apple.eawt.QuitResponse";
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
        if (AWTUtils.isJava9()) {
            if (java.awt.Desktop.isDesktopSupported()) return java.awt.Desktop.getDesktop();
            else return null;
        }
        else if (AWTUtils.isMac()) {
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

}
