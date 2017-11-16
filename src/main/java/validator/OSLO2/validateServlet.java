package validator.OSLO2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.resultset.ResultsFormat;
import org.apache.jena.util.FileUtils;
import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;


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
	 	RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/WEB-INF/result.jsp");
	    dispatcher.forward(request, response);
	}
	

	
	
	
	/**
     * Validate the data and return the validationReport
     * @param request 
     * 			The information provided with the request.
	 * @throws ServletException 
	 * @throws IOException 
     */
	private ValidationReport validate (HttpServletRequest request) throws IOException, ServletException {
		//Get option selected in the drop down
		String shapesOption = IOUtils.toString(request.getPart("shapes").getInputStream(), "UTF-8");
		
		// Get rules for validation from file. Get value from drop down and corresponding shapes file from server.
		Model shapesModel = getShapesModel(shapesOption, request.getPart("shapes").getInputStream());
		
		
		// Get data to validate from file, and combine with the vocabulary
		// Check whether a file or a URI was provided.
		Model dataModel = JenaUtil.createMemoryModel();
		if (request.getPart("data") == null) {
			// If URI
			dataModel = getDataModel(shapesOption, null, 
					request.getPart("dataURI").getInputStream(), shapesModel);
		} else {
			// If file
			dataModel = getDataModel(shapesOption, request.getPart("data").getInputStream(), 
					null, shapesModel);
		}
		
		// Perform the validation of data, using the shapes model. Do not validate any shapes inside the data model.
		Resource resource = ValidationUtil.validateModel(dataModel, shapesModel, false);
		Model reportModel = resource.getModel();
		reportModel.setNsPrefix("sh", "http://www.w3.org/ns/shacl#");
		
		// Get the ttl Result and Table Result
		List<ValidationResult> validationResultsList = formatOutput(reportModel);
		String ttlResult = ModelPrinter.get().print(reportModel);
		
		// Get the data and SHACL as string from their models
		String data = getStringFromModel(dataModel);
		String shapes = getStringFromModel(shapesModel);

		// Create the validationReport, including the result, the data validated, and the rules used during validation
		ValidationReport validationReport = new ValidationReport(validationResultsList, ttlResult, data, shapes);
		
		return validationReport;
	}
	
	
	
	/**
     * Get data to validate from file, and combine with the vocabulary
     * @param fileContentData 
     * 			The information provided with the request.
     * @param fileContentDataURI 
     * 			The information provided with the request.
	 * @throws IOException 
     */
	private Model getDataModel(String shapesOption, InputStream fileContentData, InputStream fileContentDataURI, Model shapesModel) throws IOException{
		String dataString;
		
		// Check whether a file or a URI was provided.
		if (fileContentData == null) {
			// If URI
			dataString = getText(IOUtils.toString(fileContentDataURI, "UTF-8"));
		} else {
			// If file
			dataString = IOUtils.toString(fileContentData, "UTF-8");
		}
		
		//Upload the data in the Model. First set the prefixes of the model to those of the shapes model to avoid mismatches.
		Model dataModel = JenaUtil.createMemoryModel();
		Map<String, String> shapesPrefixes = shapesModel.getNsPrefixMap();
		dataModel.setNsPrefixes(shapesPrefixes);
		dataModel.read(IOUtils.toInputStream(dataString, "UTF-8"), null, FileUtils.langTurtle);
		
		// Upload the correct vocabulary to the model
		String vocStr = getText(config.getServer() + shapesOption + "-vocabularium.ttl");
		dataModel.read(IOUtils.toInputStream(vocStr, "UTF-8"), null, FileUtils.langTurtle);

		return dataModel;
	}
	
	
	
	
	/**
     * Get rules for validation from file. Get value from drop down and corresponding shapes file from server.
     * @param shapesOption 
     * 			Option selected in the drop down
     * @param shapesStream 
     * 			The information provided with the request.
	 * @throws IOException 
     */
	private Model getShapesModel(String shapesOption, InputStream shapesStream) throws IOException{
		// Get the corresponding SHACL file from the server
		String shapes = getText(config.getServer() + shapesOption +"-SHACL.ttl");
		// Create Model
		Model shapesModel = JenaUtil.createMemoryModel();
		shapesModel.read(IOUtils.toInputStream(shapes, "UTF-8"), null, FileUtils.langTurtle);
		
		return shapesModel;
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
	 * Method filters the given result using sparql and provides byte array stream containing the result of
	 * the sparql query using the provided format. The different formats are created using standard jena
	 * ResultSetFormatter
	 * 
	 * @param result
	 *            Result from shacl validation
	 * @param queryStr
	 *            String containing sparql query
	 * @param format
	 *            String switching output format, currently only provided TXT, CSV,
	 *            XML
	 * @return ByteArrayOutputStream
	 * @throws IOException
	 *             Writing to stream might cause problems
	 */
	private static ByteArrayOutputStream formatOutput(Model result, String queryStr, String format) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		Query query = QueryFactory.create(queryStr);

		QueryExecution qe = QueryExecutionFactory.create(query, result);

		ResultSet queryResult = qe.execSelect();

		queryResult.getResourceModel().setNsPrefix("sh", "http://www.w3.org/ns/shacl#");
		if (format.matches("(CSV)")) {
			ResultSetFormatter.output(outputStream, queryResult, ResultsFormat.FMT_RS_CSV);
		} else if (format.matches("(XML)")) {
			ResultSetFormatter.outputAsXML(outputStream, queryResult);
		} else if (format.matches("(TTL)")) {
			ResultSetFormatter.output(outputStream, queryResult, ResultsFormat.FMT_RDF_TTL);
		} else {
			// output txt
			String resultOutput = ResultSetFormatter.asText(queryResult);
			if (resultOutput != null) {
				outputStream.write(resultOutput.getBytes());
			}
		}
		// close this resource
		qe.close();

		return outputStream;

	}
	
	
	/**
	 * Method to create a list of validation results from the validationModel
	 * 
	 * @param model
	 *            The ValidationModel which should be converted
	 * @throws IOException
	 *             Writing to stream might cause problems
	 */
	private static List<ValidationResult> formatOutput(Model model) throws IOException {
		// formating result using query to CSV
		InputStream is = validateServlet.class.getResourceAsStream("/defaultquery.rq");
		String queryStr = FileUtils.readWholeFileAsUTF8(is);
		ByteArrayOutputStream resultStream = formatOutput(model, queryStr, "CSV");
		String output = resultStream.toString();
		resultStream.close();
		
		// Split the CSV string on newline character, add to ArrayList and remove empty lines
    	List<String> linesList = Arrays.asList(output.split("[\\n\\r]"));
    	ArrayList<String> lines = new ArrayList<String>();
    	lines.addAll(linesList);
    	lines.removeAll(Arrays.asList(""));
    	
    	// Replace empty values with NA. 
    	// If last value is empty, this cannot be changed. It is handled later
    	List<List<String>> items = new ArrayList<>();
    	for (int i = 0; i < lines.size(); i++) {
    		while (lines.get(i).contains(",,")) {
    			lines.set(i, lines.get(i).replaceAll(",,", ",NA,"));
    		}
    	}
    	
    	// Split each line on "," and if the last value was empty, complete with NA
    	List<String> firstLineSplit = new ArrayList<>(Arrays.asList((lines.get(0).split("\\s*,\\s*"))));
    	int size = firstLineSplit.size();
    	for (int i = 0; i < lines.size(); i++) {
    		List<String> splittedLine = new ArrayList<>(Arrays.asList((lines.get(i).split("\\s*,\\s*"))));
    		if (splittedLine.size() != size ) {
    			splittedLine.add("NA");
    		}
    		items.add(splittedLine);
    	}
    	
    	// Load into a List of ValidationResults
    	List<ValidationResult> validationList = new ArrayList<ValidationResult>();
    	//Start at 1 because the first row contains the headers and we do not want to include those.
    	for (int j = 1; j < items.size(); j++) {
    		ValidationResult validationResult = new ValidationResult();
    		validationResult.setFocusNode(items.get(j).get(0));
    		validationResult.setResultMessage(items.get(j).get(1));
    		validationResult.setResultPath(items.get(j).get(2));
    		validationResult.setResultSeverity(items.get(j).get(3));
    		validationResult.setSourceConstraint(items.get(j).get(4));
    		validationResult.setSourceConstraintComponent(items.get(j).get(5));
    		validationResult.setSourceShape(items.get(j).get(6));
    		validationResult.setValue(items.get(j).get(7));
    		
    		validationList.add(validationResult);
    		
    	}
		
		return validationList;
	}
	
	/**
	 * Method to get the content of a model, in TTL
	 * 
	 * @param model
	 *            The model to get the content from
	 */
	private static String getStringFromModel(Model model) {
		StringWriter out = new StringWriter();
		model.write(out, "TTL");
		String result = out.toString();
		
		return result;
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
	
    
    
    
    
//	/**
//     * Fill in the Graph URI in the rules file.
//     * @param graphData The graph URI to be filled in (of the data graph).
//     * @param graphVoc The graph URI to be filled in(of the vocabulary graph).
//     * @param Rules The SPARQL query in which the session ID should be filled in.
//     */
//    private String fillInGraphURIs(String graphData, String graphVoc, String rules) {
// 
//		rules = rules.replaceAll("<graphURI1>", "<http://" + graphVoc + ">");
//		rules = rules.replaceAll("<graphURI2>", "<http://OSLOvalidator/" + graphData + ">");
//		return rules;
//		
//	}
	
	
//	/**
//     * Perform SPARQL query on the file to validate it.
//     * @param databaseURI The server against which to perform the query.
//     * @param rules The SPARQL query.
//     */
//	private Model executeSPARQLquery(String databaseURI, String rules) {
//		// Execute SPARQL query
//        QueryExecution qe = QueryExecutionFactory.sparqlService(databaseURI, rules);
//    	Model results = qe.execConstruct();
//
//		return results;	
//	}
	
	
//	/**
//  * Upload the file to the server via a HTTP POST request
//  * @param file The file as a string.
//  * @param database The server to which the file should be uploaded.
//  * @param SessionID The session ID. This will also determine the graph URI.
//  * @param username The user name of the server.
//  * @param password The password of the server.
//  */
//	private static void httpPOST(String file, String databaseURL, String sessionID, String username, String password) {
//		String url = null;
//		try {
//			// Set credentials for server
//			CredentialsProvider credsProvider = new BasicCredentialsProvider();
//			credsProvider.setCredentials(
//               new AuthScope(AuthScope.ANY_HOST, AuthScope.ANY_PORT, "SPARQL"),
//               new UsernamePasswordCredentials(username, password));
//			
//			// Create HTTP client
//			CloseableHttpClient client = HttpClients.custom()
//	                  .setDefaultCredentialsProvider(credsProvider)
//	                  .build();
//			
////			// Proxy settings for debugging purposes. Does not need to be enabled for final version.
////			URI proxyURI = new URI("http://localhost:8888");
////			URI targetURI = new URI("http://localhost:8890");
//			
////			HttpHost proxy = new HttpHost(proxyURI.getHost(), proxyURI.getPort(), proxyURI.getScheme());
////			HttpHost target = new HttpHost(targetURI.getHost(), targetURI.getPort(), targetURI.getScheme());
////			
////			RequestConfig config = RequestConfig.custom()
////                 .setProxy(proxy)
////                 .build();
//			
////			// configure the url to upload to and create POST request
////			if ( databaseURL.substring(databaseURL.length() - 1).equalsIgnoreCase("/")) {
////				databaseURL = databaseURL.substring(0, databaseURL.length() - 1);
////			}
//			
//			url = databaseURL + "OSLOvalidator/" + sessionID;
//			HttpPost request = new HttpPost(url);
////			request.setConfig(config);
//			
//			// Set the content and headers of the POST
//			HttpEntity entity = new ByteArrayEntity(file.getBytes("UTF-8"));   // Encoding
//			request.setEntity(entity);
//			request.setHeader(HttpHeaders.ACCEPT, "*/*");
//			request.setHeader(HttpHeaders.EXPECT, "100-continue");
//	        
//			// Execute the POST and print the response if not successful.
//			CloseableHttpResponse response = client.execute(request);   // IOException
//			if (! (response.getStatusLine().getStatusCode() == 200 || response.getStatusLine().getStatusCode() == 201 )) {
//				// Throw Exception using SOAP Fault Message 
//					throw new RuntimeException("Upload of the file did not succeed");
//			}
//			client.close();    // IO Exception
//		} catch (UnsupportedEncodingException e) {
//			e.printStackTrace();
//			// Throw Exception using SOAP Fault Message 
//			
//			throw new RuntimeException("Upload of the file did not succeed");
//		}  catch (IOException e) {
//			e.printStackTrace();
//			// Throw Exception using SOAP Fault Message 
//			
//			throw new RuntimeException("Upload of the file did not succeed");
//		}
//	}

}
