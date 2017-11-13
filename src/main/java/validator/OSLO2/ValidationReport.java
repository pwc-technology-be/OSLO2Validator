package validator.OSLO2;

public class ValidationReport {
	private String result;
	private String data;
	private String shapes;
	
		
	public ValidationReport() {
		 
	}
	
	public ValidationReport(String result, String data, String shapes) {
		this.result = result;
		this.data = data;
		this.shapes = shapes;
	}
 
	public String getResult() {
		return result;
	}
 
	public void setResult(String result) {
		this.result = result;
	}
   
	public String getData() {
		return data;
	}
 
	public void setData(String data) {
		this.data = data;
	}
   
	public String getShapes() {
		return shapes;
	}
 
	public void setShapes(String shapes) {
		this.shapes = shapes;
	}
	 
}