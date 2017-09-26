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
		
		InputStream fileContentData = request.getPart("data").getInputStream();
		InputStream fileContentShapes = request.getPart("shapes").getInputStream();
		
		Model dataModel = JenaUtil.createMemoryModel();
		dataModel.read(fileContentData, "urn:dummy", FileUtils.langTurtle);
		
		Model shapesModel = JenaUtil.createMemoryModel();
		shapesModel.read(fileContentShapes, "urn:dummy", FileUtils.langTurtle);
		
		// Perform the validation of data, using the shapes model
		Resource report = ValidationUtil.validateModel(dataModel, shapesModel, true);
		
		String reportString = ModelPrinter.get().print(report.getModel());
		System.out.println(reportString);
		
		ValidationReport validationReport = new ValidationReport(reportString);
		request.setAttribute("validation", validationReport);
		
        // Forward to /WEB-INF/views/validatedView.jsp
	 	RequestDispatcher dispatcher = this.getServletContext().getRequestDispatcher("/validatedView.jsp");
	    dispatcher.forward(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
