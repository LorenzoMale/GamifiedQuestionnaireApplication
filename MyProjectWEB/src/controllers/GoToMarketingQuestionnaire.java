package controllers;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

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


import entities.Product;
import entities.User;
import entities.Question;
import entities.Questionnaire;
import services.ProductService;
import services.QuestionService;
import services.QuestionnaireService;

@WebServlet("/GoToMarketingQuestionnaire")
public class GoToMarketingQuestionnaire extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/ProductService")
	private ProductService pService;
	@EJB(name = "services/QuestionService")
	private QuestionService qService;
	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService questionnaireService;
	
	
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
		
		/*
		 *1.gets the product from session
		 *2. getMarketingQuestions()
		 *3. setta le domande
		 */
		
		
		//gets product from session
		Product product=(Product) request.getSession().getAttribute("product");
		
		//gets marketing questions (questions can't change after first insertion of questionnaire)
		Questionnaire questionnaire= product.getQuestionnaire();
		
		
		List<Question> marketingQuestions=questionnaire.getQuestions().stream().filter(q -> q.isMarketing()).collect(Collectors.toList());
		
		

		// Redirect to the marketing part of the questionnaire
		String path = "/WEB-INF/MarketingQuestionnaire.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("marketingQuestions", marketingQuestions);

		templateEngine.process(path, ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}


}
