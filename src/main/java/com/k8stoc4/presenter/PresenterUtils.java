package com.k8stoc4.presenter;

public class PresenterUtils {
    private PresenterUtils() {}

    public static String sanitizeNamespacedId(String id) {
        final String[] splitId = id.split("\\.", 2);
        if (splitId.length == 1) {
            return splitId[0].replace(".", "-");
        } else {
            return splitId[0] + "." + splitId[1].replace(".", "-");
        }
    }

    public static String sanitizeComponentId(String id) {
        return id.replace(".", "-");
    }
}
