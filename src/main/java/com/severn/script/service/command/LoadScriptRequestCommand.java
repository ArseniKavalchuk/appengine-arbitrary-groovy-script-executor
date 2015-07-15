package com.severn.script.service.command;

import java.util.Map;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class LoadScriptRequestCommand extends AbstractRequestCommand {

    private static final String SCRIPT_NAME_PARAM = "scriptName";
    
    public LoadScriptRequestCommand(Map<String, String[]> parameterMap) {
        super(parameterMap);
    }

    @Override
    public CommandResult execute() {
        String scriptName = parameterMap.get(SCRIPT_NAME_PARAM) != null ? parameterMap.get(SCRIPT_NAME_PARAM)[0] : null;
        if (scriptName != null) {
            userScriptService.trackWorkingScript(scriptName);
            return CommandResult.Builder.withResult(scriptStorageService.readScript(scriptName));
        } else {
            return CommandResult.Builder.withError(new UnsupportedOperationException("Invalid scriptName"));
        }
    }

}
