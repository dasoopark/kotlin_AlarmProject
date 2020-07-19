package com.alarmapplication.www.util

import android.content.Context
import android.content.SharedPreferences

//임의 클래스에 대해 어떤 작업을 하는 메소드를 만들 때 다형적인 유형으로 사용하는 경우
// (클래스의 메소드가 어떤 유형이라도 인자값으로 받을 수 있어야 할때)
// object 클래스와 sharedPreferences로 값 설정이 필요한 부분들의 코드를 줄여, 양을 엄청 줄일 수 있었다.

//Constants 에 저장된 const 상수들의 값 확인
object SettingsManager{
    private lateinit var pref: SharedPreferences

    fun init(context: Context) {
        pref = context.getSharedPreferences("alarm", Context.MODE_PRIVATE) //해당 앱에서만 접근을 가능하게함
    }

    //세터와 게터
    var areaType: Int       //나라고르기
        get() = pref.getInt("area", 10)
        set(value) = pref.edit {
            it.putInt("area", value)
        }

    var showIcon: Boolean       //알람 깜빡이 껏다 키기
        get() = pref.getBoolean("showIcon", true)
        set(value) = pref.edit {
            it.putBoolean("showIcon", value)
        }

    var playType: Int   //알람 소리 일반재생 인지 점점 세게 인지
        get() = pref.getInt("playType", NORMAL)
        set(value) = pref.edit {
            it.putInt("playType", value)
        }


    private inline fun SharedPreferences.edit(erate:(SharedPreferences.Editor) -> Unit) {
        val editor = edit()
        erate(editor)
        editor.apply() //객체반환
    }
}