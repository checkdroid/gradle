package com.checkdroid.gradle;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Varad on 9/1/2015.
 */
public class cdRunTasks extends DefaultTask {

    @TaskAction
    public void cdRunTests(){

        CheckdroidExtension extensions = (CheckdroidExtension) getProject()
                .getExtensions().findByName("checkdroid");

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost("http://" + extensions.server + "/api/upload");

        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();

        multipartEntity.addBinaryBody("appApk", extensions.appApk, ContentType.create("application/vnd.android.package-archive"), extensions.appApk.getName());
        multipartEntity.addBinaryBody("testApk", extensions.testApk, ContentType.create("application/vnd.android.package-archive"), extensions.testApk.getName());
        multipartEntity.addPart("email", new StringBody(extensions.email, ContentType.TEXT_PLAIN));
        multipartEntity.addPart("apiKey", new StringBody(extensions.apiKey, ContentType.TEXT_PLAIN));
        multipartEntity.addPart("appName", new StringBody(getProject().getParent().getName(),ContentType.TEXT_PLAIN));
        multipartEntity.addPart("packageName", new StringBody(extensions.fetchPackageName(),ContentType.TEXT_PLAIN));

        httppost.setEntity(multipartEntity.build());

        System.out.println("executing request " + httppost.getRequestLine());
        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            System.err.println("Couldn't find app-debug.apk and/or app-debug-androidTest-unaligned.apk in app/build/outputs/apk/");
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();

        System.out.println(response.getStatusLine());
        if (resEntity != null) {
            try {
                System.out.println(EntityUtils.toString(resEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (resEntity != null) {
            try {
                resEntity.consumeContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        httpclient.getConnectionManager().shutdown();

    }
}
