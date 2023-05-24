package com.tianscar.awt.win32;

import jnr.ffi.annotations.IgnoreError;

public interface ShellApi {

    int FO_MOVE           = 0x0001;
    int FO_COPY           = 0x0002;
    int FO_DELETE         = 0x0003;
    int FO_RENAME         = 0x0004;

    int FOF_MULTIDESTFILES         = 0x0001;
    int FOF_CONFIRMMOUSE           = 0x0002;
    int FOF_SILENT                 = 0x0004;
    int FOF_RENAMEONCOLLISION      = 0x0008;
    int FOF_NOCONFIRMATION         = 0x0010;
    int FOF_WANTMAPPINGHANDLE      = 0x0020;
    int FOF_ALLOWUNDO              = 0x0040;
    int FOF_FILESONLY              = 0x0080;
    int FOF_SIMPLEPROGRESS         = 0x0100;
    int FOF_NOCONFIRMMKDIR         = 0x0200;
    int FOF_NOERRORUI              = 0x0400;
    int FOF_NOCOPYSECURITYATTRIBS  = 0x0800;
    int FOF_NORECURSION            = 0x1000;  /* don't do recursion into directories */
    int FOF_NO_CONNECTED_ELEMENTS  = 0x2000;  /* don't do connected files */
    int FOF_WANTNUKEWARNING        = 0x4000;  /* during delete operation, warn if delete instead of recycling (even if FOF_NOCONFIRMATION) */
    int FOF_NORECURSEREPARSE       = 0x8000;  /* don't do recursion into reparse points */
    int FOF_NO_UI                  = (FOF_SILENT | FOF_NOCONFIRMATION | FOF_NOERRORUI | FOF_NOCONFIRMMKDIR);

    ShellApi INSTANCE = Win32Utils.initShellApi();

    @IgnoreError
    int SHFileOperationW(SHFILEOPSTRUCTW lpFileOp);

}
