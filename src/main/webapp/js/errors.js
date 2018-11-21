/**
 * Checks if a file has been uploaded
 * @param {string} datafile - JQuery selector for the field of the file uploaded
 * @param {string} datafileerror -  JQuery selector for the error field of the file uploaded
 */
function validateData(datafile, datafileerror) {
    var isFilled = $(datafile).get(0).files.length > 0;
    if (isFilled) {
        $(datafileerror).text("");
        return true;
    }
    else {
        $(datafileerror).text("Het databestand is een verplicht veld.");
        return false;
    }
}

$("#data").change(function () {
    validateData("#data", "#datafileerror")
});

/**
 * Validates the Form2, triggered by the web form on submit
 */
function validateForm1() {
    var cond_metadata = validateData("#data", "#datafileerror");
    if (cond_metadata) {
        return true;
    }
    return false;
}

$("#input-field").focusout(function () {
	validateEndpoint("#input-field", "#urierror");
})

/**
 * Checks if the endpoint is empty and it is a valid URL
 * @param {string} endpoint - JQuery selector for the field of the endpoint
 * @param {string} endpointerror -  JQuery selector for the error field of the endpoint
 */
function validateEndpoint(endpoint, endpointerror) {
    var value = $(endpoint).val(),
        isFilled = value.length > 0,
        subject = "De URL van de gegevens";
    if (isFilled && isUrl(value)) {
        $(endpointerror).text("");
        return true;
    }
    if (!isFilled) {
        $(endpointerror).text(subject + " is een verplicht veld.");
        return false;
    }
    if (!isUrl(value)) {
        $(endpointerror).text(subject + " is geen geldige URL.");
        return false;
    }
}

/**
 * Checks if the string is a valid URL
 * @param {string} value - The URL to check
 */
function isUrl(value) {
	var urlRegex = /^((http|https):\/\/(\w+:{0,1}\w*@)?(\S+)|)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/;
	return urlRegex.test(value);
}

/**
 * Validates the Form2, triggered by the web form on submit
 */
function validateForm2() {
    var cond_address = validateEndpoint("#input-field", "#urierror");
    if (cond_address) {
        return true;
    }
    return false;
}

$("#data").change(function(){
	  $("#file-name").text(this.files[0].name);
	});
