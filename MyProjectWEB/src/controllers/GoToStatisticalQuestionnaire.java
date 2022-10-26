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
import entities.Question;
import entities.Questionnaire;
import entities.User;
import services.ProductService;
import services.QuestionService;

@WebServlet("/StatisticalQuestionnaire")
public class GoToStatisticalQuestionnaire extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/ProductService")
	private ProductService pService;
	@EJB(name = "services/QuestionService")
	private QuestionService qService;
	
	
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
		 * 1. gets the answers from htmlpage
		 * 2. sets them to user session
		 */
		
		
		
		
		
		//gets marketing answers from request and sets them to session attribute
		String[] marketingAnswers = request.getParameterValues("marketingAnswers");
		request.getSession().setAttribute("marketingAnswers", marketingAnswers);
		
		
		//gets product and questionnaire statistical questions
		Product product= (Product) request.getSession().getAttribute("product");

		Questionnaire questionnaire= product.getQuestionnaire();
		
		 List<Question> statisticalQuestions = questionnaire.getQuestions().stream().filter(q -> q.isStatistical()).collect(Collectors.toList());



		// Redirect to the statistical part of the questionnaire
		String path = "/WEB-INF/StatisticalQuestionnaire.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("statisticalQuestions", statisticalQuestions);

		templateEngine.process(path, ctx, response.getWriter());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}


}
