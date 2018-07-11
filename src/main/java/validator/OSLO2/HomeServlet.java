package validator.OSLO2;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
		// Get drop down values from file
		// TODO: cache this
		String options = getText(config.getShaclLocation() + "options.txt");
		// Split the string on newline character, add to ArrayList and remove empty lines
    	List<String> optionsList = Arrays.asList(options.split("[\\n\\r]"));
    	optionsList.removeAll(Collections.singletonList(""));
    	
    	// Set attribute
    	request.setAttribute("options", optionsList);
    	
		// Forward to /WEB-INF/views/homeView.jsp
		// (Users can not access directly into JSP pages placed in WEB-INF)
		RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/home.jsp");
        
       dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
    
    
    /**
     * Downloads the content of a file to a string from a URL.
     * @param fileURL
     * 			HTTP URL of the file to download.
     */
    private static String getText(String fileURL) {
    	// Initialise variables
    	String ls = System.getProperty("line.separator");
        URL website;
        URLConnection connection;
        BufferedReader in;
        StringBuilder response = new StringBuilder();
        String inputLine;
        
		try {
			// Set up connection
			website = new URL(fileURL);
			connection = website.openConnection();
			// Read file and append per line
			in = new BufferedReader(
			                        new InputStreamReader(
			                            connection.getInputStream()));
			while ((inputLine = in.readLine()) != null) {
			    response.append(inputLine);
				response.append(ls);
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
			// Throw Exception using SOAP Fault Message 
			throw new RuntimeException("Download of the file did not succeed");
		}
        
        return response.toString();
        
    }
}
