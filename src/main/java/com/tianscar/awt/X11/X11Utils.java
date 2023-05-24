package com.tianscar.awt.X11;

import com.tianscar.awt.AWTFactory;
import jnr.ffi.Runtime;
import jnr.ffi.*;
import jnr.ffi.byref.IntByReference;
import jnr.ffi.byref.NativeLongByReference;
import jnr.ffi.byref.PointerByReference;
import sun.awt.AWTAccessor;
import sun.awt.SunToolkit;

import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class X11Utils {

    static Xcursor initXcursor() {
        if (GraphicsEnvironment.isHeadless()) return null;
        try {
            return X11Utils.isX11() ? LibraryLoader.create(Xcursor.class).load("Xcursor") : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    static Xlib initXlib() {
        if (GraphicsEnvironment.isHeadless()) return null;
        try {
            return X11Utils.isX11() ? LibraryLoader.create(Xlib.class).load("X11") : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    static Xfixes initXfixes() {
        if (GraphicsEnvironment.isHeadless()) return null;
        try {
            return X11Utils.isX11() ? LibraryLoader.create(Xfixes.class).load("Xfixes") : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static final int _NET_WM_MOVERESIZE_SIZE_TOPLEFT     =  0;
    public static final int _NET_WM_MOVERESIZE_SIZE_TOP         =  1;
    public static final int _NET_WM_MOVERESIZE_SIZE_TOPRIGHT    =  2;
    public static final int _NET_WM_MOVERESIZE_SIZE_RIGHT       =  3;
    public static final int _NET_WM_MOVERESIZE_SIZE_BOTTOMRIGHT =  4;
    public static final int _NET_WM_MOVERESIZE_SIZE_BOTTOM      =  5;
    public static final int _NET_WM_MOVERESIZE_SIZE_BOTTOMLEFT  =  6;
    public static final int _NET_WM_MOVERESIZE_SIZE_LEFT        =  7;
    public static final int _NET_WM_MOVERESIZE_MOVE             =  8;
    public static final int _NET_WM_MOVERESIZE_SIZE_KEYBOARD    =  9;
    public static final int _NET_WM_MOVERESIZE_MOVE_KEYBOARD    = 10;
    public static final int _NET_WM_MOVERESIZE_CANCEL           = 11;

    public static boolean isX11() {
        return Toolkit.getDefaultToolkit().getClass().getName().endsWith(".XToolkit");
    }

    public static void checkX11() {
        if (!isX11()) throw new IllegalStateException("Not X11 environment");
    }

    public static long getDisplay() {
        checkX11();
        try {
            return (long) Class.forName("sun.awt.X11.XToolkit").getMethod("getDisplay").invoke(null);
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException | ClassNotFoundException e) {
            return 0;
        }
    }

    public static long getXWindow(Component component) {
        checkX11();
        if (component == null) return 0;
        try {
            Object peer = AWTAccessor.getComponentAccessor().getPeer(component);
            return (long) peer.getClass().getMethod("getWindow").invoke(peer);
        }
        catch (Exception ignored) {
            return 0;
        }
    }

    public static boolean isXWMHintSupported(long display, long w, long atom) {
        checkX11();
        Xlib xlib = Xlib.INSTANCE;

        if (xlib == null) return false;
        SunToolkit.awtLock();
        try {
            NativeLongByReference typeRef = new NativeLongByReference();
            IntByReference formatRef = new IntByReference();
            NativeLongByReference numAtomsRef = new NativeLongByReference();
            NativeLongByReference bytesAfterRef = new NativeLongByReference();
            PointerByReference propsRef = new PointerByReference();

            xlib.XGetWindowProperty(
                    display,
                    w,
                    xlib.XInternAtom(display, "_NET_SUPPORTED", false),
                    NativeLong.valueOf(0), NativeLong.valueOf(0xFFFF), false,
                    Xatom.XA_ATOM,
                    typeRef, formatRef, numAtomsRef, bytesAfterRef, propsRef);

            Pointer props = propsRef.getValue();
            if (props == null) return false;

            NativeLong type = typeRef.getValue();
            if (type.longValue() != Xatom.XA_ATOM) {
                xlib.XFree(props.address());
                return false;
            }

            NativeLong numAtoms = numAtomsRef.getValue();
            int longSize = Runtime.getSystemRuntime().longSize();
            for (long i = 0; i < numAtoms.longValue(); i ++) {
                if (props.getNativeLong(i * longSize) == atom) {
                    xlib.XFree(props.address());
                    return true;
                }
            }

            xlib.XFree(props.address());
            return false;
        }
        finally {
            SunToolkit.awtUnlock();
        }
    }

    public static boolean sendClientMessageXEvent(long display, long window, String atomName, long[] data) {
        checkX11();
        if (window == 0 || display == 0 || data.length != 5) return false;
        Xlib xlib = Xlib.INSTANCE;
        if (xlib == null) return false;
        SunToolkit.awtLock();
        try {
            long root = xlib.XDefaultRootWindow(display);
            if (root == 0) return false;
            long atom = xlib.XInternAtom(display, atomName, false);
            if (!isXWMHintSupported(display, root, atom)) return false;
            xlib.XUngrabPointer(display, 0);
            xlib.XUngrabKeyboard(display, 0);
            XClientMessageEvent event = new XClientMessageEvent(Runtime.getRuntime(xlib));
            event.type.set(X.ClientMessage);
            event.serial.set(0);
            event.send_event.set(1);
            event.display.set(display);
            event.window.set(window);
            event.message_type.set(atom);
            event.format.set(32);
            for (int i = 0; i < data.length; i ++) {
                event.data[i].set(data[i]);
            }
            xlib.XSendEvent(display, root, false, NativeLong.valueOf(X.SubstructureRedirectMask | X.SubstructureNotifyMask), event);
            xlib.XFlush(display);
        }
        finally {
            SunToolkit.awtUnlock();
        }
        return true;
    }

    public static boolean moveResizeXWindow(long display, long window, Point location, int direction) {
        checkX11();
        Objects.requireNonNull(location, "location");
        return sendClientMessageXEvent(display, window, "_NET_WM_MOVERESIZE", new long[] {location.x, location.y, direction, X.Button1, 1});
    }

    public static Cursor createXCustomCursor(Image cursor, Point hotSpot, String name) throws IndexOutOfBoundsException, HeadlessException {
        checkX11();
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        long display = getDisplay();
        if (display == 0 || Xcursor.INSTANCE == null || !Xcursor.INSTANCE.XcursorSupportsARGB(display))
            return Toolkit.getDefaultToolkit().createCustomCursor(cursor, hotSpot, name);
        else return new X11CustomCursor(cursor, hotSpot, name);
    }

    private static final Map<String, Cursor> systemCursors = new ConcurrentHashMap<>(1);

    public static Cursor getXSystemCursor(int type) throws IllegalArgumentException, HeadlessException {
        checkX11();
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        int cursorType = getCursorType(type);
        if (cursorType != Cursor.CUSTOM_CURSOR) return Cursor.getPredefinedCursor(cursorType);
        Xlib xlib = Xlib.INSTANCE;
        long display = getDisplay();
        if (display == 0 || xlib == null) return null;
        final String name = getXSystemCursorName(type);
        final long pData = xlib.XCreateFontCursor(display, type);
        if (pData == 0) return Cursor.getDefaultCursor();
        if (!systemCursors.containsKey(name)) systemCursors.put(name, new X11SystemCursor(name, pData));
        return systemCursors.get(name);
    }

    private static int getCursorType(int type) {
        switch (type) {
            case CursorFont.XC_left_ptr:
                return Cursor.DEFAULT_CURSOR;
            case CursorFont.XC_crosshair:
                return Cursor.CROSSHAIR_CURSOR;
            case CursorFont.XC_xterm:
                return Cursor.TEXT_CURSOR;
            case CursorFont.XC_watch:
                return Cursor.WAIT_CURSOR;
            case CursorFont.XC_bottom_left_corner:
                return Cursor.SW_RESIZE_CURSOR;
            case CursorFont.XC_top_left_corner:
                return Cursor.NW_RESIZE_CURSOR;
            case CursorFont.XC_bottom_right_corner:
                return Cursor.SE_RESIZE_CURSOR;
            case CursorFont.XC_top_right_corner:
                return Cursor.NE_RESIZE_CURSOR;
            case CursorFont.XC_bottom_side:
                return Cursor.S_RESIZE_CURSOR;
            case CursorFont.XC_top_side:
                return Cursor.N_RESIZE_CURSOR;
            case CursorFont.XC_left_side:
                return Cursor.W_RESIZE_CURSOR;
            case CursorFont.XC_right_side:
                return Cursor.E_RESIZE_CURSOR;
            case CursorFont.XC_hand2:
                return Cursor.HAND_CURSOR;
            case CursorFont.XC_fleur:
                return Cursor.MOVE_CURSOR;
            default:
                return Cursor.CUSTOM_CURSOR;
        }
    }

    public static String getXSystemCursorName(int type) {
        checkX11();
        switch (type) {
            case 0: return "X_cursor";
            case 2: return "arrow";
            case 4: return "based_arrow_down";
            case 6: return "based_arrow_up";
            case 8: return "boat";
            case 10: return "bogosity";
            case 12: return "bottom_left_corner";
            case 14: return "bottom_right_corner";
            case 16: return "bottom_side";
            case 18: return "bottom_tee";
            case 20: return "box_spiral";
            case 22: return "center_ptr";
            case 24: return "circle";
            case 26: return "clock";
            case 28: return "coffee_mug";
            case 30: return "cross";
            case 32: return "cross_reverse";
            case 34: return "crosshair";
            case 36: return "diamond_cross";
            case 38: return "dot";
            case 40: return "dotbox";
            case 42: return "double_arrow";
            case 44: return "draft_large";
            case 46: return "draft_small";
            case 48: return "draped_box";
            case 50: return "exchange";
            case 52: return "fleur";
            case 54: return "gobbler";
            case 56: return "gumby";
            case 58: return "hand1";
            case 60: return "hand2";
            case 62: return "heart";
            case 64: return "icon";
            case 66: return "iron_cross";
            case 68: return "left_ptr";
            case 70: return "left_side";
            case 72: return "left_tee";
            case 74: return "leftbutton";
            case 76: return "ll_angle";
            case 78: return "lr_angle";
            case 80: return "man";
            case 82: return "middlebutton";
            case 84: return "mouse";
            case 86: return "pencil";
            case 88: return "pirate";
            case 90: return "plus";
            case 92: return "question_arrow";
            case 94: return "right_ptr";
            case 96: return "right_side";
            case 98: return "right_tee";
            case 100: return "rightbutton";
            case 102: return "rtl_logo";
            case 104: return "sailboat";
            case 106: return "sb_down_arrow";
            case 108: return "sb_h_double_arrow";
            case 110: return "sb_left_arrow";
            case 112: return "sb_right_arrow";
            case 114: return "sb_up_arrow";
            case 116: return "sb_v_double_arrow";
            case 118: return "shuttle";
            case 120: return "sizing";
            case 122: return "spider";
            case 124: return "spraycan";
            case 126: return "star";
            case 128: return "target";
            case 130: return "tcross";
            case 132: return "top_left_arrow";
            case 134: return "top_left_corner";
            case 136: return "top_right_corner";
            case 138: return "top_side";
            case 140: return "top_tee";
            case 142: return "trek";
            case 144: return "ul_angle";
            case 146: return "umbrella";
            case 148: return "ur_angle";
            case 150: return "watch";
            case 152: return "xterm";
            default: throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    public static Cursor getXSystemCursor(String name) throws HeadlessException {
        checkX11();
        if (GraphicsEnvironment.isHeadless()) throw new HeadlessException();
        long display = getDisplay();
        if (display == 0 || Xcursor.INSTANCE == null) return null;
        long pData = Xcursor.INSTANCE.XcursorLibraryLoadCursor(display, name);
        if (pData == 0) return Cursor.getDefaultCursor();
        else {
            if (!systemCursors.containsKey(name)) systemCursors.put(name, new X11SystemCursor(name, pData));
            return systemCursors.get(name);
        }
    }

    public static boolean isXcursorSupported() {
        return Xcursor.INSTANCE != null;
    }

    public static boolean fixDragAndDropCursors() {
        checkX11();
        Map<String, Object> desktopProperties = AWTFactory.getDesktopProperties();
        if (desktopProperties == null) return false;
        desktopProperties.put("DnD.Cursor.CopyDrop", getXSystemCursor("copy"));
        desktopProperties.put("DnD.Cursor.MoveDrop", getXSystemCursor("move"));
        desktopProperties.put("DnD.Cursor.LinkDrop", getXSystemCursor("alias"));
        desktopProperties.put("DnD.Cursor.CopyNoDrop", getXSystemCursor("grabbing"));
        desktopProperties.put("DnD.Cursor.MoveNoDrop", getXSystemCursor("grabbing"));
        desktopProperties.put("DnD.Cursor.LinkNoDrop", getXSystemCursor("grabbing"));
        return true;
    }

    public static boolean setXWindowType(long display, long window, CharSequence windowType) throws HeadlessException {
        checkX11();
        Xlib xlib = Xlib.INSTANCE;
        if (xlib == null || display == 0 || window == 0) return false;
        SunToolkit.awtLock();
        try {
            long windowTypeAtom = xlib.XInternAtom(display, "_NET_WM_WINDOW_TYPE", false);
            long windowTypeDnD = xlib.XInternAtom(display, windowType, false);
            Pointer data = Memory.allocate(Runtime.getRuntime(xlib), 8);
            data.putLong(0, windowTypeDnD);
            xlib.XChangeProperty(display, window, windowTypeAtom, Xatom.XA_ATOM, 32, X.PropModeReplace, data, 1);
            xlib.XFlush(display);
            return true;
        } finally {
            SunToolkit.awtUnlock();
        }
    }

    public static boolean allowXWindowInputPassthrough(long display, long window) {
        checkX11();
        Xfixes xfixes = Xfixes.INSTANCE;
        if (xfixes == null || display == 0 || window == 0) return false;
        SunToolkit.awtLock();
        try {
            Pointer region = xfixes.XFixesCreateRegion(display, null, 0);
            xfixes.XFixesSetWindowShapeRegion(display, window, ShapeConst.ShapeBounding, 0, 0, Pointer.wrap(Runtime.getRuntime(xfixes), 0));
            xfixes.XFixesSetWindowShapeRegion(display, window, ShapeConst.ShapeInput, 0, 0, region);
            xfixes.XFixesDestroyRegion(display, region);
            return true;
        }
        finally {
            SunToolkit.awtUnlock();
        }
    }

    public static boolean isX11DragSourceSupported() {
        return Xfixes.INSTANCE != null;
    }

    public static void checkX11DragSourceSupported() {
        if (Xfixes.INSTANCE == null) throw new IllegalStateException("could not load Xfixes library");
    }

    public static boolean setXWindowOverrideRedirect(long display, long window) {
        checkX11();
        Xlib xlib = Xlib.INSTANCE;
        if (display == 0 || window == 0 || xlib == null) return false;
        SunToolkit.awtLock();
        try {
            XSetWindowAttributes attributes = new XSetWindowAttributes(Runtime.getRuntime(xlib));
            attributes.override_redirect.set(1);
            xlib.XChangeWindowAttributes(display, window, NativeLong.valueOf(X.CWOverrideRedirect), attributes);
            return true;
        }
        finally {
            SunToolkit.awtUnlock();
        }
    }

}
