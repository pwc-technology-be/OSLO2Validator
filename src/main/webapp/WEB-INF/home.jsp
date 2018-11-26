<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>
<!DOCTYPE html>
<html lang="nl">
	<head>
		<jsp:include page="header.jsp"></jsp:include>
		<title>OSLO2 Validator</title>
		<script src="https://code.jquery.com/jquery-3.3.1.js"></script>
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
					  
					            <div class="typography">
					              	<!--Put Content for first tab here-->
					        		 <form method="POST" action="validate" enctype="multipart/form-data" onsubmit="return validateForm1()">
					        	    	<h2 class="h2">OSLO² applicatie profiel</h2>
					        	    	Selecteer hieronder het OSLO² applicatie profiel waartegen u uw data wil valideren.
					        	    	<br/>
					        	    	<select class="select" name="shapes">
					        	    		<c:forEach items="${options}" var="option">
						                		<option>${option}</option>
						                	</c:forEach>
					        	    	</select>
					        	    	<h2 class="h2">Data</h2>
								       	Selecteer hieronder het bestand dat u wil valideren.<br/>
								        
								        <!-- component -->
								        <div class="upload js-upload"
								          data-upload-t-close="Sluiten" id="doc_container">
								          <div class="upload__element">
								            <input class="upload__element__input" type="file" id="data" name="data"
								            data-upload-error-message-filesize="Het bestand mag max :maxFsz zijn."
								            data-upload-max-size="20000000" accept=".ttl, .rdf, .xml, .json, .jsonld" />
								            <label class="upload__element__label" for="data">
								              <i class="vi vi-paperclip"></i><span>Bijlage toevoegen</span>
								            </label>
								          </div>
								          <div id='deactivate' style='cursor:not-allowed; width:100%; height:52px; background-color:#999925; position:relative; bottom:51px; z-index:10; display:none;'></div>
								          <span id="file-name"></span>
								          <span id="datafileerror" class="errormessage"></span>
								        </div>
								        <!-- end component -->
								        
								        <h2 class="h2">Valideer</h2>
								       	<input type="submit" class="button" value="Klik hier om te valideren" name="upload" id="upload" />
								    </form>
					            </div>
					          </section>
					
					          <section class="col--1-1 tab__pane" data-tab-pane>
					            <div class="typography">
					              	<!--Put Content for second tab here-->
					        		<form method="POST" action="validate" enctype="multipart/form-data" onsubmit="return validateForm2()">
					        	    	<h2 class="h2">OSLO² applicatie profiel</h2>
					        	    	Selecteer hieronder het OSLO² applicatie profiel waartegen u uw data wil valideren.
					        	    	<br/>
					        	    	<select class="select" name="shapes">
					        	    		<c:forEach items="${options}" var="option">
						                		<option>${option}</option>
						                	</c:forEach>
					        	    	</select>
					        	    	<h2 class="h2">Data</h2>
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
							          	
								       	<h2 class="h2">Valideer</h2>
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
	 	<script src="./js/errors.js"></script>
	</body>
</html>
