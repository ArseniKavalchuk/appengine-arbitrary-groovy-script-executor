package com.severn.script.service;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.http.MediaType;

import com.google.appengine.tools.cloudstorage.GcsFileMetadata;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.ListItem;
import com.google.appengine.tools.cloudstorage.ListOptions;
import com.google.appengine.tools.cloudstorage.ListResult;
import com.google.appengine.tools.cloudstorage.RetryParams;
import com.severn.script.service.domain.Script;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class GoogleCloudStorageScriptStorageService implements ScriptStorageService {
    
    private final Logger logger = Logger.getLogger(getClass().getName());
    
    private String bucket;

    @Override
    public Script saveScript(String scriptName, String content) {
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsOutputChannel outputChannel = null;
        Script result = null;
        GcsFilename fileName = new GcsFilename(bucket, scriptName);
        try {
            outputChannel = gcsService.createOrReplace(
                        fileName,
                        new GcsFileOptions.Builder().mimeType(MediaType.TEXT_PLAIN_VALUE).contentEncoding("UTF-8").build());
            outputChannel.write(ByteBuffer.wrap(content.getBytes(StandardCharsets.UTF_8)));
            outputChannel.close();
            outputChannel = null;
            GcsFileMetadata metedata = gcsService.getMetadata(fileName);
            
            result = new Script(
                    metedata.getFilename().getObjectName(),
                    metedata.getEtag(),
                    content);
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (outputChannel != null) {
                try {
                    outputChannel.close();
                } catch (IOException e) {
                    logger.log(Level.SEVERE, e.getMessage(), e);
                }
            }
        }
        return result;
    }

    @Override
    public void deleteScript(String scriptName) {
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        try {
            gcsService.delete(new GcsFilename(bucket, scriptName));
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Script readScript(String scriptName) {
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        GcsInputChannel inputChannel = null;
        String scriptContent = null;
        try {
            GcsFilename file = new GcsFilename(bucket, scriptName);
            GcsFileMetadata metadata = gcsService.getMetadata(file);
            int fileSize = (int) metadata.getLength();
            if (fileSize > 0) {
                ByteBuffer resultBuffer = ByteBuffer.allocate(fileSize);
                inputChannel = gcsService.openReadChannel(new GcsFilename(bucket, scriptName), 0L);
                inputChannel.read(resultBuffer);
                scriptContent = new String(resultBuffer.array(), StandardCharsets.UTF_8);
            } else {
                scriptContent = "";
            }
            return new Script(metadata.getFilename().getObjectName(), metadata.getEtag(), scriptContent);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        } finally {
            if (inputChannel != null) {
                inputChannel.close();
            }
        }
    }

    @Override
    public List<Script> listScripts(int page, int count) {
        GcsService gcsService = GcsServiceFactory.createGcsService(RetryParams.getDefaultInstance());
        List<Script> result = new ArrayList<>();
        try {
            ListResult listResult = gcsService.list(bucket, new ListOptions.Builder().setRecursive(true).build());
            while (listResult.hasNext()) {
                ListItem item = listResult.next();
                if (item.isDirectory())
                    continue;
                Script script = new Script(item.getName(), item.getEtag(), null);
                result.add(script);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw new RuntimeException(e);
        }
        
        return result;
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

}
