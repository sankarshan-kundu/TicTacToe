package com.sankarshan.tictactoefree.util;

import java.lang.reflect.Field;

/**
 * Created by Sankarshan on 8/12/2015.
 */
public final class GeneralUtility {
    private GeneralUtility(){}
    public static int getResId(String resName, Class<?> c) {
        try {
            Field idField = c.getDeclaredField(resName);
            return idField.getInt(idField);
        } catch (Exception e) {
            //e.printStackTrace();
            return -1;
        }
    }
}
