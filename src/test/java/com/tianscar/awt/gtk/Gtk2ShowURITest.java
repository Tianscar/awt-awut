package com.tianscar.awt.gtk;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class Gtk2ShowURITest {

    public static void main(String[] args) throws URISyntaxException {
        GtkUtils.showURI(new URI("https://github.com"));
        GtkUtils.showURI(new File("LICENSE").toURI());
        GtkUtils.showURI(new URI("mailto://tianscar@protonmail.com"));
    }

}
