package validator.OSLO2;

public class Configuration {
	String server;

	public Configuration() {
		 
	   }
	
	public Configuration(String server) {
		this.server = server;
	}
 
	public String getServer() {
		return server;
	}
   
	public void setServer(String server) {
		this.server = server;
	}

}
