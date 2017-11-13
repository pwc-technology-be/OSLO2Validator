<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html lang="nl">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
</head>
<body>
	<jsp:include page="header.jsp"></jsp:include>

	<!-- Start page content -->

        <div class="main-content" id="main" itemprop="mainContentOfPage" role="main" tabindex="-1">
        	<section class="region">
				<div class="layout layout--wide" id="form">
	        	    <form method="POST" action="validate" enctype="multipart/form-data">
	        	    	<h2 class="h2">OSLO² applicatie profiel</h2>
	        	    	Selecteer hieronder het OSLO² applicatie profiel waartegen u uw data wil valideren.
	        	    	</br>
	        	    	<select name="shapes">
	        	    		<option>Generiek - not implemented</option>
	        	    		<option>Adres - not implemented</option>
	        	    		<option>Organisatie - not implemented</option>
	        	    		<option>Persoon</option>
	        	    		<option>Dienst - not implemented</option>
	        	    	</select>
		        	    	</p>
		        	    	</br>
	        	    	<h2 class="h2">Data</h2>
	        	    	Geef hieronder ofwel een URL mee waarop de data gepubliceerd staat, ofwel een bestand. Indien u beide invult, zal enkel het bestand gevalideerd worden.
				       	<table border="0">
				          <tr>
				             <td>Data file:</td>
				             <td><input type='file' accept='text/ttl' name="data" id="data" /></td>
				          </tr>
				          <tr>
				          	<td>Data URL: </td>
				          	<td><input type='text' name="dataURI" id="dataURI"/></td>
				          </tr>
				       	</table>
				       	</p>
				       	</br>
				       	 <input type="submit" value="valideer" name="upload" id="upload" onClick="checkIt()" />
				    </form>
					
					</br>
					</br>
            		<p>Last updated: 2017-11-13</p>
        		</div>
        	</section>
        </div>

    </div>
    
    <jsp:include page="footer.jsp"></jsp:include>

</body>
</html>