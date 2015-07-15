package com.severn.script.service;

import java.util.List;

import com.severn.script.service.domain.Script;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public interface ScriptStorageService {

    Script saveScript(String scriptName, String content);
    
    void deleteScript(String scriptName);
    
    Script readScript(String scriptName);

    List<Script> listScripts(int page, int count);
    
}
