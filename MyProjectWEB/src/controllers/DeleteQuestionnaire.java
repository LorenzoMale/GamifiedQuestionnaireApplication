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
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import services.ProductService;
import entities.Product;
import entities.User;

@WebServlet("/DeleteQuestionnaire")
public class DeleteQuestionnaire extends HttpServlet{
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/ProductService")
	private ProductService pService;
	
	
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
		 * 1.call to questionnaireServiec that deletes the questionnaire and the questions related to the product (otherwise cancel also the product with a series of cascadings)
		 * ---!!! CASCADING on question - answers , TRIGGERS for deletion of answers
		 * 2. call to GoToInspection
		 */
		
		
		
		
		
		int prodID = Integer.parseInt(request.getParameter("prodid"));
		
		pService.deleteProduct(prodID);
		
		String path = getServletContext().getContextPath() + "/Inspection";
		response.sendRedirect(path);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {}
}
