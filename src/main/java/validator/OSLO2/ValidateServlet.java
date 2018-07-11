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

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
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
public class ValidateServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static Log logger = LogFactory.getFactory().getInstance(ValidateServlet.class);
	private Configuration config;

    /**
     * @see HttpServlet#HttpServlet()
     */
	public ValidateServlet() {
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
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
	
	
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
		
		// Get rules for validation from file. Get value from drop down and corresponding shapes file from shaclLocation.
		Model shapesModel = getShapesModel(shapesOption, request.getPart("shapes").getInputStream());
		
		
		// Get data to validate from file, and combine with the vocabulary
		// Check whether a file or a URI was provided.
		Model dataModel = JenaUtil.createMemoryModel();
		dataModel = getDataModel(shapesOption, request, shapesModel);
		
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
	 * @throws ServletException 
     */
	private Model getDataModel(String shapesOption, HttpServletRequest request, Model shapesModel) throws IOException, ServletException{
		String dataString = null;
		String fileName;
		String extension = null;
		
		// Check whether a file or a URI was provided. And determine it's extension.
		if (request.getPart("data") == null) {
			// If URI, do GET
			HttpResponse httpGetResponse = httpGet(request);
			HttpEntity resEntityGet = httpGetResponse.getEntity();  
    	    
			// If response not null
			if (resEntityGet != null) {   	        
    	    	//get the extension from the header
    	        if (httpGetResponse.getHeaders("Content-Type").length > 0){
    	        extension = httpGetResponse.getHeaders("Content-Type")[0].getValue();
    	    	}
    	        //get the actual dataString
    	        dataString = EntityUtils.toString(resEntityGet);
    	    }    	    
    	    
    	    // If extension not found in Content-Type header
    	    if (extension == null) {
    			String urlString = request.getParameter("dataURI");
    			URL url = new URL(urlString);
    			extension = FilenameUtils.getExtension(url.getPath());
    	    }
					
		} else {
			// If file
			dataString = IOUtils.toString(request.getPart("data").getInputStream(), "UTF-8");
			fileName = getSubmittedFileName(request.getPart("data"));
			extension = checkForFileLang(fileName);
		}
		
		
		//Upload the data in the Model. First set the prefixes of the model to those of the shapes model to avoid mismatches.
		Model dataModel = JenaUtil.createMemoryModel();
		Map<String, String> shapesPrefixes = shapesModel.getNsPrefixMap();
		dataModel.setNsPrefixes(shapesPrefixes);
		dataModel.read(IOUtils.toInputStream(dataString, "UTF-8"), null, extension);
		
		// Upload the correct vocabulary to the model
		String vocStr = getText(config.getShaclLocation() + shapesOption + "-vocabularium.ttl");
		dataModel.read(IOUtils.toInputStream(vocStr, "UTF-8"), null, FileUtils.langTurtle);

		return dataModel;
	}
	
	
	
	
	/**
     * Get rules for validation from file. Get value from drop down and corresponding shapes file from shaclLocation.
     * @param shapesOption 
     * 			Option selected in the drop down
     * @param shapesStream 
     * 			The information provided with the request.
	 * @throws IOException 
     */
	private Model getShapesModel(String shapesOption, InputStream shapesStream) throws IOException{
		// Get the corresponding SHACL file from the shaclLocation
		String shapes = getText(config.getShaclLocation() + shapesOption +"-SHACL.ttl");
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
     * Perform an HTTP GET and return a file
     * @param fileURL
     * 			HTTP URL of the file to download.
     */
    private static HttpResponse httpGet(HttpServletRequest request) {
    	//Initialize variable
    	HttpResponse responseGet = null;
    	try {
    	    HttpClient client = new DefaultHttpClient();
    	    String getURL = request.getParameter("dataURI");
    	    HttpGet get = new HttpGet(getURL);
    	    
    	    if (request.getPart("headerKey") != null) {
    	    get.setHeader(request.getParameter("headerKey"), request.getParameter("headerValue"));
    	    }
    	    responseGet = client.execute(get);
    	} catch (Exception e) {
    	    e.printStackTrace();
    	}
    	
		return responseGet;
        
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
		InputStream is = ValidateServlet.class.getResourceAsStream("/defaultquery.rq");
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
     * Get the name of the submitted file.
     */
	private static String getSubmittedFileName(Part part) {
	    for (String cd : part.getHeader("content-disposition").split(";")) {
	        if (cd.trim().startsWith("filename")) {
	            String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
	            return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1); // MSIE fix.
	        }
	    }
	    return null;
	}
	
	
	/**
	 * Method checks file format simply by mapping the file extensions to expected
	 * format.</br>
	 * <ul>
	 * <li>extension .ttl --> FileUtils.langTurtle.</li>
	 * <li>extension .rdf --> FileUtils.langXML.</li>
	 * <li>extension .xml --> FileUtils.langXML.</li>
	 * <li>extension .jsonld --> JSONLD</li>
	 * </ul>
	 * 
	 * @param filename
	 * @return String
	 */
	private static String checkForFileLang(String filename) {
		if (filename.endsWith(".ttl")) {
			return FileUtils.langTurtle;
		} else if (filename.endsWith(".xml") || filename.endsWith(".rdf")) {
			return FileUtils.langXML;
		} else if (filename.endsWith(".jsonld") || filename.endsWith(".json")) {
			return "JSONLD";
		} else {
			return "";
		}
	}
}
