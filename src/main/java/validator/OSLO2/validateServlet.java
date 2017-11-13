package validator.OSLO2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
	Configuration config = new Configuration();
       
	
	
    /**
     * @see HttpServlet#HttpServlet()
     */
	public validateServlet() {
        super();
    }
	
	

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Get configuration
		getConfigurationValues();
		
		// Validate the data and write to validationReport
		ValidationReport validationReport = validate(request);
		
		// Store validationReport to the request attribute before forwarding
		request.setAttribute("report", validationReport);
		
        // Forward to /WEB-INF/views/validatedView.jsp
	 	RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/result.jsp");
	    dispatcher.forward(request, response);
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
 
			this.config.setUsername(prop.getProperty("username"));
			this.config.setPassword(prop.getProperty("password"));
			this.config.setDatabaseUploadURL(prop.getProperty("databaseUploadURL"));
			this.config.setDatabaseSPARQLURL(prop.getProperty("databaseSPARQLURL"));
			this.config.setSPARQLURL(prop.getProperty("SPARQLURL"));
 
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

	
	
	
	/**
     * Validate the data and return the validationReport
     * @param request The information provided with the request.
	 * @throws ServletException 
	 * @throws IOException 
     */
	private ValidationReport validate (HttpServletRequest request) throws IOException, ServletException {
		//Get option selected in the drop down
		String shapesOption = IOUtils.toString(request.getPart("shapes").getInputStream());
		
		// Get data to validate from file, and combine with the vocabulary
		Model dataModel = getDataModel(shapesOption, request.getPart("data").getInputStream(), 
				request.getPart("dataURI").getInputStream());
		
		// Get rules for validation from file. Get value from drop down and corresponding shapes file from server.
		Model shapesModel = getShapesModel(shapesOption, request.getPart("shapes").getInputStream());
		
		// Perform the validation of data, using the shapes model. Do not validate any shapes inside the data model.
		Resource report = ValidationUtil.validateModel(dataModel, shapesModel, false);
		String result = ModelPrinter.get().print(report.getModel());

		// Create the validationReport, including the result, the data validated, and the rules used during validation
		ValidationReport validationReport = new ValidationReport(result, "fileContentData", "shapes");
		
		return validationReport;
	}
	
	/**
     * Get rules for validation from file. Get value from drop down and corresponding shapes file from server.
     * @param shapesOption Option selected in the drop down
     * @param shapesStream The information provided with the request.
	 * @throws IOException 
     */
	private Model getShapesModel(String shapesOption, InputStream shapesStream) throws IOException{
		// Get the corresponding SHACL file from the server
		String shapes = getText("http://52.50.205.146:8890/SHACLvalidatorOSLO2/" + shapesOption +"-SHACL.ttl");
		// Create Model
		Model shapesModel = JenaUtil.createMemoryModel();
		shapesModel.read(IOUtils.toInputStream(shapes, "UTF-8"), null, FileUtils.langTurtle);
		
		return shapesModel;
	}
	
	/**
     * Get data to validate from file, and combine with the vocabulary
     * @param fileContentData The information provided with the request.
     * @param fileContentDataURI The information provided with the request.
	 * @throws IOException 
     */
	private Model getDataModel(String shapesOption, InputStream fileContentData, InputStream fileContentDataURI) throws IOException{
		String dataString;
		
		// Check whether a file or a URI was provided.
		if (IOUtils.toString(fileContentData).isEmpty()) {
			dataString = getText(IOUtils.toString(fileContentDataURI));
		} else {
			dataString = IOUtils.toString(fileContentData);
		}
		
		// Upload the file to the database using a HTTP POST request.
		httpPOST(dataString, config.getDatabaseUploadURL(), "data", config.getUsername(), config.getPassword());
		
		// Get SPARQL query as String. SPARQL Query to request the data and vocabulary (combined)
		String rules = getText(config.getSPARQLURL());
		// Fill in the graph URI's in the FROM statement of the SPARQL query.
		rules = fillInGraphURIs("data", shapesOption + "Vocabularium", rules);
		
		// Get data back, combined with the vocabulary
		Model data = executeSPARQLquery(config.getDatabaseSPARQLURL(), rules);

		return data;
	}
	
	
	/**
     * Fill in the Graph URI in the rules file.
     * @param graphData The graph URI to be filled in (of the data graph).
     * @param graphVoc The graph URI to be filled in(of the vocabulary graph).
     * @param Rules The SPARQL query in which the session ID should be filled in.
     */
    private String fillInGraphURIs(String graphData, String graphVoc, String rules) {
 
		rules = rules.replaceAll("<graphURI1>", "<http://" + graphVoc + ">");
		rules = rules.replaceAll("<graphURI2>", "<http://" + graphData + ">");
		return rules;
		
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
	private Model executeSPARQLquery(String databaseURI, String rules) {
		// Execute SPARQL query
        QueryExecution qe = QueryExecutionFactory.sparqlService(databaseURI, rules);
    	Model results = qe.execConstruct();

		return results;	
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

}
