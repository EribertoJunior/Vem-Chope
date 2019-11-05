package com.example.vemchope.view

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.vemchope.R
import com.example.vemchope.model.bluetooth.BluetoothController
import com.example.vemchope.model.entidade.Medicao
import com.example.vemchope.model.enums.STATUS
import com.example.vemchope.model.interfaces.DefinicaoDePeso
import com.example.vemchope.model.interfaces.SelectDevice
import com.example.vemchope.view_model.MedicaoViewModel
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    lateinit var scan: DeviceDialog
    private val CHANNEL_ID = "defalt"
    private lateinit var bluetoothController: BluetoothController

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MedicaoViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        bluetoothController = BluetoothController(this)

        initObservables()
        verificarDisponibilidadeBluetooth()
        fab.setOnClickListener { view ->
            getDevices()

        }
    }

    private fun verificarDisponibilidadeBluetooth() {
        if (!bluetoothController.isBluetoothEnabled()) {
            startActivityForResult(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1000)
        }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 1000) {
            if (bluetoothController.isBluetoothEnabled())
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
                    createNotificationChannel()
                    atualizarValores(it)
                }
                STATUS.MENSAGEM -> {
                    Toast.makeText(this, it.mensagem, Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.glass_mug_variant)
                .setContentTitle(getString(R.string.channel_name))
                .setContentText(getString(R.string.channel_description))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)

            with(NotificationManagerCompat.from(this)) {
                notify(1, builder.build())
            }
        }
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

    private fun fecharScanDialog() {
        scan.dismiss()
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
