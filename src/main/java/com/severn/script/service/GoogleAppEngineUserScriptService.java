package com.severn.script.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 *
 * @author Arseny Kovalchuk
 *
 */
public class GoogleAppEngineUserScriptService implements UserScriptService {
    
    private final Logger logger = Logger.getLogger(getClass().getName());
    
    private static final String USER_SCRIPT_RUN_KIND = "UserScriptRun";
    private static final String USER_SCRIPT_INFO_KIND = "UserScriptInfo";

    private static final String SCRIPT_PROP = "script";
    private static final String DATE_PROP = "date";
    private static final String USER_PROP = "user";
    private static final String WORKING_SCRIPT_LIST_PROP = "workingScriptList";
    private static final String RESULT_PROP = "result";
    
    //private ScriptStorageService scriptStorageService;
    private int recentListSize = 7;

    @Override
    public void trackRunScript(String scriptName, String scriptContent, Object result) {
        UserService userService = UserServiceFactory.getUserService();
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        User user = userService.getCurrentUser();
        Entity userScriptRunEntity = new Entity(USER_SCRIPT_RUN_KIND);
        userScriptRunEntity.setUnindexedProperty(USER_PROP,  user.getEmail());
        userScriptRunEntity.setUnindexedProperty(DATE_PROP, LocalDateTime.now(DateTimeZone.UTC).toString());
        userScriptRunEntity.setUnindexedProperty(SCRIPT_PROP, new Text(scriptContent));
        
        String resultStr = null;
        if (result instanceof Throwable) {
            try (ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
                        PrintStream out = new PrintStream(baos)) {
                    ((Throwable) result).printStackTrace(out);
                    resultStr = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            } catch (IOException e) {
                logger.log(Level.WARNING, e.getMessage(), e);
            }
        } else {
            resultStr = "SUCCESS";
        }
        
        userScriptRunEntity.setUnindexedProperty(RESULT_PROP, new Text(resultStr));
        Key entityKey = datastoreService.put(userScriptRunEntity);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "User {0} is executing script {1}", new Object[] {user, entityKey});
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void trackWorkingScript(String scriptName) {
        UserService userService = UserServiceFactory.getUserService();
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        User user = userService.getCurrentUser();
        
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "User {0} is working with script {1}", new Object[] {user, scriptName});
        }
        
        Entity userScriptInfoEntity = null;
        List<String> recentWorkingScripts = null;
        try {
            userScriptInfoEntity = datastoreService.get(KeyFactory.createKey(USER_SCRIPT_INFO_KIND, user.getEmail()));
            recentWorkingScripts = (List<String>) userScriptInfoEntity.getProperty(WORKING_SCRIPT_LIST_PROP);
            if (recentWorkingScripts == null) {
                recentWorkingScripts = new ArrayList<>();
            }
            
        } catch (EntityNotFoundException e) {
            userScriptInfoEntity = new Entity(USER_SCRIPT_INFO_KIND, user.getEmail());
            recentWorkingScripts = new ArrayList<>();
        }
        if (!recentWorkingScripts.contains(scriptName)) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Add script {0} to User {1} working list", new Object[] {scriptName, user});
            }
            recentWorkingScripts.add(scriptName);
            if (recentWorkingScripts.size() >= recentListSize) {
                int lastIndex = recentWorkingScripts.size();
                recentWorkingScripts = recentWorkingScripts.subList(lastIndex - recentListSize, lastIndex);
            }
            userScriptInfoEntity.setUnindexedProperty(WORKING_SCRIPT_LIST_PROP, recentWorkingScripts);
            datastoreService.put(userScriptInfoEntity);
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Script {0} is already in User {1} working list", new Object[] {scriptName, user});
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public String getLastEditedScript() {
        UserService userService = UserServiceFactory.getUserService();
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        User user = userService.getCurrentUser();
        Entity userScriptInfoEntity = null;
        try {
            userScriptInfoEntity = datastoreService.get(KeyFactory.createKey(USER_SCRIPT_INFO_KIND, user.getEmail()));
            List<String> recentWorkingScripts = (List<String>) userScriptInfoEntity.getProperty(WORKING_SCRIPT_LIST_PROP);
            
            return recentWorkingScripts.get(recentWorkingScripts.size() - 1);
            
        } catch (EntityNotFoundException e) {
            //userScriptInfoEntity = new Entity(USER_SCRIPT_INFO_KIND, user.getEmail());
            //userScriptInfoEntity.setUnindexedProperty(WORKING_SCRIPT_LIST_PROP, new ArrayList<>());
            //datastoreService.put(userScriptInfoEntity);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getRecentFiles() {
        UserService userService = UserServiceFactory.getUserService();
        DatastoreService datastoreService = DatastoreServiceFactory.getDatastoreService();
        User user = userService.getCurrentUser();
        Entity userScriptInfoEntity = null;
        try {
            userScriptInfoEntity = datastoreService.get(KeyFactory.createKey(USER_SCRIPT_INFO_KIND, user.getEmail()));
            List<String> recentWorkingScripts = (List<String>) userScriptInfoEntity.getProperty(WORKING_SCRIPT_LIST_PROP);
            Collections.reverse(recentWorkingScripts);
            return recentWorkingScripts;
        } catch (EntityNotFoundException e) {
            //userScriptInfoEntity = new Entity(USER_SCRIPT_INFO_KIND, user.getEmail());
            //userScriptInfoEntity.setUnindexedProperty(WORKING_SCRIPT_LIST_PROP, new ArrayList<>());
            //datastoreService.put(userScriptInfoEntity);
        }
        return Collections.emptyList();
    }

    public int getRecentListSize() {
        return recentListSize;
    }

    public void setRecentListSize(int recentListSize) {
        this.recentListSize = recentListSize;
    }

}
