package controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import entities.Question;
import entities.Questionnaire;
import entities.User;
import entities.Product;
import services.AnswerService;
import services.QuestionService;
import services.QuestionnaireService;
import services.UserServices;
import exceptions.BadWordException;

@WebServlet("/Greetings")
public class GoToGreetings extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/AnswerService")
	private AnswerService aService;
	@EJB(name = "services/QuestionService")
	private QuestionService qService;
	@EJB(name = "services/UserService")
	private UserServices uService;
	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService qsService;
	
	
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
		 * 1.gets user from session
		 * 1.gets marketing answers string from session
		 * 2.gets statistical answers string from ctx
		 * 3.calls questionnaireService.createValidQuestionnaire(user.getID(), questionnaire.getID(), marketingAns, statisticalAns) 
		 * --- !!! qService calls for todayQuestionnaire() (or shall i pass it from the call?? session using)to have the marketing and statistical questions and creates the new answer(answerString, question)
		 * 4.gets review (if not null)
		 * 5. calls reviewService(reviewString, productID) who will get today product (or i pass it from call??) 
		 * 6. send to the greetings page
		 */
		
		
		
		//gets marketing answers
		String[] marketingAnswers = (String[]) request.getSession().getAttribute("marketingAnswers");
		
		//gets statistical answers		
		
		String age =request.getParameter("age"); 
		
		String sex = request.getParameter("sex");
		String level = request.getParameter("level");
		
		String review = request.getParameter("review");


		Product product=(Product)request.getSession().getAttribute("product");
		
		try {
		aService.createValidQuestionnaire(user.getId(), product.getID(), marketingAnswers, age, sex, level, review); 
		}catch(BadWordException bw) {
			String path = "/WEB-INF/BlockedAccount.html";
			ServletContext servletContext = getServletContext();
			final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

			templateEngine.process(path, ctx, response.getWriter());
			return;
		}
		


		// Redirect to the Greetings page
		String path = "/WEB-INF/Greetings.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		templateEngine.process(path, ctx, response.getWriter());
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}
	
	

}
