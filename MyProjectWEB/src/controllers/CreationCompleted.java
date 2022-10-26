package controllers;

import java.io.IOException;
import java.sql.Date;
import java.util.List;
import java.util.ArrayList;


import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.User;
import services.QuestionnaireService;



@WebServlet("/Create")
public class CreationCompleted extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService qService;

	
	public CreationCompleted() {
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
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
	
		// If the user is not logged in redirect to the login, if user is blocked redirect to blockedAccount page
		String loginpath = getServletContext().getContextPath() + "/index.html";
		String blockedpath = getServletContext().getContextPath() + "/index.html";
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}
		User user = (User) session.getAttribute("user");
		if(user.getRole()=='B') {
			response.sendRedirect(blockedpath);
			return;
		}
		if(user.getRole()=='U') {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Page only for admins");
			return;
		}
	
		
		
		/*
		 * 1.gets all product parameters
		 * 2.gets all question strings
		 * 3.call to qService.createQuestionnaire(productparameters + quest date) -> create new Product(params), create new Quest(..., product) --- persist(quest) (cascaded)
		 * 4.redirects to gotohome servlet
		 */
		
		
		
		
		String prodName = (String) request.getSession().getAttribute("name");
		System.out.println(prodName);
		String prodDescription = (String) request.getSession().getAttribute("description");
		System.out.println(prodDescription);

		
		Date dateOfPublication = (Date) request.getSession().getAttribute("date");
		byte[] imgByteArray = (byte[]) request.getSession().getAttribute("image");
		
		String[] markQuestions = (String[]) request.getParameterValues("marketingQuestions");
		
		
		qService.createQuestionnaireAndProduct(prodName, prodDescription, dateOfPublication, imgByteArray, markQuestions);

		String path = "/WEB-INF/AdminHome.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		templateEngine.process(path, ctx, response.getWriter());
		return;
	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}
	
}
