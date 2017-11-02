package validator.OSLO2;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.topbraid.shacl.util.ModelPrinter;
import org.topbraid.shacl.validation.ValidationUtil;
import org.topbraid.spin.util.JenaUtil;
import org.apache.commons.io.IOUtils;
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
	
	private ValidationReport validate (HttpServletRequest request) throws ServletException, IOException {
		// Get data to validate from file
		InputStream fileContentData = request.getPart("data").getInputStream();
		Model dataModel = JenaUtil.createMemoryModel();
		dataModel.read(fileContentData, "urn:dummy", FileUtils.langTurtle);
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
     * Perform SPARQL query on the file to validate it.
     * @param databaseURI The server against which to perform the query.
     * @param rules The SPARQL query.
     */
	private String executeSPARQLquery(String databaseURI, String rules) {
		System.out.println("here");
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

}
