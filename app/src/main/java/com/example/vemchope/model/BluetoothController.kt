package com.example.vemchope.model

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import com.example.vemchope.model.interfaces.ScanDeviceCompleted


class BluetoothController {
    var mBluetoothAdapter: BluetoothAdapter? = null
    var listDevice: ArrayList<BluetoothDevice> = arrayListOf()

    fun isBluetoothEnabled(): Boolean {
        return mBluetoothAdapter != null && mBluetoothAdapter?.isEnabled ?: false
    }

    fun initializeBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun scanDevice(context: Context, onScanDeviceCompleted: ScanDeviceCompleted) {
        listDevice.clear()
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                        // Processo de discovery inicializado.
                    }
                    BluetoothDevice.ACTION_FOUND -> {
                        val device =
                            intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                        if (!device.name.isNullOrBlank())
                            listDevice.add(device)

                        // Encontrou um dispositivo
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        // Processo de discovery finalizado.
                        onScanDeviceCompleted.onScanDeviceComplited(listDevice)
                    }
                }
            }
        }

        val filter = IntentFilter()
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        context.registerReceiver(receiver, filter)
        // iniciar a ação de encontrar dispositivos, isso pode demorar em torno de 12
        // segundos para finalizar.
        mBluetoothAdapter?.startDiscovery()
    }
}