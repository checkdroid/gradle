package com.checkdroid.gradle

import org.gradle.api.Project

class CheckdroidExtension {
    private final Project target;
    public File appApk;
    public File testApk;
    public String email;
    public String apiKey;
    public String server;
    public String packageName;
    public Boolean debug;

    public CheckdroidExtension(Project target) {
        this.target = target;

        String apkDir = target.getBuildDir().toString() + "/outputs/apk"

        appApk = new File(apkDir, "app-debug.apk");
        testApk = new File(apkDir, "app-debug-androidTest-unaligned.apk");
        email = new String();
        apiKey = new String();
        packageName = fetchPackageName();
        debug = false;
        server = new String("app.checkdroid.com");
    }

    String fetchPackageName() {
        return target.android.defaultConfig.applicationId;
    }

    String fetchTestDir() {
        return target.getProjectDir().toString()+File.separator+(['src', 'androidTest','java']+fetchPackageName().split("\\.").flatten()).join(File.separator)
    }
}
