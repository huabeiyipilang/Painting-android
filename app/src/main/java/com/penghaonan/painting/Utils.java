package com.penghaonan.painting;

import android.Manifest;
import android.os.Build;

import java.util.LinkedList;
import java.util.List;

public class Utils {
    public static List<String> getPermissions() {
        List<String> permissions = new LinkedList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permissions;
    }
}
