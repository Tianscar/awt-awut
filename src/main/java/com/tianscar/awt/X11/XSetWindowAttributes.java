package com.tianscar.awt.X11;

import jnr.ffi.Runtime;
import jnr.ffi.Struct;

public class XSetWindowAttributes extends Struct {

    public final UnsignedLong background_pixmap = new UnsignedLong();
    public final UnsignedLong background_pixel = new UnsignedLong();
    public final UnsignedLong border_pixmap = new UnsignedLong();
    public final UnsignedLong border_pixel = new UnsignedLong();
    public final Signed32 bit_gravity = new Signed32();
    public final Signed32 win_gravity = new Signed32();
    public final Signed32 backing_store = new Signed32();
    public final UnsignedLong backing_planes = new UnsignedLong();
    public final UnsignedLong backing_pixel = new UnsignedLong();
    public final Signed32 save_under = new Signed32();
    public final SignedLong event_mask = new SignedLong();
    public final SignedLong do_not_propagate_mask = new SignedLong();
    public final Signed32 override_redirect = new Signed32();
    public final UnsignedLong colormap = new UnsignedLong();
    public final UnsignedLong cursor = new UnsignedLong();

    public XSetWindowAttributes(Runtime runtime) {
        super(runtime);
    }

}
