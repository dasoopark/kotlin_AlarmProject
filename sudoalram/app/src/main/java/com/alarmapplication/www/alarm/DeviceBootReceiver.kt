package com.alarmapplication.www.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.alarmapplication.www.ui.AlarmQuizActivity

// 재부팅 후에도 알람이 동작하도록, 하기위해서 생성한다. (알람 특성)
class DeviceBootReceiver : BroadcastReceiver()
{
    //인터페이스 상속후 구현 필터
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "android.intent.action.BOOT_COMPLETED") { //무조건 해줘야하는 문장
            val id: Int = intent?.getIntExtra("id", -1) ?: -1 //재부팅 후 어느 알람인지 확인하기 위해서

            //함수를 호출하는 객체를 이어지는 블록의 인자로 넘기고 결과값 반환
            context?.let {
                val scheduledIntent = Intent(it, AlarmQuizActivity::class.java)
                scheduledIntent.putExtra("id", id) //설정된 알람 데이터 가져옴
                scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) //새로운 태스크를 생성해서, 그 태스트안에 액티비티를 추가
                it.startActivity(scheduledIntent)
            }
        }
    }
}
