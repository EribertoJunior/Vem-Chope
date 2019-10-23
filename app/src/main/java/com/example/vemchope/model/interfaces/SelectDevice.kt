package com.example.vemchope.model.interfaces

import android.bluetooth.BluetoothDevice

interface SelectDevice {
    fun selected(bluetoothDevice: BluetoothDevice)

    fun recarregar()
}