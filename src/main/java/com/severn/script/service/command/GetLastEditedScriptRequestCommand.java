package com.severn.script.service.command;

import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.severn.script.service.domain.Script;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class GetLastEditedScriptRequestCommand extends AbstractRequestCommand {
    
    private final Logger logger = Logger.getLogger(getClass().getName());
    
    public GetLastEditedScriptRequestCommand(Map<String, String[]> parameterMap) {
        super(parameterMap);
    }

    /**
     * We shouldn't fail here?
     */
    @Override
    public CommandResult execute() {
        String scriptName = userScriptService.getLastEditedScript();
        Script script = new Script();
        if (scriptName != null) {
            try {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Trying to load script {1}", scriptName);
                }
                script = scriptStorageService.readScript(scriptName);
            } catch (Exception e) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.log(Level.FINE, "Error loading script {1} ", scriptName);
                }
            }
        }
        return CommandResult.Builder.withResult(script);
    }

}
