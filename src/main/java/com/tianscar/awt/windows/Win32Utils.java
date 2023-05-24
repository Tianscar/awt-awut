package com.tianscar.awt.windows;

import com.tianscar.awt.AWTFactory;
import jnr.ffi.LibraryLoader;
import jnr.ffi.Memory;
import jnr.ffi.Pointer;
import jnr.ffi.Runtime;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class Win32Utils {

    static ShellApi initShellApi() {
        try {
            return AWTFactory.isWindows() ? LibraryLoader.create(ShellApi.class).load("Shell32") : null;
        }
        catch (Exception e) {
            return null;
        }
    }

    public static boolean shellApiMoveToTrash(File file) {
        if (file == null || !file.exists()) return false;
        ShellApi shellapi = ShellApi.INSTANCE;
        if (shellapi == null) return false;
        SHFILEOPSTRUCTW fop = new SHFILEOPSTRUCTW(Runtime.getRuntime(shellapi));
        fop.wFunc.set(shellapi.FO_DELETE);
        String path = file.getAbsolutePath();
        Pointer pointer = Memory.allocate(Runtime.getRuntime(shellapi), path.length() * 2 + 2);
        pointer.putString(0, path, path.length() * 2, StandardCharsets.UTF_16LE);
        fop.pFrom.set(pointer);
        fop.fFlags.set(shellapi.FOF_ALLOWUNDO | shellapi.FOF_NOCONFIRMATION | shellapi.FOF_NOERRORUI);
        return shellapi.SHFileOperationW(fop) == 0;
    }

    public static boolean isShellApiMoveToTrashSupported() {
        return ShellApi.INSTANCE != null;
    }

}
