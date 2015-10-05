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

import java.io.IOException;

/**
 * Created by Varad on 9/1/2015.
 */
public class cdRunTasks extends DefaultTask {

    @TaskAction
    public void cdRunTests(){

        CheckdroidExtension ext = (CheckdroidExtension) getProject()
                .getExtensions().findByName("checkdroid");

        HttpClient httpclient = new DefaultHttpClient();

        HttpPost httppost = new HttpPost("http://" + ext.server + "/api/upload");

        MultipartEntityBuilder multipartEntity = MultipartEntityBuilder.create();

        if(ext.debug){
            System.out.println("Uploading");
            System.out.println("email : " + ext.email);
            System.out.println("appName : " + getProject().getParent().getName());
            System.out.println("packageName : " + ext.fetchPackageName());
            System.out.println("appApk : " + ext.appApk);
            System.out.println("testApk : " + ext.testApk);
        }
        multipartEntity.addBinaryBody("appApk", ext.appApk, ContentType.create("application/vnd.android.package-archive"), ext.appApk.getName());
        multipartEntity.addBinaryBody("testApk", ext.testApk, ContentType.create("application/vnd.android.package-archive"), ext.testApk.getName());
        multipartEntity.addPart("email", new StringBody(ext.email, ContentType.TEXT_PLAIN));
        multipartEntity.addPart("apiKey", new StringBody(ext.apiKey, ContentType.TEXT_PLAIN));
        multipartEntity.addPart("appName", new StringBody(getProject().getParent().getName(),ContentType.TEXT_PLAIN));
        multipartEntity.addPart("packageName", new StringBody(ext.fetchPackageName(),ContentType.TEXT_PLAIN));

        httppost.setEntity(multipartEntity.build());
        if(ext.debug){
            System.out.println("Sending HTTP request " + httppost.getRequestLine());
        }

        HttpResponse response = null;
        try {
            response = httpclient.execute(httppost);
        } catch (IOException e) {
            System.err.println("Couldn't find app-debug.apk and/or app-debug-androidTest-unaligned.apk in app/build/outputs/apk/");
            System.err.println("Please run gradle tasks assembleDebug and assembleDebugAndroidTest before");
            e.printStackTrace();
            throw new RuntimeException("Please build the app and test APKs.");
        }
        HttpEntity resEntity = response.getEntity();

        if(ext.debug){
            System.out.println(response.getStatusLine());
        }

        String DEBUG_MSG = "Tip: Set debug=true in your checkdroid gradle config to see debug messages.";
        String ERROR_NETWORK = "ERROR: Transfer failed. Please check your network connection and checkdroid gradle configuration.\n"+ ((ext.debug)? "" : DEBUG_MSG);

        boolean isError = false;

        if(response.getStatusLine().getStatusCode() == 200){
            if (resEntity != null) {
                try {
                    String message = EntityUtils.toString(resEntity);
                    if(message.contains("ERROR") || message.contains("Failure")) {
                        isError = true;
                        System.out.println(((ext.debug)? "" : DEBUG_MSG));
                    }
                    System.out.println(message);
                    resEntity.consumeContent();
                } catch (IOException e) {
                    System.out.println(ERROR_NETWORK);
                    // e.printStackTrace();
                    isError = true;
                }
            }
        } else {
            System.out.println(ERROR_NETWORK);
            isError = true;
        }

        if(!isError) {
            System.out.println("App and Tests uploaded successfully. Please hang tight while we run your tests and send results your way.");
        }


        httpclient.getConnectionManager().shutdown();

    }
}
