package maged.csi5230.edu.tictactoegame.utils;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.JarFile;

/**
 * Created by dragonlayout on 2017/11/11.
 */

public class ActivityController {

    private static List<Activity> activityList = new ArrayList<>();

    public static void add(Activity activity) {
        if (!activityList.contains(activity)) {
            activityList.add(activity);
        }
    }

    public static void remove(Activity activity) {
        if (activityList.contains(activity)) {
            activityList.remove(activity);
        }
    }

    public static void finishSpecifiedActivity(Activity activity) {
        if (activity != null) {
            if (activityList.contains(activity)) {
                activity.finish();
            }
        }
    }

    public static void finishAllActivities() {
        for (Activity activity : activityList) {
            activity.finish();
        }
    }
}
