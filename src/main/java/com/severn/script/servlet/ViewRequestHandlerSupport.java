package com.severn.script.servlet;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class ViewRequestHandlerSupport extends UserAuthenticationRequestHandler {

    protected String viewTemplate;
    
    @Override
    protected void handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        ServletContext sc = request.getSession().getServletContext();
        sc.getRequestDispatcher(viewTemplate).include(request, response);
    }

    public String getViewTemplate() {
        return viewTemplate;
    }

    public void setViewTemplate(String viewTemplate) {
        this.viewTemplate = viewTemplate;
    }

}
