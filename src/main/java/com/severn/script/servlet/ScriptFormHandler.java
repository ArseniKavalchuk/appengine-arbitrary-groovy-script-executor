package com.severn.script.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public class ScriptFormHandler extends ViewRequestHandlerSupport {

    private String sampleContent;
    
    @Override
    protected void handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("sampleContent", sampleContent);
        super.handleRequestInternal(request, response);
    }

    public String getSampleContent() {
        return sampleContent;
    }

    public void setSampleContent(String sampleContent) {
        this.sampleContent = sampleContent;
    }

    
}
