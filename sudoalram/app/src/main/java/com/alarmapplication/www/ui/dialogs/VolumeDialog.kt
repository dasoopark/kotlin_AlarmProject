package com.alarmapplication.www.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.alarmapplication.www.R
import com.alarmapplication.www.util.*
import kotlinx.android.synthetic.main.dialog_nick_name.*
import kotlinx.android.synthetic.main.dialog_nick_name.btn_cancel
import kotlinx.android.synthetic.main.dialog_nick_name.btn_okay
import kotlinx.android.synthetic.main.dialog_select_song.*

interface OnVolumeSetListener{
    fun onOkClick(volume: Int)
}
//알람 소리 세기 누르면 나오는 창 => 작게(25%), 보통(50%), 크게(75%), 매우 크게(100%)
class VolumeDialog(context: Context, private val onVolumeSetListener: OnVolumeSetListener?): Dialog(context), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)  //Dialog 밖을 터치 했을 경우, Dialog 사라지게 하기 X
        setCancelable(false)      //뒤에 배경 터치 못하게 하기 (꺼지는거 방지)
        setContentView(R.layout.dialog_set_volume)

        btn_okay.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let{
            when(it.id){
                R.id.btn_okay->
                {
                    //마찬가지로 각 버튼에 임의의 값을 줘서, volume으로 어느 것을 고르는지 구분한다.
                    val volume: Int = when (radioGroup.checkedRadioButtonId)
                    {
                        R.id.rb_small ->
                        {
                            SMALL_VOLUME
                        }
                        R.id.rb_normal ->
                        {
                            NORMAL_VOLUME
                        }
                        R.id.rb_big ->
                        {
                            BIG_VOLUME
                        }
                        else -> { //rb_biggest
                            BIGGEST_VOLUME
                        }
                    }
                    onVolumeSetListener?.onOkClick(volume)
                    dismiss()
                }
                R.id.btn_cancel->{
                    dismiss()
                }
                else -> {

                }
            }
        }
    }
}