package com.tianscar.awt.X11;

public interface X {
    
    int NoEventMask			    = 0;
    int KeyPressMask			= 1 << 0;
    int KeyReleaseMask			= 1 << 1;
    int ButtonPressMask			= 1 << 2;
    int ButtonReleaseMask		= 1 << 3;
    int EnterWindowMask			= 1 << 4;
    int LeaveWindowMask			= 1 << 5;
    int PointerMotionMask		= 1 << 6;
    int PointerMotionHintMask	= 1 << 7;
    int Button1MotionMask		= 1 << 8;
    int Button2MotionMask		= 1 << 9;
    int Button3MotionMask		= 1 << 10;
    int Button4MotionMask		= 1 << 11;
    int Button5MotionMask		= 1 << 12;
    int ButtonMotionMask		= 1 << 13;
    int KeymapStateMask			= 1 << 14;
    int ExposureMask			= 1 << 15;
    int VisibilityChangeMask	= 1 << 16;
    int StructureNotifyMask		= 1 << 17;
    int ResizeRedirectMask		= 1 << 18;
    int SubstructureNotifyMask	= 1 << 19;
    int SubstructureRedirectMask= 1 << 20;
    int FocusChangeMask			= 1 << 21;
    int PropertyChangeMask		= 1 << 22;
    int ColormapChangeMask		= 1 << 23;
    int OwnerGrabButtonMask		= 1 << 24;
    
    int Button1			= 1;
    int Button2			= 2;
    int Button3			= 3;
    int Button4			= 4;
    int Button5			= 5;
    
    int KeyPress		    = 2;
    int KeyRelease		    = 3;
    int ButtonPress		    = 4;
    int ButtonRelease		= 5;
    int MotionNotify		= 6;
    int EnterNotify		    = 7;
    int LeaveNotify		    = 8;
    int FocusIn			    = 9;
    int FocusOut		    = 10;
    int KeymapNotify		= 11;
    int Expose			    = 12;
    int GraphicsExpose		= 13;
    int NoExpose		    = 14;
    int VisibilityNotify	= 15;
    int CreateNotify		= 16;
    int DestroyNotify		= 17;
    int UnmapNotify		    = 18;
    int MapNotify		    = 19;
    int MapRequest		    = 20;
    int ReparentNotify		= 21;
    int ConfigureNotify		= 22;
    int ConfigureRequest	= 23;
    int GravityNotify		= 24;
    int ResizeRequest		= 25;
    int CirculateNotify		= 26;
    int CirculateRequest	= 27;
    int PropertyNotify		= 28;
    int SelectionClear		= 29;
    int SelectionRequest	= 30;
    int SelectionNotify		= 31;
    int ColormapNotify		= 32;
    int ClientMessage		= 33;
    int MappingNotify		= 34;
    int GenericEvent		= 35;
    int LASTEvent		    = 36;	/* must be bigger than any event # */
    
    int CWBackPixmap		= 1 << 0;
    int CWBackPixel		    = 1 << 1;
    int CWBorderPixmap		= 1 << 2;
    int CWBorderPixel       = 1 << 3;
    int CWBitGravity		= 1 << 4;
    int CWWinGravity		= 1 << 5;
    int CWBackingStore      = 1 << 6;
    int CWBackingPlanes	    = 1 << 7;
    int CWBackingPixel	    = 1 << 8;
    int CWOverrideRedirect	= 1 << 9;
    int CWSaveUnder	    	= 1 << 10;
    int CWEventMask		    = 1 << 11;
    int CWDontPropagate	    = 1 << 12;
    int CWColormap	    	= 1 << 13;
    int CWCursor	        = 1 << 14;

    int PropModeReplace         = 0;
    int PropModePrepend         = 1;
    int PropModeAppend          = 2;

    int StaticGray		= 0;
    int GrayScale		= 1;
    int StaticColor		= 2;
    int PseudoColor		= 3;
    int TrueColor		= 4;
    int DirectColor		= 5;

    int InputOutput		= 1;
    int InputOnly		= 2;

    int AllocNone		= 0;
    int AllocAll		= 1;
        
}
