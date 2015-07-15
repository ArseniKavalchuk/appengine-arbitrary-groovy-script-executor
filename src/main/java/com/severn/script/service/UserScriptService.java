package com.severn.script.service;

import java.util.List;
/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public interface UserScriptService {
    
    void trackRunScript(String scriptName, String scriptContent, Object result);
    
    void trackWorkingScript(String scriptName);

    String getLastEditedScript();
    
    List<String> getRecentFiles();
    
}
