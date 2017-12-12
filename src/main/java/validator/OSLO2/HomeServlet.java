package validator.OSLO2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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
	Configuration config = new Configuration();
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public HomeServlet() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get configuration
		getConfigurationValues();
		
    	//Get drop down values from file
    	ArrayList<String> optionsList = new ArrayList<String>();
    	String options = getText(config.getServer() + "options.txt");
		// Split the string on newline character, add to ArrayList and remove empty lines
    	List<String> linesList = Arrays.asList(options.split("[\\n\\r]"));
    	optionsList.addAll(linesList);
    	optionsList.removeAll(Arrays.asList(""));
    	
    	//Set attribute
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
        URL website = null;
        URLConnection connection = null;
        BufferedReader in = null;
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
    
    
    /**
     * Load the configuration settings from the config.properties file.
     */
    private void getConfigurationValues() {
	    InputStream inputStream = null;
		
		try {
			Properties prop = new Properties();
			String propFileName = "/config.properties";
 
//			inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(propFileName);
			inputStream = ValidateServlet.class.getResourceAsStream(propFileName);
			prop.load(inputStream);
 
			this.config.setServer(prop.getProperty("server"));
 
		}	catch (Exception e) {
			e.printStackTrace();
			// Throw Exception using SOAP Fault Message 
			
			throw new RuntimeException("Configuration not loaded");
		} 
		finally {
			try {
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
				// Throw Exception using SOAP Fault Message 
				
				throw new RuntimeException("Configuration not loaded");
			}
		}
		return;
		
	}

}
