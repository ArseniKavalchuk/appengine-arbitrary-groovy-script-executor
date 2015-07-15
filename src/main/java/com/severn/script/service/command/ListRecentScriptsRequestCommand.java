package com.severn.script.service.command;

import java.util.Map;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class ListRecentScriptsRequestCommand extends AbstractRequestCommand {
    
    public ListRecentScriptsRequestCommand(Map<String, String[]> parameterMap) {
        super(parameterMap);
    }

    @Override
    public CommandResult execute() {
        return CommandResult.Builder.withResult(userScriptService.getRecentFiles());
    }

}
