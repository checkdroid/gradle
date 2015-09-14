package com.checkdroid.gradle;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;

import java.io.*;

/**
 * Created by Varad on 9/4/2015.
 */
public class cdGetTasks extends DefaultTask {

    @TaskAction
    public void cdGetTests(){
        CheckdroidExtension extensions = (CheckdroidExtension) getProject()
                .getExtensions().findByName("checkdroid");
        URIBuilder builder = new URIBuilder();
        builder.setScheme("http").setHost(extensions.server).setPath("/api/package/testcases")
                .setParameter("email", extensions.email)
                .setParameter("packagename",extensions.fetchPackageName())
                .setParameter("apiKey",extensions.apiKey);

        HttpClient httpclient = new DefaultHttpClient();

        HttpGet get = new HttpGet(builder.toString());
        HttpResponse response = null;

        try {
            response = httpclient.execute(get);
        } catch (IOException e) {
            e.printStackTrace();
        }
        HttpEntity resEntity = response.getEntity();

        System.out.println(response.getStatusLine());

        BufferedInputStream bis = null;
        try {
            bis = new BufferedInputStream(resEntity.getContent());
            String filePath = extensions.fetchTestDir()+File.separatorChar + "BaristaTests.java";
            File file = new File(filePath);
            if(file.exists())
            {
                System.err.println("BaristaTests.java exists, delete the file and run task again to generate new file ");
                bis.close();
            }else {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
                int inByte;
                while ((inByte = bis.read()) != -1) bos.write(inByte);
                bis.close();
                bos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
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
