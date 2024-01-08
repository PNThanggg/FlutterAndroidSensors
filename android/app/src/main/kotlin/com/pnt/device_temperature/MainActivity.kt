package com.pnt.device_temperature

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodChannel


class MainActivity : FlutterActivity() {
    companion object {
        private const val METHOD_CHANNEL_NAME = "environment_sensors/method"
        private const val TEMPERATURE_CHANNEL_NAME = "environment_sensors/temperature"
        private const val HUMIDITY_CHANNEL_NAME = "environment_sensors/humidity"
        private const val LIGHT_CHANNEL_NAME = "environment_sensors/light"
        private const val PRESSURE_CHANNEL_NAME = "environment_sensors/pressure"
    }

    private var sensorManager: SensorManager? = null
    private var methodChannel: MethodChannel? = null
    private var temperatureChannel: EventChannel? = null
    private var humidityChannel: EventChannel? = null
    private var lightChannel: EventChannel? = null
    private var pressureChannel: EventChannel? = null

    private var temperatureStreamHandler: StreamHandlerImpl? = null
    private var humidityStreamHandler: StreamHandlerImpl? = null
    private var lightStreamHandler: StreamHandlerImpl? = null
    private var pressureStreamHandler: StreamHandlerImpl? = null

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        super.configureFlutterEngine(flutterEngine)

        sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

        temperatureChannel = EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            TEMPERATURE_CHANNEL_NAME
        )
        temperatureStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_AMBIENT_TEMPERATURE)
        temperatureChannel?.setStreamHandler(temperatureStreamHandler)

        humidityChannel = EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            HUMIDITY_CHANNEL_NAME
        )
        humidityStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_RELATIVE_HUMIDITY)
        humidityChannel?.setStreamHandler(humidityStreamHandler)

        lightChannel = EventChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            LIGHT_CHANNEL_NAME
        )
        lightStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_LIGHT)
        lightChannel?.setStreamHandler(lightStreamHandler)

        pressureChannel =
            EventChannel(flutterEngine.dartExecutor.binaryMessenger, PRESSURE_CHANNEL_NAME)
        pressureStreamHandler = StreamHandlerImpl(sensorManager, Sensor.TYPE_PRESSURE)
        pressureChannel?.setStreamHandler(pressureStreamHandler)

        methodChannel = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger,
            METHOD_CHANNEL_NAME
        )
        methodChannel?.setMethodCallHandler { call, result ->
            when (call.method) {
                "isSensorAvailable" -> {
                    result.success(
                        sensorManager?.getSensorList(call.arguments as Int)?.isNotEmpty()
                    )
                }

                else -> result.notImplemented()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        methodChannel?.setMethodCallHandler(null)
        methodChannel = null

        temperatureChannel?.setStreamHandler(null)
        temperatureChannel = null

        humidityChannel?.setStreamHandler(null)
        humidityChannel = null

        lightChannel?.setStreamHandler(null)
        lightChannel = null

        pressureChannel?.setStreamHandler(null)
        pressureChannel = null
    }
}
