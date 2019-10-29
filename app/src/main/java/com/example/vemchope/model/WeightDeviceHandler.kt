package com.example.vemchope.model

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.util.*

class WeightDeviceHandler {
    companion object {
        val APP_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    }

    private var mSocket: BluetoothSocket? = null

    fun connect(device: BluetoothDevice) {
        Thread(Runnable {
            try {
                mSocket = device.createInsecureRfcommSocketToServiceRecord(APP_UUID)
                mSocket?.connect()

                val bluetoothIn = mSocket?.inputStream
                val buffer = ByteArray(256)

                Log.d("devlog", "socket connected!!!")
                while (true) {
                    try {
                        bluetoothIn?.let {
                            Log.d("devlog", "it.available(): " + it.available())
                            if (it.available() > 0) {
                                bluetoothIn.read(buffer, 0, it.available())
                                val result = String(buffer)
                                Log.d("devlog", "result: $result")
                            }
                        }
                    } catch (ex: IOException) {
                        ex.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }).start()
    }
}