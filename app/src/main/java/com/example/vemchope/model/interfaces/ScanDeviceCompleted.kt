package com.example.vemchope.model.interfaces

import android.bluetooth.BluetoothDevice

interface ScanDeviceCompleted {

    fun onScanDeviceComplited(list: ArrayList<BluetoothDevice>)
}