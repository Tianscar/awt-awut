package com.tianscar.awt.gtk;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class GError extends Struct {

    public final Unsigned32 domain = new Unsigned32();
    public final Signed32 code = new Signed32();
    public final Pointer message = new Pointer();

    public GError(Runtime runtime) {
        super(runtime);
    }

}
