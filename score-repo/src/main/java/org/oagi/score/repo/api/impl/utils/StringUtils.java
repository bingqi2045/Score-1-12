package org.oagi.score.repo.api.impl.utils;

public abstract class StringUtils {

    public static boolean isEmpty(Object str) {
        if (str == null) {
            return true;
        }
        if (str instanceof String) {
            str = trim((String) str);
        }
        return "".equals(str);
    }

    public static String trim(String str) {
        if (str == null) {
            return "";
        }
        return str.trim();
    }

    public static boolean equals(String o1, String o2) {
        return o1 == null ? o2 == null : trim(o1).equals(trim(o2));
    }

}
