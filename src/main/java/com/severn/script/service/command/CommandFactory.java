package com.severn.script.service.command;

import java.util.Map;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public interface CommandFactory {
    
    public static final String COMMAND_BEAN_SUFFIX = "Command";
    public static final String COMMAND_PARAM = "command";

    RequestCommand build(Map<String, String[]> parameterMap);
    
}
