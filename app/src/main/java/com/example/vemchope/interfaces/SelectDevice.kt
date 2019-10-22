package com.example.vemchope.interfaces

import android.bluetooth.BluetoothDevice

interface SelectDevice {
    fun selected(bluetoothDevice: BluetoothDevice)

    fun recarregar()
}