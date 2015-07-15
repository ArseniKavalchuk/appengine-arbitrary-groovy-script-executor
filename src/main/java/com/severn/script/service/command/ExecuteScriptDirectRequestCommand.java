package com.severn.script.service.command;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.severn.script.service.domain.Script;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class ExecuteScriptDirectRequestCommand extends AbstractRequestCommand implements ApplicationContextAware {

    protected ApplicationContext applicationContext;
    protected String scriptName;
    protected String scriptContent;
    protected String execUser;
    
    public ExecuteScriptDirectRequestCommand(Map<String, String[]> parameterMap) {
        super(parameterMap);
        
        String[] execUserParams = parameterMap.get("execUser");
        if (execUserParams == null || execUserParams.length == 0 || execUserParams[0] == null) {
            throw new IllegalArgumentException("execUser parameter is mandatory");
        }
        this.execUser = execUserParams[0];
        
        String[] scriptNameParams = parameterMap.get("scriptName");
        if (scriptNameParams != null && scriptNameParams.length == 1 && scriptNameParams[0] != null) {
            this.scriptName = scriptNameParams[0];
        }
        
        String[] scriptContentParams = parameterMap.get("scriptContent");
        if (scriptContentParams != null && scriptContentParams.length == 1 && scriptContentParams[0] != null) {
            this.scriptContent = scriptContentParams[0];
        }
        
        if (this.scriptContent == null && this.scriptName == null) {
            throw new IllegalArgumentException("Either scriptName or scriptContent parameter is mandatory");
        }
    }

    @Override
    public CommandResult execute() {
        
        // TODO : log commands!

        long startTime = System.currentTimeMillis();
        Map<String, Object> variables = new HashMap<String, Object>();
        
        if (this.scriptContent != null) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Executing script via scriptContent param");
            }
        } else {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Reading script {0} from storage", this.scriptName);
            }
            variables.put("scriptName", this.scriptName);
            Script scriptObject = scriptStorageService.readScript(this.scriptName);
            this.scriptContent = scriptObject.getScriptContent();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Got script {0} from storage", this.scriptName);
            }
        }
        variables.put("parameterMap", this.parameterMap);
        variables.put("applicationContext", this.applicationContext);
        variables.put("execUser", this.execUser);
        variables.put("logger", this.logger);
        
        groovy.lang.Binding binding = new groovy.lang.Binding(variables);
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Initializing GroovyShell");
        }
        groovy.lang.GroovyShell shell = new groovy.lang.GroovyShell(this.getClass().getClassLoader(), binding);
        groovy.lang.Script script = null;
        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "Parsing script {0}", this.scriptName);
            }
            script = shell.parse(scriptContent);
            
        } catch (final Exception e) {
            logger.log(Level.SEVERE, "Script parse error", e);
            return CommandResult.Builder.withError(e);
        }
        
        if (logger.isLoggable(Level.FINE)) {
            logger.log(Level.FINE, "Executing script {0}", this.scriptName);
        }
        script.setBinding(binding);
        Object result = null;
        try {
            result = script.run();
            long endTime = System.currentTimeMillis();
            if (logger.isLoggable(Level.FINE)) {
                logger.log(
                        Level.FINE,
                        "Script execution time: {0} sec",
                        new Object[] {Long.toString(endTime - startTime)});
            }
            return CommandResult.Builder.withResult(result);
        } catch (Exception e) {
            result = e;
            return CommandResult.Builder.withError(e);
        } finally {
            userScriptService.trackRunScript(scriptName, scriptContent, result);
            // skip this for now, since we asking for 'Save' if script has been changed on the client
            //userScriptService.trackWorkingScript(scriptName);
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
