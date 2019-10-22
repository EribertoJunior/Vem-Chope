package com.example.vemchope

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vemchope.adapter.ListDeviceAdapter
import com.example.vemchope.interfaces.ScanDeviceCompleted
import com.example.vemchope.interfaces.SelectDevice
import kotlinx.android.synthetic.main.content_dialog_device_fragment.view.*
import kotlinx.android.synthetic.main.dialog_devices_bluetooth.view.*

class DeviceDialogFragment(private val selectDevice: SelectDevice) : DialogFragment() {

    private val bluetoothController = BluetoothController()

    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_devices_bluetooth, null)

        bluetoothController.initializeBluetoothAdapter()

        view.rvListDevice.apply {
            layoutManager = LinearLayoutManager(view.context)
            adapter = ListDeviceAdapter(selectDevice = selectDevice)
        }

        val alertDialog = AlertDialog.Builder(view?.context)
        view.progressBar.visibility = View.VISIBLE


        scanDeviceBluetooth(view)

        alertDialog.apply {
            setTitle(getString(R.string.dispositivos_encontrados))
            setView(view)
            setCancelable(false)
            setPositiveButton(getString(R.string.recarregar)) { dialog, which ->
                selectDevice.recarregar()
            }
            setNeutralButton(getString(R.string.cancelar)) { dialog, which ->
                dialog.dismiss()
            }
        }

        return alertDialog.create()
    }

    private fun scanDeviceBluetooth(
        view: View
    ) {
        bluetoothController.scanDevice(view.context, object :
            ScanDeviceCompleted {
            override fun onScanDeviceComplited(list: ArrayList<BluetoothDevice>) {

                (view.rvListDevice.adapter as ListDeviceAdapter).apply {
                    this.list = list
                    notifyDataSetChanged()
                }

                view.rvListDevice.visibility = View.VISIBLE
                view.progressBar.visibility = View.GONE

            }
        })
    }
}