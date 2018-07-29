package android;
// Need to check the above one

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import android.content.Context;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;
import android.hardware.SensorEvent;
import android.hardware.Sensor;

// this is our Cordova plugin, we need to extend it from the CordovaPlugin class
public class AndroidSensorManager extends CordovaPlugin {
  // a bunch of globals
  private SensorManager mSensorManager;
  private Sensor accelerometer;
  private CallbackContext callbackContext;
  private JSONObject data = new JSONObject();

  // at the initialize function, we can configure the tools we want to use later, like the sensors
  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
    mSensorManager = (SensorManager) cordova.getActivity().getSystemService(Context.SENSOR_SERVICE);
    accelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
  }

  // safety unregistering from the events if the application stops somehow
  @Override
  public void onDestroy() {
    mSensorManager.unregisterListener(listener);
  }
  
  // this is the main part of the plugin, we have to handle all of the actions sent from the js
  @Override
  public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
    if ("start".equals(action)) {
      // here we call a start action to register our event listener to watch the data coming from the accelerometer
      mSensorManager.registerListener(listener, accelerometer, SensorManager.SENSOR_DELAY_UI);
    } else if ("stop".equals(action)) {
      // manually stop receiving data.
      mSensorManager.unregisterListener(listener);
    } else if ("getCurrent".equals(action)) {
      // we send back the latest saved data from the event listener to the success callback
      PluginResult result = new PluginResult(PluginResult.Status.OK, this.data);
      callbackContext.sendPluginResult(result);
      return true;
    }
    // Returning false results in a "MethodNotFound" error.
    return false;  
  }
  // this is our event listener which will handle the received data and save it to a global variable
  private SensorEventListener listener = new SensorEventListener() {
    public void onSensorChanged(SensorEvent event) {
      if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
        data = new JSONObject();
        try {
            data.put("x", event.values[0]);
            data.put("y", event.values[1]);
            data.put("z", event.values[2]);
        } catch(JSONException e) {}
      }
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
      // unused
    }
};
}
