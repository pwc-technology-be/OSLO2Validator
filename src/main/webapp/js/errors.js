/**
 * Checks if a file has been uploaded
 * @param {string} metadatafile - JQuery selector for the field of the file uploaded
 * @param {string} metadatafileerror -  JQuery selector for the error field of the file uploaded
 */
function validateData(datafile, datafileerror) {
    var isFilled = $(datafile).get(0).files.length > 0;
    if (isFilled) {
        $(datafileerror).text("");
        return true;
    }
    else {
        $(datafileerror).text("Le fichier de données est un champ obligatoire.");
        return false;
    }
}

$("#data").change(function () {
    validateData("#data", "#datafileerror")
});
/**
 * Validates the Form1
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
 * @param {string} subject -  string to name the endpoint field
 */
function validateEndpoint(endpoint, endpointerror) {
    var value = $(endpoint).val(),
        isFilled = value.length > 0,
        subject = "L'adresse URL des données";
    if (isFilled && isUrl(value)) {
        $(endpointerror).text("");
        return true;
    }
    if (!isFilled) {
        $(endpointerror).text(subject + " est un champ obligatoire.");
        return false;
    }
    if (!isUrl(value)) {
        $(endpointerror).text(subject + " n'est pas une URL valide.");
        return false;
    }
}

function isUrl(value) {
	var urlRegex = /^((http|https):\/\/(\w+:{0,1}\w*@)?(\S+)|)(:[0-9]+)?(\/|\/([\w#!:.?+=&%@!\-\/]))?$/;
	return urlRegex.test(value);
}
/**
 * Validates the Form2
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