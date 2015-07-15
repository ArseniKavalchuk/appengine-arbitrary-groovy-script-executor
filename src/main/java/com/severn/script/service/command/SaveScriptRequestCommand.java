package com.severn.script.service.command;

import java.util.Map;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class SaveScriptRequestCommand extends AbstractRequestCommand {

    private static final String SCRIPT_NAME_PARAM = "scriptName";
    private static final String SCRIPT_CONTENT_PARAM = "scriptContent";
    
    public SaveScriptRequestCommand(Map<String, String[]> parameterMap) {
        super(parameterMap);
    }

    @Override
    public CommandResult execute() {
        String scriptName = parameterMap.get(SCRIPT_NAME_PARAM) != null ? parameterMap.get(SCRIPT_NAME_PARAM)[0] : null;
        String scriptContent = parameterMap.get(SCRIPT_CONTENT_PARAM) != null ? parameterMap.get(SCRIPT_CONTENT_PARAM)[0] : null;
        if ((scriptName != null && scriptName.trim().length() != 0 && !"undefined".equalsIgnoreCase(scriptName.trim()))
                && (scriptContent != null/* && scriptContent.length() > 0*/)) {
            try {
                return CommandResult.Builder.withResult(scriptStorageService.saveScript(scriptName, scriptContent));
            } finally {
                userScriptService.trackWorkingScript(scriptName);
            }
        } else {
            return CommandResult.Builder.withError(new UnsupportedOperationException("Invalid scriptName or scriptContent"));
        }
    }

}
