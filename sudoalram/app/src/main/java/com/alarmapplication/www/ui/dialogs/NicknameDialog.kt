package com.alarmapplication.www.ui.dialogs

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.alarmapplication.www.R
import kotlinx.android.synthetic.main.dialog_nick_name.*

//알람 별명 누르면 나오는 다이어로그(alert가 아닌 커스텀 다이어로그)
interface OnNickNameSetListener{
    fun onOkClick(nickName: String)
}

//이벤트 ( onclickListener -> onClick
class NicknameDialog(context: Context, private val onNickNameSetListener: OnNickNameSetListener?): Dialog(context), View.OnClickListener
{
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCanceledOnTouchOutside(false)   //Dialog 밖을 터치 했을 경우, Dialog 사라지게 하기 X
        setCancelable(false)    //뒤에 배경 터치 못하게 하기 (꺼지는거 방지)
        setContentView(R.layout.dialog_nick_name)

        btn_okay.setOnClickListener(this)
        btn_cancel.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        //함수를 호출하는 객체를 이어지는 블록의 인자로 넘기고 결과값 반환
        p0?.let{
            when(it.id){
                R.id.btn_okay->
                {
                    val nickName = edt_nick_name.text.toString()  //확인을 누르면 알람 별명(입력받음)
                    if(nickName.isEmpty())
                    {
                        Toast.makeText(context, "닉네임을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    onNickNameSetListener?.onOkClick(nickName)
                    dismiss() //다이어로그 안전하게 종료하기
                }
                R.id.btn_cancel-> //취소 누르면 사라짐
                {
                    dismiss()
                }
                else -> {
                }
            }
        }
    }
}