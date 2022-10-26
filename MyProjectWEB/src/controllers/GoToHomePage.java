package controllers;

import java.io.IOException;
import java.util.Comparator;
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

import services.*;
import entities.*;
import exceptions.MissingProductException;

@WebServlet("/Home")
public class GoToHomePage extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/ProductService")
	private ProductService pService;
	@EJB(name = "services/QuestionnaireService")
	private QuestionnaireService qService;
	@EJB(name = "services/LeaderboardService")
	private LeaderboardService lService;
	

	public GoToHomePage() {
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
		HttpSession session = request.getSession();
		if (session.isNew() || session.getAttribute("user") == null) {
			response.sendRedirect(loginpath);
			return;
		}

	

		if(null != request.getSession().getAttribute("marketingAnswers") ) {
		request.getSession().removeAttribute("marketingAnswers");
		}
		/*
		 * 1.gets the user from the login
		 * 2.controls user role(if not null) and redirects him to the right page (home, admin home, blocked user)
		 * 3.sets user to session attribute
		 * 4.
		 * 		a. caso USER :
		 * 			-asks the questionnaire of the day(se c'è, altrimenti pagina di aspettare)
		 *          -controlla in leaderboardService() se esiste già user  REFRESH NEEDED
		 * 			-getProduct
		 * 			-set product infos
		 * 			-set review list
		 * 
		 * ---!!! remember leaderboard is a single table without foreign keys a par from user  --- in alteernative map user to leaderboard for this check
		 * 			-reinvia a pagina Home.html
		 * 
		 * 		b. caso ADMIN:
		 * 			-sends to AdminHome.html
		 * 
		 * 		c. caso BLOCKED:
		 * 			-goes to BlockedUser.html
		 */
		
		

		User user = (User) session.getAttribute("user");
		
	
		

		//controls user role(if not null) and redirects him to the right page (home, admin home, blocked user)
		switch(user.getRole()) {
			case 'A':{ //admin case, redirects to AdminHome.html
				String path = "/WEB-INF/AdminHome.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			case 'B':{ //blocked user, redirects to BlockedAccount.html
				String path = "/WEB-INF/BlockedAccount.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				templateEngine.process(path, ctx, response.getWriter());
				return;
			}
			case 'U':{ //user, default
			//asks for the questionnaire of the day
				Product todayProduct= null;
				
				try {
					todayProduct= pService.getTodayProduct();
			
				}catch(MissingProductException e) {
					//daily questionnaire missing
					String path = "/WEB-INF/QuestionnaireNotAvailable.html";
					ServletContext servletContext = getServletContext();
					final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
					templateEngine.process(path, ctx, response.getWriter());
					return;
				}
				
				//sets product to session attribute
				request.getSession().setAttribute("product", todayProduct);
				
				Questionnaire todayQuestionnaire=todayProduct.getQuestionnaire();
				
				
				
				List<Review> reviews=todayProduct.getReviews();
				
				
				String compilingPermitted="";
				if(!lService.checkUserHasDoneQuestionnaire(user.getId(), todayQuestionnaire.getID())) {
					compilingPermitted="Compile the questionnaire!";
				}
				
				String path = "/WEB-INF/Home.html";
				ServletContext servletContext = getServletContext();
				final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());
				request.getSession().setAttribute("product", todayProduct);
				ctx.setVariable("reviews", reviews);
				ctx.setVariable("compilingPermitted", compilingPermitted);

				templateEngine.process(path, ctx, response.getWriter());
				
					
		}
		
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
	}


}
