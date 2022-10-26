package controllers;

import java.io.IOException;
import java.util.List;

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

import services.LeaderboardService;
import services.ProductService;
import services.QuestionnaireService;
import entities.LeaderboardRecord;
import entities.Product;
import entities.Questionnaire;
import entities.User;
import exceptions.MissingProductException;

@WebServlet("/Leaderboard")
public class GoToLeaderBoard extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/LeaderboardService")
	private LeaderboardService lService;
	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService qService;
	

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
		 * 1.gets questionnaire from session product
		 * 2.call lService(questionnaire) -> named query NO CACHE returns list ordered
		 */
		Product todayProduct= (Product) request.getSession().getAttribute("product");
		
		Questionnaire todayQuestionnaire = todayProduct.getQuestionnaire();
		
		List<LeaderboardRecord> leaderboard=null;
		
		leaderboard = lService.getLeaderboardNoCacheNoDeletedQuestionnaires(todayQuestionnaire.getID());
		
		
		
		
		String path = "/WEB-INF/Leaderboard.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("leaderboard", leaderboard);
		if(leaderboard==null || leaderboard.size()==0) {
			ctx.setVariable("emptyLeaderboard", "no one has filled the questionnaire yet!");
		}
	

		templateEngine.process(path, ctx, response.getWriter());
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	
	public void destroy() {
	}
	
}
