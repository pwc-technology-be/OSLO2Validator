package validator.OSLO2;

import java.io.IOException;
import java.io.InputStream;

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
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileUtils;


/**
 * Servlet implementation class validateServlet
 */
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
		response.setContentType("text/html;charset=UTF-8");
		
//		String dataURI = (String) request.getParameter("dataURI");
//		String dataURIString = getText(dataURI);
////		String dataString = readFile(request.getPart("data"));
//		String shapesString = readFile(request.getPart("shapes"));
        
//        ValidatorFiles files = new ValidatorFiles("hallo", "hallo2");
//        request.setAttribute("files", files);
		
		InputStream fileContentData = request.getPart("data").getInputStream();
		InputStream fileContentShapes = request.getPart("shapes").getInputStream();
		
		Model dataModel = JenaUtil.createMemoryModel();
		dataModel.read(fileContentData, "urn:dummy", FileUtils.langTurtle);
		
		// Perform the validation of everything, using the data model
		// also as the shapes model - you may have them separated
		Resource report = ValidationUtil.validateModel(dataModel, dataModel, true);
		
		String reportString = ModelPrinter.get().print(report.getModel());
		System.out.println(reportString);
		
		ValidationReport validationReport = new ValidationReport(reportString);
		request.setAttribute("validation", validationReport);
		
        // Forward to /WEB-INF/views/validatedView.jsp
	 	RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/validatedView.jsp");
	    dispatcher.forward(request, response);
	}
	
//	private String readFile(Part part) throws IOException {
//		String ls = System.getProperty("line.separator");
//		
//        InputStream fileContentData = part.getInputStream();
//       
//        BufferedReader reader = new BufferedReader(new InputStreamReader(fileContentData));
//        StringBuilder out = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            out.append(line);
//            out.append(ls);
//        }
//        reader.close();
//        return out.toString();
//	}
//	
//	 private static String getText(String fileURL) {
//	    	// Initialise variables
//	    	String ls = System.getProperty("line.separator");
//	        URL website = null;
//	        URLConnection connection = null;
//	        BufferedReader in = null;
//	        StringBuilder response = new StringBuilder();
//	        String inputLine;
//	        
//			try {
//				// Set up connection
//				website = new URL(fileURL);
//				connection = website.openConnection();
//				// Read file and append per line
//				in = new BufferedReader(
//				                        new InputStreamReader(
//				                            connection.getInputStream()));
//				while ((inputLine = in.readLine()) != null) {
//				    response.append(inputLine);
//					response.append(ls);
//				}
//				in.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//				// Throw Exception using SOAP Fault Message 
//				throw new RuntimeException("Download of the file did not succeed" + "Tried to download from: " + fileURL);
//			}
//	        
//	        return response.toString();
//	        
//	    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
    
//    protected void processRequest(HttpServletRequest request, HttpServletResponse response) 
//    		throws ServletException, IOException {
//    	
//        response.setContentType("text/html;charset=UTF-8");
//
//        final Part filePart = request.getPart("file");
//        InputStream filecontent = null;
//        final PrintWriter writer = response.getWriter();        
//
//        try {
//            filecontent = filePart.getInputStream();
//            String dataString = IOUtils.toString(filecontent, "UTF-8");
//            System.out.println(dataString);
//            
//
//        } catch (FileNotFoundException fne) {
//            writer.println("You either did not specify a file to upload or are "
//                    + "trying to upload a file to a protected or nonexistent "
//                    + "location.");
//            writer.println("<br/> ERROR: " + fne.getMessage());
//
//            LOGGER.log(Level.SEVERE, "Problems during file upload. Error: {0}", 
//                    new Object[]{fne.getMessage()});
//        } finally {
//            if (filecontent != null) {
//                filecontent.close();
//            }
//            if (writer != null) {
//                writer.close();
//            }
//        }
//    }

}
