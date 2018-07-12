package validator.OSLO2;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
	private LoadingCache<String, List<String>> cache;
       
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
		cache = CacheBuilder.newBuilder()
            .refreshAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(1)
            .build(new CacheLoader<String, List<String>>() {
                @Override
                public List<String> load(String s) throws Exception {
                    if (!"options".equals(s)) throw new IllegalArgumentException("Only options is supported as a key");
                    Scanner scanner = new Scanner(new URL(config.getShaclLocation() + "options.txt").openStream());
                    scanner.useDelimiter("\n");
                    ArrayList<String> options = new ArrayList<>();
                    while (scanner.hasNext()) {
                        options.add(scanner.next().trim());
                    }
                    options.removeAll(Collections.singletonList(""));
                    return options;
                }
            });
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	// Set attribute
        try {
			request.setAttribute("options", cache.get("options"));
		} catch (ExecutionException e) {
        	throw new IOException(e.getCause());
		}
    	
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
}
