package validator.OSLO2;

public class Configuration {
	String username;
	String password;
	String databaseUploadURL;
	String databaseSPARQLURL;
	String SPARQLURL;

	public Configuration() {
		 
	   }
	
	public Configuration(String username, String password, String databaseURL, String databaseUploadURL, String databaseSPARQLURL,
			String SPARQLURL) {
		this.username = username;
		this.password = password;
		this.databaseUploadURL = databaseUploadURL;
		this.databaseSPARQLURL = databaseSPARQLURL;
		this.SPARQLURL = SPARQLURL;
	}
 
	public String getUsername() {
		return username;
	}
   
	public void setUsername(String username) {
		this.username = username;
	}
   
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDatabaseUploadURL() {
		return databaseUploadURL;
	}

	public void setDatabaseUploadURL(String databaseUploadURL) {
		this.databaseUploadURL = databaseUploadURL;
	}
	
	public String getDatabaseSPARQLURL() {
		return databaseSPARQLURL;
	}

	public void setDatabaseSPARQLURL(String databaseSPARQLURL) {
		this.databaseSPARQLURL = databaseSPARQLURL;
	}
	
	public String getSPARQLURL() {
		return SPARQLURL;
	}

	public void setSPARQLURL(String SPARQLURL) {
		this.SPARQLURL = SPARQLURL;
	}
	
}
