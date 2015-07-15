package com.severn.script.service.command;

import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class SpringCommandFactoryImpl implements CommandFactory, ApplicationContextAware {

    private ApplicationContext applicationContext;
    
    @Override
    public RequestCommand build(Map<String, String[]> parameterMap) {
        String[] command = parameterMap.get(COMMAND_PARAM);
        if (command == null || command.length == 0 || command[0] == null) {
            throw new UnsupportedOperationException("Command " + String.valueOf(command) + " is not implemented");
        }
        try {
            return (RequestCommand) applicationContext.getBean(command[0] + COMMAND_BEAN_SUFFIX, parameterMap);
        } catch (BeansException e) {
            throw new UnsupportedOperationException(e);
        }
        
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
