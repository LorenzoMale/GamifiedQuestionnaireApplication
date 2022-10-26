package controllers;

import java.io.IOException;
import java.util.ArrayList;
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

import entities.Answer;
import entities.Product;
import entities.Question;
import entities.User;

import services.ProductService;
import services.QuestionnaireService;
import services.AnswerService;
import services.UserServices;



@WebServlet("/UserAnswers")
public class GoToUserAnswers extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "services/ProductService")
	private ProductService pService;
	@EJB(name = "services/AnswerService")
	private AnswerService aService;
	@EJB(name = "services/UserService")
	private UserServices uService;
	
	
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
		 * 1. get user ctx variable
		 * 2. get questionnaire ctx/session attribute
		 * 3. call for answer service(user, questionnaire) -> call questionnaire service, gets the questions, for each one call to questionservice (user, question)creates named query with user and question parameter and returns an answer or null(exception needed)
		 * --- !!! nb: answers non mappato oppure lazy su question, troppo costoso
		 */
		
		
		int userid = Integer.parseInt(request.getParameter("userid"));
		
		Product product = (Product) request.getSession().getAttribute("product");
		
		//gets questions
		//List<Question> marketingQuestions=product.getQuestionnaire().getQuestions().stream().filter(x->x.isMarketing()).collect(Collectors.toList());
		//List<Question> statisticalQuestions=product.getQuestionnaire().getQuestions().stream().filter(x->x.isStatistical()).collect(Collectors.toList());

		//gets user answers
		List<Answer> answers=aService.findAnswerByUserAndQuestions(userid, product.getQuestionnaire().getID());
		//List<Answer> statisticalAnswers=aService.findAnswerByUserAndQuestions(userid, product.getID());

		//sets questions and answers variables
		String path = "/WEB-INF/UserAnswers.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("answers", answers);
				
		templateEngine.process(path, ctx, response.getWriter());
		
	
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}

}
