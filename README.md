Deze repository is een onderdeel van het initiatief **Open Standaarden voor Linkende Organisaties __(OSLO)__**.
Meer informatie is terug te vinden op de [OSLO productpagina](https://overheid.vlaanderen.be/producten-diensten/OSLO2).

Deze repository bevat de source code voor een tool die toelaat om data te valideren tegen de [OSLO applicatieprofielen](https://data.vlaanderen.be/ns#Applicatieprofielen). Deze validator is beschikbaar via https://data.vlaanderen.be/shacl-validator/ of kan lokaal geïnstalleerd worden met behulp van onderstaande instructies.

# OSLO2Validator

 ## Table of Contents
* [Overview](#overview)
* [Requirements](#requirements)
* [Source Structure](#source-structure)
* [Reuse](#reuse)
* [Usage Instructions](#usage-instructions)
  * [Validating by uploading a file](#validating-by-uploading-a-file)
  * [Validating by URI](#validating-by-uri)
* [License](#license)


## Overview

This project was created as part of the OSLO² ([Github](http://informatievlaanderen.github.io/OSLO/),
 [Vlaanderen.be](https://overheid.vlaanderen.be/producten-diensten/OSLO2)) initiative by the Flemish Government.
 The OSLO² project aims to achieve a solid standard for data exchange between (local) governments.
 These data exchange formats are created as RDF vocabularies (or ontologies), following the principles of the Linked
 Data movement.
 
 This tool provides a way of validating RDF graphs against the [Application Profiles available within the OSLO² context](http://data.vlaanderen.be/ns/#Applicatieprofielen). The validation is based on the [SHACL Shapes Constraint Language](https://www.w3.org/TR/shacl/), a language for validating RDF graphs against a set of conditions. SHACL is a W3C recommendation since 20 July 2017.

## Requirements

* JDK 8 (minimum)
* Servlet container such as Tomcat 7 (minimum) or Jetty

## Source Structure

* /src/main/resources/defaultquery.rq - File containing the query used during formatting of the validation result.
* /src/main/java/validator/OSLO2/APModel.java - Data model of an application profile configuration entry
* /src/main/java/validator/OSLO2/Configuration.java - Data class responsible for loading and validating all configuration
* /src/main/java/validator/OSLO2/HomeServlet.java - The home servlet of the web application.
* /src/main/java/validator/OSLO2/ValidateServlet.java - The servlet receiving the user input and taking care of the validation process.
* /src/main/java/validator/OSLO2/ValidationReport.java - The class file defining the elements returned to the user.
* /src/main/java/validator/OSLO2/ValidationResult.java - The class file defining the list of all validation results returned to the user through the ValidationReport.
* /src/main/webapp/WEB-INF/home.jsp - JSP of the home web page.
* /src/main/webapp/WEB-INF/result.jsp - JSP page of the web page with the results after validation.
* /src/main/webapp/WEB-INF/web.xml - Web Application Deployment Descriptor of the application.

## Configuration

The tool is highly reusable due to the fact that the SHACL rules and vocabularies it uses to validate the RDF Graph are retrieved from a location which can be specified in the AP_CONFIG environment variable

The configuration file takes the following form:
```json
{
  "adres-register": {
    "location": "http://data.vlaanderen.be/shacl/Adresregister-SHACL.ttl",
    "dependencies": ["http://data.vlaanderen.be/ns/adres.ttl"]
  },
  "dienst": {
    "location": "http://data.vlaanderen.be/shacl/Dienst-SHACL.ttl",
    "dependencies": ["http://data.vlaanderen.be/ns/dienst.ttl"]
  }
}
```

* *location*: A URL to a turtle file containing the SHACL description of the application profile
* *dependencies*: An array of URLs with additional terms that need to be loaded along with the SHACL file (typically a vocabulary)

## Usage Instructions

Go to the webpage where the tool is hosted. 
Choose whether you want to validate an RDF graph by providing the tool with a file or with a URL. 

### Validating by uploading a file

Choose the application profile against which you want to validate the RDF graph. Next, upload the file by clicking "Bijlage toevoegen" and click the "Klik hier om te valideren" button. The allowed file extension are: ".ttl", ".xml", ".rdf", ".jsonld" and ".json". The resulting webpage will contain the following tabs of information:

* The validation result in TTL format
* The validation result in table format
* The data which was validated. This is the data you provided the tool with, combined with the applicable RDF vocabularies (based on the application profile you selected)
* The SHACL rules which were used for validation, this are the SHACL rules which were composed for the application profile that you selected.

### Validating by URI

Choose the application profile against which you want to validate the RDF graph. Next, input the URI in the appropriate text box. When uploading through URI, you are able to provide an optional header for the HTTP GET request through the Header Key and Header Value text box which will retrieve your RDF graph. The tool can validate RDF and JSONLD data. The resulting webpage will contain the following tabs of information:

* The validation result in TTL format
* The validation result in table format
* The data which was validated. This is the data you provided the tool with, combined with the applicable RDF vocabularies (based on the application profile you selected)
* The SHACL rules which were used for validation, this are the SHACL rules which were composed for the application profile that you selected.

## License

This software is released with the EUPL 1.2 license.
