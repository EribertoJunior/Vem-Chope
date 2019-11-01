package com.example.vemchope.view

import android.bluetooth.BluetoothDevice
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.vemchope.R
import com.example.vemchope.model.entidade.Medicao
import com.example.vemchope.model.enums.STATUS
import com.example.vemchope.model.interfaces.DefinicaoDePeso
import com.example.vemchope.model.interfaces.SelectDevice
import com.example.vemchope.view_model.MedicaoViewModel

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    lateinit var scan: DeviceDialog

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MedicaoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        initObservables()

        fab.setOnClickListener { view ->
            getDevices()
        }
    }

    private fun initObservables() {
        viewModel.medicaoData.observe(this, Observer {
            when (it.status) {
                STATUS.ATUALIZAR -> {
                    atualizarValores(it)

                }
                STATUS.NOTIFICAR -> {
                    NotificationCompat.Builder(this, "")
                        //.setSmallIcon(R.drawable.notification_icon)
                        .setContentTitle("Vem Chope")
                        .setContentText("Trás o refil ae...")
                        .priority = NotificationCompat.PRIORITY_DEFAULT
                    atualizarValores(it)
                }
                STATUS.MENSAGEM -> {
                    Toast.makeText(this, it.mensagem, Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    private fun atualizarValores(it: Medicao) {
        tvPesoAtual.text = it.pesoAtual.toString()
        tvPesoMaximo.text = it.pesoMaximo.toString()
        tvPorcentagem.text = it.porcentagem.toString()
        Log.d(
            "medidas",
            "pesoMaximo: ${it.pesoMaximo}, pesoAtual: ${it.pesoAtual}, ${it.porcentagem}%"
        )
    }

    private fun getDevices() {
        scan = DeviceDialog(selectDevice = object : SelectDevice {
            override fun recarregar() {
                getDevices()
            }

            override fun selected(bluetoothDevice: BluetoothDevice) {
                Toast.makeText(this@MainActivity, bluetoothDevice.name, Toast.LENGTH_LONG).show()
                fecharScanDialog()
                viewModel.boundDevice(bluetoothDevice, this@MainActivity)
            }
        })

        scan.show(supportFragmentManager, "")
    }

    /*private fun boundDevice(device: BluetoothDevice) {
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
        WeightDeviceHandler(
            object : Callback<String> {
                override fun onComplete(data: String) {
                    this@MainActivity.runOnUiThread {

                        if (data.isNotEmpty() and !data.contains("-")) {
                            Log.d(">>", data)

                            tvPesoAtual.text = data
                        }
                    }
                }
            }
        ).connect(device)
    }*/

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
                    //tvPesoMaximo.text = peso
                    viewModel.definitPesoMaximo(peso)
                }
            })
        peso.show(supportFragmentManager, "")
    }
}
