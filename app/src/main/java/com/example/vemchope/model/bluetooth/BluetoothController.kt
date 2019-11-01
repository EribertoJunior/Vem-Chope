package com.example.vemchope.model.bluetooth

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.bluetooth.BluetoothDevice.ACTION_BOND_STATE_CHANGED
import com.example.vemchope.model.interfaces.Callback
import com.example.vemchope.model.interfaces.ScanDeviceCompleted


class BluetoothController(context: Context) {
    var mBluetoothAdapter: BluetoothAdapter? = null
    var listDevice: ArrayList<BluetoothDevice> = arrayListOf()

    var mContext: Context = context

    fun isBluetoothEnabled(): Boolean {
        return mBluetoothAdapter != null && mBluetoothAdapter?.isEnabled ?: false
    }

    fun initializeBluetoothAdapter() {
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
    }

    fun scanDevice(context: Context, onScanDeviceCompleted: ScanDeviceCompleted) {

        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                when (intent.action) {
                    BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                        // Processo de discovery inicializado.
                        listDevice.clear()
                    }
                    BluetoothDevice.ACTION_FOUND -> {
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                            ?.let {
                                if (!it.name.isNullOrBlank()) listDevice.add(it)
                            }

                        // Encontrou um dispositivo
                    }
                    BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                        // Processo de discovery finalizado.
                        onScanDeviceCompleted.onScanDeviceComplited(listDevice)
                        context.unregisterReceiver(this)
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

    fun boundDevice(device: BluetoothDevice, callback: Callback<Boolean>) {
        device.createBond()
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                when (intent?.action) {
                    ACTION_BOND_STATE_CHANGED -> {
                        val state = intent.getIntExtra(
                            "android.bluetooth.device.extra.BOND_STATE", BluetoothDevice.BOND_NONE
                        )
                        if (state == BluetoothDevice.BOND_BONDED) {
                            callback.onComplete(true)
                        } else {
                            callback.onComplete(false)
                        }
                    }
                }
            }
        }
        mContext.registerReceiver(receiver, IntentFilter(ACTION_BOND_STATE_CHANGED))
    }
}