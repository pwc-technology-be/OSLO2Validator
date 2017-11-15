package validator.OSLO2;

import java.util.List;

public class ValidationReport {
	private String result;
	private String data;
	private String shapes;
	private List<ValidationResult> validationResultList;
	
		
	public ValidationReport() {
		 
	}
	
	public ValidationReport(List<ValidationResult> validationResultList, String result, String data, String shapes) {
		this.validationResultList = validationResultList;
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

	public List<ValidationResult> getValidationResultList() {
		return validationResultList;
	}

	public void setValidationResultList(List<ValidationResult> validationResultList) {
		this.validationResultList = validationResultList;
	}
	 
}