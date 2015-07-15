package com.severn.script.service.command;

import java.util.Map;

import com.severn.script.service.ScriptStorageService;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class ListScriptsRequestCommand extends AbstractRequestCommand {

    private ScriptStorageService scriptStorageService;
    private int page = 0;
    private int count = 50;
    
    public ListScriptsRequestCommand(Map<String, String[]> parameterMap) {
        super(parameterMap);
        String[] pageParam = parameterMap.get("page");
        if (pageParam != null && pageParam.length > 0) {
            this.page = Integer.valueOf(pageParam[0]);
        }
        String[] countParam = parameterMap.get("count");
        if (countParam != null && countParam.length > 0) {
            this.count = Integer.valueOf(countParam[0]);
        }
    }
    
    @Override
    public CommandResult execute() {
        return CommandResult.Builder.withResult(
            scriptStorageService.listScripts(ListScriptsRequestCommand.this.page, ListScriptsRequestCommand.this.count)
        );
    }

    public ScriptStorageService getScriptStorageService() {
        return scriptStorageService;
    }

    public void setScriptStorageService(ScriptStorageService scriptStorageService) {
        this.scriptStorageService = scriptStorageService;
    }

}
