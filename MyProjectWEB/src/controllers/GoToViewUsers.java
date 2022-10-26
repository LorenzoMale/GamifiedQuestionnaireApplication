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

import entities.LeaderboardRecord;
import entities.Product;
import entities.User;
import services.QuestionnaireService;
import services.LeaderboardService;
import services.ProductService;

@WebServlet("/ViewUsers")
public class GoToViewUsers extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;

	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "services/ProductService")
	private ProductService pService;
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
			if(user.getRole()=='U') {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Page only for admins");
				return;
			}				
		
		/*
		 * 1. gets the selected questionnaire parameter
		 * 2. questionnaire.getLeaderboard() ritorna list<leaderboard> riguardante quel questionario
		 * 3. leaderboard.getValidQuestUser() (fare un metodo in leaderboard che torna solo gli user con point >0  ritorna lista di user
		 * ---!!! l'oggetto leaderboard in realtà è una riga di classifica, non una classifica
		 * 4. leaderboard.getInvalidQuestionnaire() ritorna lista di user
		 * 5. settare le due variabili List<User>
		 * 6. set (ctx var or session) the questionnaire
		 * 6. redirect to questionnaireUserList.html
		 */
		
		
		int chosenProductid=Integer.parseInt(request.getParameter("prodid"));
		

		//request is coming from inspection.html page
		Product chosenProduct=pService.findProductByID(chosenProductid);
		request.getSession().setAttribute("product", chosenProduct);
	
		
		//list of records containing users who have completed questionnaire
		List<LeaderboardRecord> validRecords = lService.getValidQuestionnairesFromLeaderboard(chosenProductid);
		
		//list of records containing users who have deleted questionnaire
		List<LeaderboardRecord> notValidRecords = lService.getNotValidQuestionnairesFromLeaderboard(chosenProductid);
		
	
		
		
		String path = "/WEB-INF/QuestionnaireUserList.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
		ctx.setVariable("validRecords", validRecords);
		ctx.setVariable("notValidRecords", notValidRecords);


		templateEngine.process(path, ctx, response.getWriter());
		
		
		
		
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}


}
