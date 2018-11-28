<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="nl">
	<head>
		<jsp:include page="header.jsp"></jsp:include>
		<title>OSLO2 Validator</title>
		<link href="https://cdnjs.cloudflare.com/ajax/libs/dropzone/5.4.0/min/dropzone.min.css" rel="stylesheet">
		<script src="https://code.jquery.com/jquery-3.3.1.min.js"></script>
		<script>
			$(document).ready(function () {
			       
			  		console.log('hola');
				var target = document.getElementById("doc_container");
				
				var observer = new MutationObserver(function(mutations) {
					if($("#deactivate").css('display') == "none"){
						$("#deactivate").css('display', 'block');
					} else {
						$("#deactivate").css('display', 'none');
					}
				});
				 
				// configuration of the observer:
				var config = { attributes: true, childList: true, characterData: true };
				 
				// pass in the target node, as well as the observer options
				observer.observe(target, config);
			 }); 
		</script>
		<style>
			#datafileerror{margin-left:80px;color:red}
			#file-name{margin-left:80px}
			#urierror{color:red}
		</style>
	</head>
	<body>
		<jsp:include page="banner.jsp"></jsp:include>
	
		<!-- Start page content -->
		<div class="page">
	        <div class="main-content" id="main" role="main" tabindex="-1">
	        	<div class="region">
					<div class="layout layout--wide" id="form">			
						<div class="grid" data-tabs data-tabs-responsive-label="Navigatie">
					          <div class="col--1-1">
					            <div class="tabs__wrapper">
					              <ul class="tabs" data-tabs-list>
					                <li class="tab tab--active">
					                  <a class="tab__link" id="tab-2-nummer-1" href="#" data-tab>Valideer via opladen van bestand</a>
					                </li>
					                <li class="tab">
					                  <a class="tab__link" id="tab-2-nummer-2" href="#" data-tab>Valideer via URL</a>
					                </li>
					              </ul>
					            </div>
					          </div>
				
					          <section class="col--1-1 tab__pane" data-tab-pane>
					  			<h2 class="h2">Valideer via opladen van bestand</h2>
					            <div class="typography">
					              	<!--Put Content for first tab here-->
					        		 <form method="POST" action="validate" enctype="multipart/form-data" class="dropzone" id="my-awesome-dropzone">
					        	    	<h3 class="h3">OSLO² applicatie profiel</h3>
					        	    	Selecteer hieronder het OSLO² applicatie profiel waartegen u uw data wil valideren.
					        	    	<br/>
					        	    	<select class="select" name="shapes">
					        	    		<c:forEach items="${options}" var="option">
						                		<option>${option}</option>
						                	</c:forEach>
					        	    	</select>
					        	    	<h3 class="h3">Data</h3>
								       	Selecteer hieronder het bestand dat u wil valideren.<br/>
								        
								        <!-- component -->
								         <div class="dropzone-previews"></div>
								         <div class="fallback">
    										<input id="data" name="data" type="file" multiple />
  											</div>
								          <span id="file-name"></span>
								          <span id="datafileerror" class="errormessage"></span>
								        <!-- end component -->
								        
								        <h3 class="h3">Valideer</h3>

								       	<button type="submit" class="button" value="Klik hier om te valideren" name="upload" id="upload" >Submit</button>
								    </form>
					            </div>
					          </section>
				
					          <section class="col--1-1 tab__pane" data-tab-pane>
					          <h2 class="h2">VValideer via URL</h2>
					            <div class="typography">
					              	<!--Put Content for second tab here-->
					        		<form method="POST" action="validate" enctype="multipart/form-data" onsubmit="return validateForm2()">
					        	    	<h3 class="h3">OSLO² applicatie profiel</h3>
					        	    	Selecteer hieronder het OSLO² applicatie profiel waartegen u uw data wil valideren.
					        	    	<br/>
					        	    	<select class="select" name="shapes">
					        	    		<c:forEach items="${options}" var="option">
						                		<option>${option}</option>
						                	</c:forEach>
					        	    	</select>
					        	    	<h3 class="h3">Data</h3>
					        	    	Geef hieronder de URL op waarop de data gepubliceerd is die u wilt valideren.<br/>
								       							       	
								       	<!-- input-field component -->          
							          	<label for="input-field" class="form__label">URI </label>
							          	<input class="input-field" id="input-field" type="text" placeholder="" name="dataURI" />
							          	<span id="urierror" class="errormessage"></span>
							          	<br/><p></p>
							          	Geef hieronder eventuele optionele headers mee.
							          	<br/><p></p>
							          	<!-- input-field component -->          
								        <label for="input-field" class="form__label">Header key </label>
								        <input class="input-field" id="input-field2" type="text" placeholder="" name="headerKey" />
								        <!-- input-field component -->          
							          	<label for="input-field" class="form__label">Header value </label>
							          	<input class="input-field" id="input-field3" type="text" placeholder="" name="headerValue" />
							          	
								       	<h3 class="h3">Valideer</h3>
								       	<input type="submit" class="button" value="Klik hier om te valideren" name="upload" id="upload2" />
								    </form>
					            </div>
					          </section>
					
					        </div>
						
						<br/>
						<br/>
	            		
	            		
	        		</div>
	        	</div>
	        </div>
	    </div>
	    <jsp:include page="footer.jsp"></jsp:include>
	    <script src="https://cdnjs.cloudflare.com/ajax/libs/dropzone/5.4.0/min/dropzone.min.js"></script>
	    <script>
	    // Init dropzone instance
	    Dropzone.autoDiscover = false;
	    const myDropzone = new Dropzone('#my-awesome-dropzone', {
	      url: "validate",
	      autoProcessQueue: false
	    });

	    // Submit
	    const $button = document.getElementById('upload')
	    $button.addEventListener('click', function (e) {
	      // Retrieve selected files
	      //var formFields = $('#my-awesome-dropzone').serializeArray();

	       //$.each(formFields, function (i, field) {
	       // 	formData.append(field.name, field.value)
	        //});
	     e.preventDefault();
      	e.stopPropagation();
	      const acceptedFiles = myDropzone.getAcceptedFiles()
	      for (let i = 0; i < acceptedFiles.length; i++) {
	        setTimeout(function () {
	          myDropzone.processFile(acceptedFiles[i])
	        }, i * 2000)
	      }
	      
	      
	    })
	   
	    </script>
	 	<script src="./js/errors.js"></script>
	</body>
</html>
