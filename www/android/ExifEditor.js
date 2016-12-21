var exec = require('cordova/exec');

window.AddExifData = function(arg0, success, error) {
    console.log("AddExifData :: " + arg0);
    exec(success, error, "ExifEditor", "AddExifData", arg0);
};
