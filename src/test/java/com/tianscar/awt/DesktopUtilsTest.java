package com.tianscar.awt;

import java.io.File;
import java.net.URISyntaxException;

public class DesktopUtilsTest {

    public static void main(String[] args) throws URISyntaxException {
        //DesktopUtils.browse(new URI("https://github.com"));
        DesktopUtils.open(new File("LICENSE"));
        //DesktopUtils.mail(new URI("mailto://tianscar@protonmail.com"));
    }
}
