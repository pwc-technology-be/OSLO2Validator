<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="stylesheet" type="text/css" href="main.css">
		<title>Validated</title>
	</head>
	<body>
	    <div class="page">
	
	        <div class="banner">
	              <a id="logobanner" href="#"><img src="icon.png" width="70" height="70" float="left" alt="Logo OH Vlaanderen" /></a>
	              <h1> OSLO&#178; Validator </h1>
	        </div>
	
	        <div class="main">
	        
	        	<div class="width-2">
	        		<h2>Gevalideerde data</h2>
	        		<pre>
						<c:out value="${report.data != null ? report.data : 'no report found'}"/>
					</pre>
	        	</div>
	        	
	        	<div class="width-2">
	        		<h2>Regels gebruikt tijdens validatie</h2>
	        		<pre>
						<c:out value="${report.shapes != null ? report.shapes : 'no report found'}"/>
					</pre>
	        	</div>
	        	
	        	<div>
	        		<h2>Validatie rapport</h2>
	        		<pre>
						<c:out value="${report.result != null ? report.result : 'no report found'}"/>
					</pre>
	        	</div>

				
			</div>
		</div>
		
	</body>
</html>