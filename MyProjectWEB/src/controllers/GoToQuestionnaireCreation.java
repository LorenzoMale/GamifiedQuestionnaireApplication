package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

import javax.ejb.EJB;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import javax.servlet.annotation.MultipartConfig;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import others.Utils;
import entities.Product;
import entities.User;
import services.ProductService;
import services.QuestionnaireService;

@WebServlet("/Questionnaire")
@MultipartConfig
public class GoToQuestionnaireCreation extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private TemplateEngine templateEngine;
	@EJB(name = "services/ProductService")
	private ProductService pService;
	
	public GoToQuestionnaireCreation() {
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
		if(user.getRole()=='U') {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Page only for admins");
			return;
		}
		
		/*
		 * 1.takes all parameters, sets them as ctx variables
		 * 2.controls num of questions>0
		 * 2.controls the date is today or after
		 * 4.controls there's no questionnaire already set for that day (qservice.controlPossibleDate(date) throws dateNotAvailableException
		 * 3.redirects to productcreation.html
		 */
		
		
		
		//takes all parameters
		String name= (String) request.getParameter("name");
		String description= (String) request.getParameter("description");
		
		
		Part imgFile = request.getPart("image");
		InputStream imgContent = imgFile.getInputStream();
		byte[] imgByteArray = Utils.readImage(imgContent);
		
		int numberOfQuestions = Integer.parseInt(request.getParameter("questionnaireLength"));
		
		
		Date date = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		try {
			java.util.Date tempDate = sdf.parse(request.getParameter("date"));
			 date = new java.sql.Date(tempDate.getTime()) ;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		
		if (name == null | name.isEmpty()  |description == null | description.isEmpty() | numberOfQuestions<1  | imgByteArray.length == 0 | Utils.yesterday().after(date) | !pService.isDateAvailable(date)) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid product parameters");
			return;
		}
		
		request.getSession().setAttribute("name", name);
		request.getSession().setAttribute("description", description);
		request.getSession().setAttribute("date", date);
		request.getSession().setAttribute("image", imgByteArray);
		
		
		
		String path = "/WEB-INF/QuestionnaireCreation.html";
		ServletContext servletContext = getServletContext();
		final WebContext ctx = new WebContext(request, response, servletContext, request.getLocale());

		
		
		ctx.setVariable("numberOfQuestions", numberOfQuestions);
		
		templateEngine.process(path, ctx, response.getWriter());
		
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}


	public void destroy() {}
	
	
}
