package com.severn.script.service.command;

import java.beans.ConstructorProperties;
import java.util.Map;
import java.util.logging.Logger;

import com.severn.script.service.ScriptStorageService;
import com.severn.script.service.UserScriptService;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public abstract class AbstractRequestCommand implements RequestCommand {
    
    protected Logger logger = Logger.getLogger(getClass().getName());

    protected Map<String, String[]> parameterMap;
    
    protected ScriptStorageService scriptStorageService;
    protected UserScriptService userScriptService;

    
    @ConstructorProperties({"parameterMap"})
    public AbstractRequestCommand(Map<String, String[]> parameterMap) {
        this.parameterMap = parameterMap;
    }


    public ScriptStorageService getScriptStorageService() {
        return scriptStorageService;
    }


    public void setScriptStorageService(ScriptStorageService scriptStorageService) {
        this.scriptStorageService = scriptStorageService;
    }


    public UserScriptService getUserScriptService() {
        return userScriptService;
    }


    public void setUserScriptService(UserScriptService userScriptService) {
        this.userScriptService = userScriptService;
    }
    
}
