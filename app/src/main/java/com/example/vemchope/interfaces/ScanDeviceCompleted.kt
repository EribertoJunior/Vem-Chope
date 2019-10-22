package com.example.vemchope.interfaces

import android.bluetooth.BluetoothDevice

interface ScanDeviceCompleted {

    fun onScanDeviceComplited(list: ArrayList<BluetoothDevice>)
}