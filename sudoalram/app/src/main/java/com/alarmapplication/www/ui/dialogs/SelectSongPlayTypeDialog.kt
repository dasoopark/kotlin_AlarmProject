package com.alarmapplication.www.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import com.alarmapplication.www.R
import com.alarmapplication.www.util.EMART
import com.alarmapplication.www.util.INCREASE
import com.alarmapplication.www.util.LOTTE
import com.alarmapplication.www.util.NORMAL
import kotlinx.android.synthetic.main.dialog_nick_name.btn_cancel
import kotlinx.android.synthetic.main.dialog_nick_name.btn_okay
import kotlinx.android.synthetic.main.dialog_select_song.*

interface OnSongPlayTypeSelected{
    fun onOkClick(type: Int)
}

//음악 재생 방법 누르면 나오는 창 =>일반재생, 점점 세게
class SelectSongPlayTypeDialog(context: Context, private val onSongPlayTypeSelected: OnSongPlayTypeSelected?): Dialog(context), View.OnClickListener {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)  //Dialog 밖을 터치 했을 경우, Dialog 사라지게 하기 X
        setCancelable(false)     //뒤에 배경 터치 못하게 하기 (꺼지는거 방지)
        setContentView(R.layout.dialog_select_song_play_type)

        btn_okay.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        //함수를 호출하는 객체를 이어지는 블록의 인자로 넘기고 결과값 반환
        p0?.let{
            when(it.id)
            {
                R.id.btn_okay->
                {
                    //마찬가지로 알람 세기별로, 임의의 값을 줘서 type으로 구별 한다
                    val type: Int = if(radioGroup.checkedRadioButtonId == R.id.rb_normal)
                    {
                        NORMAL
                    }else
                    {
                        INCREASE
                    }
                    onSongPlayTypeSelected?.onOkClick(type)
                    dismiss()
                }
                R.id.btn_cancel->
                {
                    dismiss()
                }
                else -> {
                }
            }
        }
    }
}