package com.moltenwolfcub.timestables;

public class StringUtils {

    public static String Title(String raw) {
        StringBuilder titleCaseName = new StringBuilder();
        boolean nextTitleCase = true;

        if (raw != null) {
            for (char c : raw.toCharArray()) {
                if (Character.isSpaceChar(c)) {
                    nextTitleCase = true;
                } else if (nextTitleCase) {
                    c = Character.toUpperCase(c);
                    nextTitleCase = false;
                } else {
                    c = Character.toLowerCase(c);
                }
                titleCaseName.append(c);
            }
        }
        return titleCaseName.toString();
    }
}
