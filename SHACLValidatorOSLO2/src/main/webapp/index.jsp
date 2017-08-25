<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<link rel="stylesheet" type="text/css" href="main.css">
<title>OSLO2 Validator</title>
</head>
<body>
    <div class="page">

        <div class="banner">
              <a id="logobanner" href="#"><img src="icon.png" width="70" height="70" float="left" alt="Logo OH Vlaanderen" /></a>
              <h1> OSLO&#178; Validator </h1>
        </div>


        <div class="main">
        	    <form method="POST" action="validate" enctype="multipart/form-data">
			       <table border="0">
			          <tr>
			             <td>Data file:</td>
			             <td><input type='file' accept='text/ttl' name="data" id="data" /></td>
			          </tr>
			          <tr>
			             <td>Data file URL:</td>
			             <td><input type='text' name="dataURI"/></td>
			          </tr>
			          <tr>
			             <td>Shapes file:</td>
			             <td><input type='file' accept='text/ttl' name="shapes" id="shapes" /></td>
			          </tr>
			          <tr>
			             <td colspan="2">                  
			                 <input type="submit" value="Upload" name="upload" id="upload" />
			             </td>
			          </tr>
			       </table>
			    </form>

            <p>Last updated: 2017-08-11</p>
        </div>

    </div>
</body>
</html>