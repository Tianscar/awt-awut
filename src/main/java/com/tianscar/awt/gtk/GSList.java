package com.tianscar.awt.gtk;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class GSList extends Struct {

    public final Pointer data = new Pointer();
    public final Pointer next = new Pointer();

    public GSList(Runtime runtime) {
        super(runtime);
    }

}
