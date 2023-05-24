package com.tianscar.awt.gtk;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class Gtk2ShowURITest {

    public static void main(String[] args) throws URISyntaxException {
        GtkUtils.gtk2ShowURI(new URI("https://github.com"));
        GtkUtils.gtk2ShowURI(new File("LICENSE").toURI());
        GtkUtils.gtk2ShowURI(new URI("mailto://tianscar@protonmail.com"));
    }

}
