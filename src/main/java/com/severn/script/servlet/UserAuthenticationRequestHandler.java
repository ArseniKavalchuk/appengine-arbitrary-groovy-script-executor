package com.severn.script.servlet;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/**
 * 
 * @author Arseny Kovalchuk
 *
 */
public abstract class UserAuthenticationRequestHandler implements HttpRequestHandler {

    protected Logger logger = Logger.getLogger(getClass().getName());
    
    protected String loginTemplate = "/WEB-INF/view/login.jsp";
    
    @Override
    public void handleRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        UserService userService = UserServiceFactory.getUserService();
        
        String thisURL = request.getRequestURI();
        if (request.getUserPrincipal() != null && userService.isUserLoggedIn() && userService.isUserAdmin()) {
            if (logger.isLoggable(Level.FINE)) {
                logger.log(Level.FINE, "User logged in: principal={0}, user={1}", new Object[] {request.getUserPrincipal(), userService.getCurrentUser()});
            }
            // TODO : avatar
            request.setAttribute("user", userService.getCurrentUser());
            request.setAttribute("logoutUrl", userService.createLogoutURL(thisURL));
            handleRequestInternal(request, response);
        } else {
            if (logger.isLoggable(Level.WARNING)) {
                logger.log(Level.WARNING, "Unauthorized: user={0}", userService.getCurrentUser());
            }
            /*
            request.setAttribute("loginUrl", userService.createLoginURL(thisURL));
            ServletContext sc = request.getSession().getServletContext();
            sc.getRequestDispatcher(loginTemplate).forward(request, response);
            */
            response.sendRedirect(userService.createLoginURL(thisURL));
        }
        
    }
    
    protected abstract void handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException;

    public String getLoginTemplate() {
        return loginTemplate;
    }

    public void setLoginTemplate(String loginTemplate) {
        this.loginTemplate = loginTemplate;
    }

}
