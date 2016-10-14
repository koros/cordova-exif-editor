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
     */
    private void addExifData(String filePath, String extraInfo, CallbackContext callbackContext) {
        try {
            if (extraInfo != null) {
                Log.i("ExifEditor", " filePath = " + filePath );
                Log.i("ExifEditor", " extraInfo = " + extraInfo );
                JSONObject jsonObj = new JSONObject(extraInfo);
                tagImageWithCurrentUserLocation(filePath, jsonObj);
                ExifInterface exif = new ExifInterface(filePath);
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

    /**
     * Tag the image with latitude and longitude ref values
     * @param filePath - image uri
     * @param jsonObj - key, value json object
     * @throws Exception
     */
    public static void tagImageWithCurrentUserLocation(String filePath, JSONObject jsonObj) throws Exception {
        if (jsonObj.has("lat") && jsonObj.has("lon")){
            double lat = jsonObj.getDouble("lat");
            double lon = jsonObj.getDouble("lon");

            Location location = new Location("");
            location.setLatitude(lat);
            location.setLongitude(lon);

            ExifInterface exif = new ExifInterface(filePath);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE, convertToDegreeMinuteSeconds(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF, getLatitudeRef(location.getLatitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, convertToDegreeMinuteSeconds(location.getLongitude()));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF, getLongitudeRef(location.getLongitude()));
            exif.saveAttributes();
        }
    }

    /**
     * returns ref for latitude which is S or N.
     *
     * @param latitude
     * @return S or N
     */
    private static String getLatitudeRef(double latitude) {
        return latitude < 0.0d ? "S" : "N";
    }

    /**
     * returns ref for latitude which is S or N.
     *
     * @param longitude
     * @return W or E
     */
    private static String getLongitudeRef(double longitude) {
        return longitude < 0.0d ? "W" : "E";
    }

    /**
     * convert latitude into DMS (degree minute second) format. For instance<br/>
     * -79.948862 becomes<br/>
     * 79/1,56/1,55903/1000<br/>
     * It works for latitude and longitude<br/>
     *
     * @param latitude could be longitude.
     * @return
     */
    private static String convertToDegreeMinuteSeconds(double latitude) {
        latitude = Math.abs(latitude);
        int degree = (int) latitude;
        latitude *= 60;
        latitude -= (degree * 60.0d);
        int minute = (int) latitude;
        latitude *= 60;
        latitude -= (minute * 60.0d);
        int second = (int) (latitude * 1000.0d);

        StringBuilder sb = new StringBuilder();
        sb.append(degree);
        sb.append("/1,");
        sb.append(minute);
        sb.append("/1,");
        sb.append(second);
        sb.append("/1000,");
        return sb.toString();
    }
}
