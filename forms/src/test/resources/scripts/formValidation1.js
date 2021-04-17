/**
 Checks the fieldName value length is at least 5
*/
function validateForm(){
    // get value
    var value = "" + view.get("f1")
    var result = {};

    if(value.length < 10){
        out.println(value.length);
        result["error"] = true;
        result["message"] = "The value of field f1 has to be at least 10";
    }
    return result;
}