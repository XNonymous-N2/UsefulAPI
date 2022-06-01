package de.xnonymous.usefulapi.util;

import java.util.Arrays;

public class Checks {

    public static boolean isNotEmpty(String... strings) {
        return Arrays.stream(strings).noneMatch(s -> s == null || s.isEmpty());
    }

    public static <T> T nullOr(T what, T or) {
        return what == null ? or : what;
    }

}
