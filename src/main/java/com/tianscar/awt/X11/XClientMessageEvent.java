package com.tianscar.awt.X11;

import jnr.ffi.Runtime;

public class XClientMessageEvent extends XEvent {

    public XClientMessageEvent(Runtime runtime) {
        super(runtime);
    }

    public final Signed32 type = new Signed32();
    public final UnsignedLong serial = new UnsignedLong();
    public final Signed32 send_event = new Signed32();
    public final Pointer display = new Pointer();
    public final UnsignedLong window = new UnsignedLong();
    public final UnsignedLong message_type = new UnsignedLong();
    public final Signed32 format = new Signed32();
    public final SignedLong[] data = array(new SignedLong[5]);

}
