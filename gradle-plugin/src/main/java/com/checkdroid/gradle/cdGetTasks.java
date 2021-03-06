package com.checkdroid.gradle;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.*;

/**
 * Created by Varad on 9/4/2015.
 */
public class cdGetTasks extends DefaultTask {

    @TaskAction
    public void cdGetTests(){
        CheckdroidExtension ext = (CheckdroidExtension) getProject()
                .getExtensions().findByName("checkdroid");

        if(ext.debug){
            System.out.println("Server set to: " + ext.server);
            System.out.println("Fetching Tests for "+ ext.email + " and " + ext.fetchPackageName());
        }
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(ext.server).setPath("/api/package/testcases")
                .setParameter("email", ext.email)
                .setParameter("packagename",ext.fetchPackageName())
                .setParameter("apiKey", ext.apiKey);

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet get = new HttpGet(builder.toString());
        HttpResponse response = null;

        try {
            response = httpclient.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();

        if(ext.debug){
            System.out.println("Response: " + response.getStatusLine());
        }

        if(response.getStatusLine().getStatusCode() == 500){
            try {
                System.out.println(EntityUtils.toString(resEntity));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if (response.getStatusLine().getStatusCode() == 200){
            BufferedInputStream bis = null;
            try {
                bis = new BufferedInputStream(resEntity.getContent());
                String filePath = ext.fetchTestDir() + File.separatorChar + "BaristaTests.java";
                File file = new File(filePath);
                if (file.exists()) {
                    System.err.println("BaristaTests.java exists, delete the file and run task again to generate new file ");
                    bis.close();
                } else {
                    BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                    int inByte;
                    while ((inByte = bis.read()) != -1) bos.write(inByte);
                    bis.close();
                    bos.close();
                    System.out.println("Success");
                }
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
