var DeviceSensorLoader = function(require, exports, module) {
  var exec = require('cordova/exec');

  var intervalID;

  function DeviceSensor() {}

  DeviceSensor.prototype.start = function(success, failure, timeOffset) {
    // register the accelerometer listener
    exec(success, failure, 'AndroidSensorManager', 'start', []);
    intervalId = setInterval(function() {
      // get the latest value from the accelerometer
      exec(success, failure, 'AndroidSensorManager', 'getCurrent', []);
    }, timeOffset || 500);
  };

  DeviceSensor.prototype.stop = function(success, failure) {
    if (intervalId) {
      clearInterval(intervalId);
      intervalId = null;
    }
    // unregister the accelerometer event listener
    exec(success, failure, 'AndroidSensorManager', 'stop', []);
  };

  var deviceSensor = new DeviceSensor();
  module.export = deviceSensor;
};

DeviceSensorLoader(require, exports, module);

cordova.define("cordova/plugin/DeviceSensor", DeviceSensorLoader);
