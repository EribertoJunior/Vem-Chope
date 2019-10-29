package com.example.vemchope.view

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.example.vemchope.R
import com.example.vemchope.model.BluetoothController
import com.example.vemchope.model.WeightDeviceHandler
import com.example.vemchope.model.interfaces.Callback
import com.example.vemchope.model.interfaces.DefinicaoDePeso
import com.example.vemchope.model.interfaces.SelectDevice

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    lateinit var scan: DeviceDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            getDevices()
        }
    }

    private fun getDevices() {
        scan = DeviceDialog(selectDevice = object : SelectDevice {
            override fun recarregar() {
                getDevices()
            }

            override fun selected(bluetoothDevice: BluetoothDevice) {
                Toast.makeText(this@MainActivity, bluetoothDevice.name, Toast.LENGTH_LONG).show()
                fecharScanDialog()
                boundDevice(bluetoothDevice)
            }
        })

        scan.show(supportFragmentManager, "")
    }

    private fun boundDevice(device: BluetoothDevice) {
        Log.d("devlog", "device state: " + device.bondState)
        if (BluetoothDevice.BOND_BONDED == device.bondState) {
            connectDevice(device)
        } else {
            val bluetoothController = BluetoothController(this)
            bluetoothController.boundDevice(device, object : Callback<Boolean> {
                override fun onComplete(result: Boolean) {
                    Log.d("devlog", "Bound device complete: $result")
                    if (result) connectDevice(device)
                }
            })
        }
    }

    private fun connectDevice(device: BluetoothDevice) {
        Log.d("devlog", "Connect Device")
        WeightDeviceHandler().connect(device)
    }

    private fun fecharScanDialog() {
        scan.dismiss()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.bt_menu_definir_peso -> {

                definitPesoMaximo()

                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun definitPesoMaximo() {
        val peso =
            PesoTotalDialog(definicaoDePeso = object : DefinicaoDePeso {
                override fun pesoInformado(peso: String) {
                    tvPesoMaximo.text = peso
                }
            })
        peso.show(supportFragmentManager, "")
    }
}
