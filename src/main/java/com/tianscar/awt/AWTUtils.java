package com.tianscar.awt;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Map;

public class AWTUtils {

    public static Map<String, Object> getDesktopProperties() {
        try {
            Field field = Toolkit.class.getDeclaredField("desktopProperties");
            field.setAccessible(true);
            return (Map<String, Object>) field.get(Toolkit.getDefaultToolkit());
        } catch (NoSuchFieldException | IllegalAccessException |ClassCastException e) {
            return null;
        }
    }

}
