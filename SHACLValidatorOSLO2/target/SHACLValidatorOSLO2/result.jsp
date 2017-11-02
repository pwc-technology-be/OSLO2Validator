<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ page isELIgnored="false" %>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
	</head>
	<body>
	
		<jsp:include page="header.jsp"></jsp:include>
	
			<div class="main-content" id="main" itemprop="mainContentOfPage" role="main" tabindex="-1">
				<section class="region">
					<div class="layout layout--wide" id="resultaat">
		        	
<%-- 		        		<h2 class="h2">Shapes</h2>
		        		<pre>
							<c:out value="${report.shapes != null ? report.shapes : 'no report found'}"/>
						</pre> 
						
						<h2 class="h2">Data</h2>
		        		<pre>
							<c:out value="${report.data != null ? report.shapes : 'no report found'}"/>
						</pre>--%>
		        		
		        		<h2 class="h2">Validatie rapport</h2>
		        		<pre>
							<c:out value="${report.result != null ? report.result : 'no report found'}"/>
						</pre>
					
					</div>
				</section>
			</div>
		</div>
		
		<jsp:include page="footer.jsp"></jsp:include>
		
	</body>
</html>