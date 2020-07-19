package com.alarmapplication.www.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.alarmapplication.www.R
import com.alarmapplication.www.util.*
import kotlinx.android.synthetic.main.dialog_nick_name.btn_cancel
import kotlinx.android.synthetic.main.dialog_nick_name.btn_okay
import kotlinx.android.synthetic.main.dialog_select_song.*

interface OnAreaSelectedListener {
    fun onOkClick(type: Int)
}

//지역 선택 누르면 나오는 창 => 아시아, 유럽, 아시아+유럽 고르는 다이어로그창
class SelectAreaDialog(context: Context, private val onAreaSelectedListener: OnAreaSelectedListener?) : Dialog(context), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)  //Dialog 밖을 터치 했을 경우, Dialog 사라지게 하기 X
        setCancelable(false)      //뒤에 배경 터치 못하게 하기 (꺼지는거 방지)
        setContentView(R.layout.dialog_select_area)

        btn_okay.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id)
            {
                R.id.btn_okay ->
                {
                    //어떤 나라를 골랐는지 구별하기 위해서 선택지별로 int형 값을 줘서, type에 저장받는다
                    val type: Int = when (radioGroup.checkedRadioButtonId)
                    {
                        R.id.rb_asia -> {
                            ASIA
                        }
                        R.id.rb_europe -> {
                            EUROPE
                        }
                        else -> {
                            ASIA_AND_EUROPE
                        }
                    }
                    onAreaSelectedListener?.onOkClick(type)
                    dismiss()
                }
                R.id.btn_cancel -> {
                    dismiss()
                }
                else -> {

                }
            }
        }
    }
}