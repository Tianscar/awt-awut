package com.tianscar.awt;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

public class DesktopTest {

    public static void main(String[] args) throws URISyntaxException {
        AWTFactory.browse(new URI("https://github.com"));
        AWTFactory.open(new File("LICENSE"));
        AWTFactory.mail(new URI("mailto://tianscar@protonmail.com"));
    }
}
