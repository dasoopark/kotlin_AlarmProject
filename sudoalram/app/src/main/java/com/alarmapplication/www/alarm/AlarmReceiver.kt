package com.alarmapplication.www.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.alarmapplication.www.database.AppDatabase
import com.alarmapplication.www.ui.AlarmQuizActivity
import com.alarmapplication.www.ui.MainActivity
import com.alarmapplication.www.ui.SettingsActivity
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

//BroadcastReceiver : 단말기 안에서 행해지는 수 많은 일들을 대신해서 알려주는 방송
class AlarmReceiver : BroadcastReceiver()
{

    //intent-filter을 통해 걸러진 intent를 받아들이는 곳이다,
    override fun onReceive(p0: Context?, p1: Intent?) {
        val id: Int = p1?.getIntExtra("id", -1) ?: -1

        p0?.let {
            //★★ 코루틴을 통해 비동기적으로 프로그래밍이 가능하게 해준다. 알람이 울려도, 핸드폰은 딴거 해야하고
            // 메인액티비티등은 있어야 하니깐 필요함
            GlobalScope.launch(IO) {
                //괄호 안의 코드들이 비동기적으로 수행되게 됨, IO쓰레드 = > 데이터베이스 처리
                val alarm = AppDatabase.getAppDataBase(p0).alarmDao().getAlarm(id) //알람리스트로부터 알람id를 얻어와서

                if(!alarm.isOn){
                    return@launch  
                }
                //알람이 켜질 때
                else{  
                    val cal = Calendar.getInstance(Locale.KOREA)    
                    val hour = cal.get(Calendar.HOUR_OF_DAY)
                    val minute=cal.get(Calendar.MINUTE)
                    if(alarm.hour == hour && alarm.minute==minute)  //★★★현재 시간이 , 알람리스트에서 저장된 시간과 같아지게되면
                    {
                        //메인이 되어, 알람퀴즈를 푸는 액티비티로 화면이 전환된다. 그리고 알람이 울린다, Main쓰레드에서 작업, 보여줌
                           withContext(Main) {
                            val scheduledIntent = Intent(it, AlarmQuizActivity::class.java) //알람퀴즈 푸는 화면 =>리시버로 띄움 ㅈ
                            scheduledIntent.putExtra("id", id) //알람에 대한 정보 넣음 
                            scheduledIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  //새로운 태스크에 생성
                            it.startActivity(scheduledIntent)
                        }
                    }
                }
            }
        }

    }

}