package com.checkdroid.gradle;

import org.gradle.api.GradleException;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.plugins.PluginContainer;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Varad on 9/1/2015.
 */
public class CheckdroidPlugin implements Plugin<Project> {
    @Override
    public void apply(Project project) {

        PluginContainer plugins = project.getPlugins();
        if(!plugins.hasPlugin("android") && !plugins.hasPlugin("android-library")) {
            throw new GradleException("This must be an android or android-library project");
        }

        Map<String,Object> cdRunTaskInfo = new HashMap<String,Object>();
        cdRunTaskInfo.put("type", cdRunTasks.class);
        cdRunTaskInfo.put("description", "Uploads APKs to CheckDroid server");
        cdRunTaskInfo.put("group", "CheckDroid");
        project.task(cdRunTaskInfo, "cdRunTests");

        Map<String,Object> cdGetTaskInfo = new HashMap<String, Object>();
        cdGetTaskInfo.put("type",cdGetTasks.class);
        cdGetTaskInfo.put("description","Get Tests from CheckDroid server");
        cdGetTaskInfo.put("group","CheckDroid");
        project.task(cdGetTaskInfo,"cdGetTests");
        // Define conventions and attach them to tasks
        CheckdroidExtension extensions = new CheckdroidExtension(project);

        project.getExtensions().add("checkdroid", extensions);
    }
}
