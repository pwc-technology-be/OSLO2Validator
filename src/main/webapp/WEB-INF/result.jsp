<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" import="validator.OSLO2.ValidationResult.*" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<jsp:include page="header.jsp"></jsp:include>
	    <title>OSLO2 SHACL Validator Results</title>
		<link href="https://code.jquery.com/ui/1.12.1/themes/base/jquery-ui.css" rel="stylesheet" />
		<link href="https://cdn.datatables.net/1.10.19/css/dataTables.jqueryui.min.css" rel="stylesheet" />
		<link href="https://cdn.datatables.net/responsive/2.2.3/css/responsive.dataTables.min.css" rel="stylesheet" />
		<link href="https://cdn.datatables.net/buttons/1.5.2/css/buttons.dataTables.min.css" rel="stylesheet" />
		<link href="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.41.0/codemirror.min.css" rel="stylesheet" />
		<style>
		.fullscreen-message{font-size:12px;color:grey}
		.CodeMirror, .CodeMirror * { box-sizing: content-box !important; }
		.CodeMirror-fullscreen {
		    position:fixed;
		    top:200px;
		    left:8px;
		    right:0;
		    bottom:0;
		    height:auto;
		    z-index:9;
		    width:99%;
		    padding:0;
		    border-top:3px solid grey;
		    border-bottom:3px solid grey;
		    border-bottom:3px solid grey;
		    border-top-left-radius:10px;
		    border-top-right-radius:10px;
		    border-bottom-left-radius:10px;
		    border-bottom-right-radius:10px;
		}
		</style>
       <!--  <link href="./media/themes/smoothness/jquery-ui-1.7.2.custom.css" rel="stylesheet" type="text/css" media="all" /> -->
		<script src="https://code.jquery.com/jquery-3.3.1.js" type="text/javascript"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/jquery.dataTables.min.js" type="text/javascript"></script>
        <script src="https://cdn.datatables.net/1.10.19/js/dataTables.jqueryui.min.js" type="text/javascript"></script>
        <script src="https://cdn.datatables.net/responsive/2.2.3/js/dataTables.responsive.min.js" type="text/javascript"></script>
        <script src="https://cdn.datatables.net/buttons/1.5.2/js/dataTables.buttons.min.js" type="text/javascript"></script>
        <script src="https://cdn.datatables.net/buttons/1.5.2/js/buttons.html5.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.41.0/codemirror.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.41.0/addon/display/fullscreen.min.js"></script>
        <script src="https://cdnjs.cloudflare.com/ajax/libs/codemirror/5.41.0/mode/turtle/turtle.min.js"></script>
	</head>
	<body>
	
		<jsp:include page="banner.jsp"></jsp:include>
		<!-- Start page content -->
		<div class="page">
			<div class="main-content" id="main" itemprop="mainContentOfPage" role="main" tabindex="-1">
				<section class="region">
					<div class="layout layout--wide">
					
						<form action="home">
							<input type="submit" class="button" value="Terug" />
						</form>
					
				        <div class="grid" data-tabs data-tabs-responsive-label="Navigatie">
				          <div class="col--1-1">
				            <div class="tabs__wrapper">
				              <ul class="tabs" data-tabs-list>
				                <li class="tab tab--active">
				                  <a class="tab__link" id="tab-2-nummer-1" href="#" data-tab>Resultaat in tabel</a>
				                </li>
				                <li class="tab">
				                  <a class="tab__link" id="tab-2-nummer-2" href="#" data-tab>Resultaat in TTL</a>
				                </li>
				                <li class="tab">
				                  <a class="tab__link" id="tab-2-nummer-3" href="#" data-tab>Data</a>
				                </li>
				                <li class="tab">
				                  <a class="tab__link" id="tab-2-nummer-4" href="#" data-tab>Regels</a>
				                </li>
				              </ul>
				            </div>
				          </div>
				          
				         <section class="col--1-1 tab__pane" data-tab-pane>
				            <h1 class="h1">Resultaat in tabel</h1>
				            Hieronder ziet u overzicht van de validatieresultaten.
				            <div class="typography">
				              	<!--Put Content for second tab here-->
				        		<table id="resultaten" class="display">
				                    <thead style="background: linear-gradient(white, rgb(234, 234, 234));">
				                        <tr>
				                            <th>Focus Node</th>
				                            <th>Result Message</th>
				                            <th>Result Path</th>
				                            <th>Result Severity</th>
				                         	<th>Value</th>
				                            <th>Source Constraint</th>
				                            <th>Source Constraint Component</th>
				                            <th>Source Shape</th>

				                        </tr>
				                    </thead>
				                    <tbody>
					                	<c:forEach items="${report.validationResultList}" var="result">
					                		<tr>
					                			<td>${result.focusNode}</td>
					                			<td>${result.resultMessage}</td>
					                			<td>${result.resultPath}</td>
					                			<td>${result.resultSeverity}</td>
				                			    <td>${result.value}</td>
					                			<td>${result.sourceConstraint}</td>
					                			<td>${result.sourceConstraintComponent}</td>
					                			<td>${result.sourceShape}</td>
					                		</tr>
					                	</c:forEach>
				                    </tbody>
				                </table>
				            </div>
				          </section>
				          
				          <section class="col--1-1 tab__pane" data-tab-pane>
				            <h1 class="h1">Resultaat in TTL</h1>
				            Hieronder ziet u een overzicht van de validatieresultaten.
				            <br><br>
				            <span class="fullscreen-message">Voor volledig scherm, klik op het gebied en druk op F11, druk op ESC om terug naar normaal te gaan.</span>
				            <div class="typography">
				              	<!--Put Content for first tab here-->
				        		<form><textarea id="result-ttl" name="result-ttl"><c:out value="${report.result != null ? report.result : 'no report found'}"/></textarea></form>  
				            </div>
				          </section>
				
				          
				
				          <section class="col--1-1 tab__pane" data-tab-pane>
				            <h1 class="h1">Data</h1>
				            Hieronder ziet u een overzicht van de data die gevalideerd werd. Aan de data die u opgaf werd het betreffende vocabularium aan toegevoegd om meer context aan uw bestand toe te voegen en het validatieresultaat te verbeteren.
				            </br></br>
				            <span class="fullscreen-message">Voor volledig scherm, klik op het gebied en druk op F11, druk op ESC om terug naar normaal te gaan.</span>
				            <div class="typography">
				            	<!--Put Content for third tab here-->
				        		<form><textarea id="result-data" name="result-data"><c:out value="${report.data != null ? report.data : 'no report found'}"/></textarea></form> 
				          	</div>
				          </section>
				          
				          <section class="col--1-1 tab__pane" data-tab-pane>
				            <h1 class="h1">Regels</h1>
				            Hieronder ziet u een overzicht van de SHACL regels die gebruikt werden om uw data te valideren. Deze SHACL regels zijn gebasseerd op het applicatie profiel dat u selecteerde in de eerste stap.
				            </br></br>
				            <span class="fullscreen-message">Voor volledig scherm, klik op het gebied en druk op F11, druk op ESC om terug naar normaal te gaan.</span>
				           	<div class="typography">
				        		<!--Put Content for fourth tab here-->
				        		<form><textarea id="result-shapes" name="result-shapes"><c:out value="${report.shapes != null ? report.shapes : 'no report found'}"/></textarea></form>
				          	</div>
				          </section>
				
				        </div>
					     
					    					
 					</div>
				</section>
			</div>
		</div>		
		<jsp:include page="footer.jsp"></jsp:include>
		 <script>
          $(document).ready(function () {
            $("#resultaten").dataTable({
            	 dom: 'Bfrtip',
                "sPaginationType": "full_numbers",
                "bJQueryUI": true,
                "responsive": true,
                buttons: ['copy', 'csv', 'excel']
            });

	      var editor1 = CodeMirror.fromTextArea(document.getElementById("result-ttl"), {
	        mode: "text/turtle",
	        lineNumbers: true,
	        readOnly: true,
	        extraKeys: {
	            "F11": function(cm) {
	              cm.setOption("fullScreen", !cm.getOption("fullScreen"));
	            },
	            "Esc": function(cm) {
	              if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
	            }
	          }
	      });
	      var editor2 = CodeMirror.fromTextArea(document.getElementById("result-data"), {
	        mode: "text/turtle",
	        lineNumbers: true,
	        readOnly: true,
       	 	extraKeys: {
       	        "F11": function(cm) {
       	          cm.setOption("fullScreen", !cm.getOption("fullScreen"));
       	        },
       	        "Esc": function(cm) {
       	          if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
       	        }
       	      }
		    });
	      var editor3 = CodeMirror.fromTextArea(document.getElementById("result-shapes"), {
	        mode: "text/turtle",
	        lineNumbers: true,
	        readOnly: true,
       	 	extraKeys: {
       	        "F11": function(cm) {
       	          cm.setOption("fullScreen", !cm.getOption("fullScreen"));
       	        },
       	        "Esc": function(cm) {
       	          if (cm.getOption("fullScreen")) cm.setOption("fullScreen", false);
       	        }
       	      }
		      });
	    	$("#tab-2-nummer-2").click(function () {
	            setTimeout(function () {
	                editor1.refresh();
	            }, 10);
	        });
	    	$("#tab-2-nummer-3").click(function () {
	            setTimeout(function () {
	                editor2.refresh();
	            }, 10);
	        });
	    	$("#tab-2-nummer-4").click(function () {
	            setTimeout(function () {
	                editor3.refresh();
	            }, 10);
	        });
	    }); 

    	</script>
	</body>
</html>