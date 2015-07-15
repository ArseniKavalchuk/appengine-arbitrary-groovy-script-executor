package com.severn.script.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.severn.script.service.command.CommandFactory;
import com.severn.script.service.command.CommandResult;
import com.severn.script.service.command.RequestCommand;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class CommandHandler extends UserAuthenticationRequestHandler {
    
    protected CommandFactory commandFactory;

    @SuppressWarnings("unchecked")
    @Override
    protected void handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Map<String, String[]> parameterMap = new HashMap<>(request.getParameterMap());
        String[] userParam = new String[] {request.getAttribute("user").toString() };
        parameterMap.put("execUser", userParam);
        try {
            RequestCommand command = commandFactory.build(parameterMap);
            CommandResult result = command.execute();
            
            if (result.hasError() == false) {
                response.setCharacterEncoding("UTF-8");
                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(result.getResult(), response.getWriter());
                return;
            } else {
                response.setContentType(MediaType.TEXT_PLAIN_VALUE);
                PrintWriter writer = response.getWriter();
                Throwable error = result.getError();
                if (error instanceof org.codehaus.groovy.control.MultipleCompilationErrorsException) {
                    response.setStatus(488); // custom error ?
                    writer.println(result.getError().getMessage());
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    writer.println(result.getError().getMessage());
                    result.getError().printStackTrace(response.getWriter());
                }
                return;
            }
            
        } catch (Exception e) {
            if (logger.isLoggable(Level.SEVERE)) {
                logger.log(Level.SEVERE, e.getMessage(), e);
            }
            // TODO : parse and send json error
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
    }

    public CommandFactory getCommandFactory() {
        return commandFactory;
    }

    public void setCommandFactory(CommandFactory commandFactory) {
        this.commandFactory = commandFactory;
    }

}
