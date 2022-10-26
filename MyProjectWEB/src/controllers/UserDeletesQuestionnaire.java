package controllers;

import java.io.IOException;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import entities.Product;
import entities.User;
import services.LeaderboardService;
import services.QuestionnaireService;
import services.UserServices;


@WebServlet("/UserDeletesQuestionnaire")
public class UserDeletesQuestionnaire extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/UserServices")
	private UserServices uService;
	@EJB(name = "services/LeaderboardService")
	private LeaderboardService lService;
	
	
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
		 * 1.get user from session
		 * 2.calls leaderboardService
		 * 
		 * 3.redirects to GoToHome that will send user to the home 
		 */
		
		//gets  product from session
		
		Product product = (Product) request.getSession().getAttribute("product");
		
		//informs db user has cancelled questionnaire
		lService.cancelledQuestionnaire(user.getId(), product.getQuestionnaire().getID());
		uService.updateLastLogin(user.getId());
		
	
		String path =  getServletContext().getContextPath() + "/Home";
		response.sendRedirect(path);
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}
	
	
	
}
