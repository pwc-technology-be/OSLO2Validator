package validator.OSLO2;

public class Configuration {
	String shaclLocation = "https://data.vlaanderen.be/shacl/";
	private static final String SHACL_LOCATION = "SHACL_LOCATION";

	public static Configuration loadFromEnvironment() {
        Configuration config = new Configuration();
        config.setShaclLocation(System.getenv(SHACL_LOCATION));
        return config;
    }

    @Override
    public String toString() {
	    return "{\"shaclLocation\": \"" + shaclLocation + "\"}";
    }

	public String getShaclLocation() {
		return shaclLocation;
	}
   
	public void setShaclLocation(String shaclLocation) {
		this.shaclLocation = shaclLocation;
        if (null == this.shaclLocation) {
            throw new IllegalArgumentException("shaclLocation must be a valid local path or remote server");
        }
	}

}
