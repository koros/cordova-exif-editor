package cordova.plugin.exif.editor;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import java.io.IOException;
import java.util.Iterator;
import android.util.Log;

/**
 * This class Adds EXIF data to a existing image.
 */
public class ExifEditor extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (action.equals("AddExifData")) {
            String filePath = args.getString(0);
            String extraInfo = args.getString(1);
            this.addExifData(filePath, extraInfo, callbackContext);
            return true;
        }
        return false;
    }

    /**
     * Adds Exif data to an image
     * @param filePath - path of the file
     * @param extraInfo - json sting of key value pairs passed from javascript e.g {"UserComment" : "gone hiking"}
     * @throws Exception
     */
    private void addExifData(String filePath, String extraInfo, CallbackContext callbackContext) {
        try {
            if (extraInfo != null) {
                Log.i("ExifEditor", " filePath = " + filePath );
                Log.i("ExifEditor", " extraInfo = " + extraInfo );
                ExifInterface exif = new ExifInterface(filePath);
                JSONObject jsonObj = new JSONObject(extraInfo);
                Iterator<String> keysIterator = jsonObj.keys();
                while (keysIterator.hasNext()) {
                    String key = keysIterator.next();
                    exif.setAttribute(key, jsonObj.getString(key));
                }
                exif.saveAttributes();
            }
            callbackContext.success("Exif data added sucessfully");
        }catch (Exception e){
            e.printStackTrace();
            callbackContext.error(e.toString());
        }
    }
}
