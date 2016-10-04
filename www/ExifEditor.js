var exec = require('cordova/exec');

exports.addExifData = function(arg0, success, error) {
    exec(success, error, "ExifEditor", "addExifData", [arg0]);
};
