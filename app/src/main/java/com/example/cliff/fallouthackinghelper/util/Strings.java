package com.example.cliff.fallouthackinghelper.util;

/**
 * Created by Cliff on 12/28/2015.
 */
public class Strings {

    public static String StringOr(String s1, String s2) {
        char[] s1a = s1.toCharArray();
        char[] s2a = s2.toCharArray();
        int finLen = Math.max(s1a.length, s2a.length);
        char[] ret = new char[finLen];

        for (int i = 0; i < finLen; i++) {
            Character cs1 = (i < s1a.length) ? s1a[i] : null;
            Character cs2 = (i < s1a.length) ? s2a[i] : null;

            if ((cs1 == null) || (cs2 == null)) break;

            ret[i] = (
                (cs1 == cs2)  ||
                (cs1 == '\0') ||
                (cs2 == '\0')
            ) ? ((cs1 != '\0') ? cs1 : cs2) : ' ';
        }

        return ret.toString();
    }

}
