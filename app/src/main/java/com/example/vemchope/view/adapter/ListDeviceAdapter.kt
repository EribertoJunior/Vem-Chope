package com.example.vemchope.view.adapter

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.vemchope.R
import com.example.vemchope.model.interfaces.SelectDevice
import kotlinx.android.synthetic.main.item_device_bluetooth.view.*

class ListDeviceAdapter(
    var list: ArrayList<BluetoothDevice> = arrayListOf(),
    selectDevice: SelectDevice
) :
    RecyclerView.Adapter<ListDeviceAdapter.ViewHolder>() {

    private val mOnclickListener: View.OnClickListener = View.OnClickListener { view ->
        val bluetoothDevice = view.tag as BluetoothDevice

        selectDevice.selected(bluetoothDevice)
    }


    @SuppressLint("InflateParams")
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_device_bluetooth, null)
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val device = list[position]

        holder.view.tvNomeDevice.text = device.name
        with(holder.view) {
            tag = device
            setOnClickListener(mOnclickListener)
        }
    }

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}