package com.tonghu.removeallbackgroundapp.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class AppUtils {

    public static boolean isSystemApp(Context context, String packageName) {
        try {
            PackageManager pm = context.getPackageManager();
            // Get packageinfo for target application
            PackageInfo targetPkgInfo = pm.getPackageInfo(
                    packageName, PackageManager.GET_SIGNATURES);
            // Get packageinfo for system package
            PackageInfo sys = pm.getPackageInfo(
                    "android", PackageManager.GET_SIGNATURES);
            // Match both packageinfo for there signatures
            return (targetPkgInfo != null && targetPkgInfo.signatures != null && sys.signatures[0]
                    .equals(targetPkgInfo.signatures[0]));
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isPackageRunning(Context context, String packagename) {
        Log.i("tonghu", "MainActivity, isPackageRunning(L127): " + packagename);
        return findPIDbyPackageName(context, packagename) != -1;
    }

    public static int findPIDbyPackageName(Context context, String packagename) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        int result = -1;
        if (am != null) {
            for (ActivityManager.RunningAppProcessInfo pi : am.getRunningAppProcesses()){
                if (pi.processName.equalsIgnoreCase(packagename)) {
                    result = pi.pid;
                }
                if (result != -1) break;
            }
        } else {
            result = -1;
        }

        return result;
    }

    public static boolean killPackageProcesses(Context context, String packagename) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        boolean result = false;

        if (am != null) {
            am.killBackgroundProcesses(packagename);
            result = !isPackageRunning(context, packagename);
        } else {
            result = false;
        }

        return result;
    }
}
