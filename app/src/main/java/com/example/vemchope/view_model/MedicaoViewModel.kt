package com.example.vemchope.view_model

import android.bluetooth.BluetoothDevice
import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.vemchope.model.bluetooth.BluetoothController
import com.example.vemchope.model.bluetooth.WeightDeviceHandler
import com.example.vemchope.model.entidade.Medicao
import com.example.vemchope.model.enums.STATUS
import com.example.vemchope.model.interfaces.Callback
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.concurrent.timerTask

class MedicaoViewModel : ViewModel(), LifecycleObserver {
    val medicaoData: MutableLiveData<Medicao> = MutableLiveData(
        Medicao(
            status = STATUS.ATUALIZAR
        )
    )

    private var notificar = true

    fun boundDevice(device: BluetoothDevice, context: Context) {
        Log.d("devlog", "device state: " + device.bondState)
        if (BluetoothDevice.BOND_BONDED == device.bondState) {
            connectDevice(device)
        } else {
            val bluetoothController = BluetoothController(context)
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

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                WeightDeviceHandler(object : Callback<String> {
                    override fun onComplete(data: String) {
                        if (data.isNotEmpty() and !data.contains("-")) {
                            Log.d(">>", data)

                            medicaoData.postValue(medicaoData.value?.apply {
                                pesoAtual = data.toFloat()
                                porcentagem = regraDeTres(pesoMaximo, data.toDouble())
                                status = if (porcentagem <= 20 && notificar) {
                                    notificar = false
                                    contarTempo()

                                    STATUS.NOTIFICAR
                                } else STATUS.ATUALIZAR
                            })
                        }

                    }
                }
                ).connect(device)
            }
        }
    }

    private fun contarTempo() {
        Timer().schedule(timerTask {
            notificar = true
        }, 15000)
    }

    fun definitPesoMaximo(peso: String) {
        medicaoData.postValue(medicaoData.value?.apply {
            pesoMaximo = peso.toFloat()
        })
    }

    private fun regraDeTres(pesoMaximo: Float, pesoAtual: Double): Int {

        return ((pesoAtual * 100) / pesoMaximo).toInt()

    }
}
