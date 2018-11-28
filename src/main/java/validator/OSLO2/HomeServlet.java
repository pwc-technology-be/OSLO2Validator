package validator.OSLO2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class HomeServlet
 */
@WebServlet
public class HomeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getFactory().getInstance(HomeServlet.class);
	private Configuration config;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeServlet() {
        super();
        try {
        	config = Configuration.loadFromEnvironment();
        	logger.info("Using configuration " + config);
		} catch (IllegalArgumentException e) {
        	logger.fatal(e.getMessage());
        	System.exit(1);
		}
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// Set attribute
        try {
			request.setAttribute("options", config.getApplicationProfiles().keySet());
		} catch (ExecutionException e) {
        	throw new IOException(e.getCause());
		}
    	
		// Forward to /WEB-INF/views/homeView.jsp
		// (Users can not access directly into JSP pages placed in WEB-INF)
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/home.jsp");
		response.setHeader("Location", "validate");
       dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
