package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringEscapeUtils;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.User;
import services.UserServices;

@WebServlet("/Registration")
public class Registration extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/UserServices")
	private UserServices userService;
	
	public Registration() {
		super();
	}
	
	public void init() throws ServletException {
		ServletContext servletContext = getServletContext();
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// obtain and escape params
		String email = null;
		String psw = null;
		String pswConfirm = null;
		
		ServletContext servletContext = getServletContext();
		String path;
		
		
		try {
			email = (request.getParameter("email"));
			psw = (request.getParameter("pswReg"));
			pswConfirm = (request.getParameter("pswConfirm"));
			
			//regular expression for the E-mail
			String regex = "^[\\w-_\\.+]*[\\w-_\\.]\\@([\\w]+\\.)+[\\w]+[\\w]$";
		    
			if (email == null || psw == null || pswConfirm == null || email.isEmpty() || psw.isEmpty() || pswConfirm.isEmpty() || !email.matches(regex)) {
				throw new Exception("Input is not valid");
			}else if(!psw.equals(pswConfirm)) {
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());	
				ctx.setVariable("RegistrationErrorMsg", "Passwords don't match!");
				path = "/index.html";
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}

		} catch (Exception e) {
			// for debugging only e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing credential value");
			return;
		}
		
		User user=null;
		try {
			// query db to authenticate for user
			user = userService.checkExistentEmail(email);
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Could not check credentials");
			return;
		}
		// If the user exists, add info to the session and go to home page, otherwise
				// show login page with error message

	
		if (user != null) {
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());	
			ctx.setVariable("RegistrationErrorMsg", "Email already in use!");
			path = "/index.html";
			templateEngine.process(path, ctx, response.getWriter());
		} else {
			
			//new user creation + continue to the product questionnaire
			//1. creare un nuovo user e salvarlo
			user = userService.createUser(email, psw);
			System.out.println(user.getUsername() + user.getId());
			//3. 
			request.getSession().setAttribute("user", user);
			path = getServletContext().getContextPath() + "/Home";
			response.sendRedirect(path);
		}

	}

}
