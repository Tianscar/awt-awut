package com.tianscar.awt.win32;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class SHFILEOPSTRUCTW extends Struct {

    public final Pointer hwnd = new Pointer();
    public final Unsigned32 wFunc = new Unsigned32();
    public final Pointer pFrom = new Pointer();
    public final Pointer pTo = new Pointer();
    public final WORD fFlags = new WORD();
    public final BOOL16 fAnyOperationsAborted = new BOOL16();
    public final Pointer hNameMappings = new Pointer();
    public final Pointer lpszProgressTitle = new Pointer();

    public SHFILEOPSTRUCTW(Runtime runtime) {
        super(runtime);
    }

}
