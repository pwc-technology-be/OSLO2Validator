package validator.OSLO2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.GregorianCalendar;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;


/**
 * Servlet implementation class validateServlet
 */
@WebServlet
@MultipartConfig
public class validateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String username;
	String password;
	String databaseURL;
       
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
	public validateServlet() {
        super();
    }

	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		getConfigurationValues();
		
		response.getWriter().append("Served at: ").append(request.getContextPath());
		response.setContentType("text/html;charset=UTF-8");
		
		// validate the data and write to validationReport
		ValidationReport validationReport = validate(request);
		
		// Store validationReport to the request attribute before forwarding
		request.setAttribute("report", validationReport);
		
        // Forward to /WEB-INF/views/validatedView.jsp
	 	RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/result.jsp");
	    dispatcher.forward(request, response);
	}
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	
	/**
     * Validate the data and return the validationReport
     * @param request The information provided with the request.
     */
	private ValidationReport validate (HttpServletRequest request) throws ServletException, IOException {
		// Get data to validate from file
		InputStream fileContentData = request.getPart("data").getInputStream();
		// Upload the file to the database using a HTTP POST request.
		String dataString = IOUtils.toString(fileContentData);
		httpPOST(dataString, getDatabaseURL(), "001", getUsername(), getPassword());
		Model dataModel = JenaUtil.createMemoryModel();
		dataModel.read(IOUtils.toInputStream(dataString, "UTF-8"), "urn:dummy", FileUtils.langTurtle);
		
		
//		String data = executeSPARQLquery("http://localhost:8890/sparql", "SELECT ?s ?p ?o WHERE { GRAPH <http://localhost:8890/persoonL> { ?s ?p ?o }}");
		
		// Get rules for validation from file. Get value from dropdown and corresponding shapes file from server.
		InputStream shapesStream = request.getPart("shapes").getInputStream();
		String shapesOption = IOUtils.toString(shapesStream);				
		String shapes = getText("http://52.50.205.146:8890/SHACLvalidatorOSLO2/" + shapesOption +"-SHACL.ttl");
		Model shapesModel = JenaUtil.createMemoryModel();
		shapesModel.read(IOUtils.toInputStream(shapes, "UTF-8"), null, FileUtils.langTurtle);
		
		// Perform the validation of data, using the shapes model. Do not validate any shapes inside the data model.
		Resource report = ValidationUtil.validateModel(dataModel, shapesModel, false);
		String result = ModelPrinter.get().print(report.getModel());

		// Create the validationReport, including the result, the data validated, and the rules used during validation
		ValidationReport validationReport = new ValidationReport(result, fileContentData.toString(), 
				shapes);
		
		return validationReport;
	}
	
	
	
	
	/**
     * Upload the file to the server via a HTTP POST request
     * @param file The file as a string.
     * @param database The server to which the file should be uploaded.
     * @param SessionID The session ID. This will also determine the graph URI.
     * @param username The user name of the server.
     * @param password The password of the server.
     */
	private static void httpPOST(String file, String databaseURL, String sessionID, String username, String password) {
		String url = null;
		try {
			System.out.println(databaseURL + username + password + sessionID);
			// Set credentials for server
			CredentialsProvider credsProvider = new BasicCredentialsProvider();
			credsProvider.setCredentials(
                  new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "SPARQL"),
                  new UsernamePasswordCredentials(username, password));
			
			// Create HTTP client
			CloseableHttpClient client = HttpClients.custom()
	                  .setDefaultCredentialsProvider(credsProvider)
	                  .build();
			
//			// Proxy settings for debugging purposes. Does not need to be enabled for final version.
//			URI proxyURI = new URI("http://localhost:8888");
//			URI targetURI = new URI("http://localhost:8890");
			
//			HttpHost proxy = new HttpHost(proxyURI.getHost(), proxyURI.getPort(), proxyURI.getScheme());
//			HttpHost target = new HttpHost(targetURI.getHost(), targetURI.getPort(), targetURI.getScheme());
//			
//			RequestConfig config = RequestConfig.custom()
//                    .setProxy(proxy)
//                    .build();
			
//			// configure the url to upload to and create POST request
//			if ( databaseURL.substring(databaseURL.length() - 1).equalsIgnoreCase("/")) {
//				databaseURL = databaseURL.substring(0, databaseURL.length() - 1);
//			}
			
			url = databaseURL + sessionID;
			HttpPost request = new HttpPost(url);
//			request.setConfig(config);
			
			// Set the content and headers of the POST
			HttpEntity entity = new ByteArrayEntity(file.getBytes("UTF-8"));   // Encoding
			request.setEntity(entity);
			request.setHeader(HttpHeaders.ACCEPT, "*/*");
			request.setHeader(HttpHeaders.EXPECT, "100-continue");
	        
			// Execute the POST and print the response if not successful.
			CloseableHttpResponse response = client.execute(request);   // IOException
			if (! (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 )) {
				// Throw Exception using SOAP Fault Message 
					throw new RuntimeException("Upload of the file did not succeed");
			}
			client.close();    // IO Exception
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			// Throw Exception using SOAP Fault Message 
			
			throw new RuntimeException("Upload of the file did not succeed");
		}  catch (IOException e) {
			e.printStackTrace();
			// Throw Exception using SOAP Fault Message 
			
			throw new RuntimeException("Upload of the file did not succeed");
		}
	}
	
	
	
	
	/**
     * Perform SPARQL query on the file to validate it.
     * @param databaseURI The server against which to perform the query.
     * @param rules The SPARQL query.
     */
	private String executeSPARQLquery(String databaseURI, String rules) {
		// Execute SPARQL query
        QueryExecution qe = QueryExecutionFactory.sparqlService(databaseURI, rules);
    	ResultSet results = qe.execSelect();
    	
        String result = new String();
        System.out.println(result);
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		try {
			// Get output as CSV in a String
       		ResultSetFormatter.outputAsCSV(stream, results);
        	result = stream.toString("UTF-8");  
		} catch (Exception e) {
        	e.printStackTrace();
			// Throw Exception using SOAP Fault Message 
			
			throw new RuntimeException("SPARQL query did not succeed");
        } finally {
            qe.close();
        }
		return result; 	
		
	}
	
	
	
	/**
     * Downloads the content of a file to a string from a URL.
     * @param fileURL HTTP URL of the file to download.
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
     * Get the current date and time.
     */
	private XMLGregorianCalendar getXMLGregorianCalendarDateTime() throws DatatypeConfigurationException {
        GregorianCalendar calendar = new GregorianCalendar();
        return DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar);
        
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
			inputStream = validateServlet.class.getResourceAsStream(propFileName);
			prop.load(inputStream);
 
			setUsername(prop.getProperty("username"));
			setPassword(prop.getProperty("password"));
			setDatabaseURL(prop.getProperty("databaseURL"));
 
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
    
	private void setUsername(String username) {
		this.username = username;
	}
	
	private String getUsername() {
		return this.username;
	}
	
	private void setPassword(String password) {
		this.password = password;
	}
	
	private String getPassword() {
		return this.password;
	}
	
	private void setDatabaseURL(String databaseURL) {
		this.databaseURL = databaseURL;
	}
	
	private String getDatabaseURL() {
		return this.databaseURL;
	}

}
