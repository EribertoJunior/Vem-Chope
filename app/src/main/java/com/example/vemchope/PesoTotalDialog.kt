package com.example.vemchope

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.vemchope.interfaces.DefinicaoDePeso
import kotlinx.android.synthetic.main.dialog_peso_total.view.*

class PesoTotalDialog(private val definicaoDePeso: DefinicaoDePeso) : DialogFragment() {
    @SuppressLint("InflateParams")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val view = activity!!.layoutInflater.inflate(R.layout.dialog_peso_total, null)

        val alertDialog = AlertDialog.Builder(view.context)

        view.etPesoTotal.addTextChangedListener(MaskEditUtil.mask(view.etPesoTotal, MaskEditUtil.PESO))

        alertDialog.apply {
            setView(view)
            setCancelable(false)
            setTitle(getString(R.string.pergunta_do_dialog_de_definicao_depeso_total))
            setPositiveButton(getString(R.string.salvar)) { _, _ ->
                val novoPeso: String = view.etPesoTotal.text.toString()
                if (novoPeso.isEmpty()) {
                    definicaoDePeso.pesoInformado("%.3f".format("0".toDouble()).replace(",","."))
                }else{
                    definicaoDePeso.pesoInformado("%.3f".format(novoPeso.toDouble()).replace(",","."))
                }

            }
            setNegativeButton(getString(R.string.cancelar)) { _, _ -> }
        }

        return alertDialog.create()
    }
}