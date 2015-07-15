package com.severn.script.service.domain;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class Script {

    private String scriptName;
    private String scriptEtag;
    private String scriptContent;
    
    public Script() {}
    
    public Script(String scriptName, String scriptEtag, String scriptContent) {
        this.setScriptName(scriptName).setScriptEtag(scriptEtag).setScriptContent(scriptContent);
    }

    public String getScriptName() {
        return scriptName;
    }

    public Script setScriptName(String scriptName) {
        this.scriptName = scriptName;
        return this;
    }

    public String getScriptEtag() {
        return scriptEtag;
    }

    public Script setScriptEtag(String scriptEtag) {
        this.scriptEtag = scriptEtag;
        return this;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public Script setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
        return this;
    }

}
