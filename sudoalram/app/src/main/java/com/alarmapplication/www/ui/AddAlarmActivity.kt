package com.alarmapplication.www.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.app.TimePickerDialog.OnTimeSetListener
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alarmapplication.www.R
import com.alarmapplication.www.alarm.AlarmReceiver
import com.alarmapplication.www.alarm.DeviceBootReceiver
import com.alarmapplication.www.database.AppDatabase
import com.alarmapplication.www.data.model.Alarm
import com.alarmapplication.www.ui.dialogs.*
import com.alarmapplication.www.util.EMART
import com.alarmapplication.www.util.GGANG
import com.alarmapplication.www.util.LOTTE
import kotlinx.android.synthetic.main.activity_add_alarm.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import java.util.Calendar.*

// 알람을 추가하고자 할 때
class AddAlarmActivity : AppCompatActivity(), View.OnClickListener {

    // 시간, 노래 종류, 진동 여부, 볼륨 세기를 설정 해야 한다.
    private var setHourOfDay: Int = 0   //알람 설정 시간
    private var setMinuteOfHour: Int = 0    //알람 설정 분
    private var songType: Int = LOTTE   //알람노래
    private var isVibrationOn = true    // 알람 울리면서 진동 여부
    private var volumeAmount: Int = 100     //알람 볼륨 양

    //한국의 표준 시간 포맷 처리 (알람 설정하려고 생성)
    private val cal = getInstance(Locale.KOREA)

    private var isNew: Boolean = false // 조건에 따라서 창에 보이게 하거나 사라지게 할 때 씀 (삭제창 의 조건)
    private lateinit var alarm: Alarm // 나중에 초기화 하기 위해 lateinit 사용

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_alarm)

        title = "상세보기"
        //안드로이드 툴바는 기본적으로 뒤로가기 버튼이 없기 때문에, 다음의 문장으로 생기게 가능하다
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   // ( <- 상세보기 ) 형태로 뜸

        //함수를 호출하는 객체를 이어지는 블록의 인자로 넘기고 결과값 반환 알람 추가에 관한 내용
        //Let으로 intent 데이터 넘기기 편하게 가능
        intent?.let {
            isNew = it.getBooleanExtra("isNew", false) //새로운 알람 저장하기 위해서 인텐트에 넣어줌
            if (isNew) {
                button_delete.visibility = View.INVISIBLE //알람 추가일 떄는 삭제창을 안보이게 하고
            } else {
                button_delete.visibility = View.VISIBLE  //알람리스트에서 알람을 누를떄는 삭제창을 보이게한다!~
                button_confirm.text = "수정"

                val itemId = it.getIntExtra("itemId", -1) //

                if (itemId > 0){ //첫번쨰 알람부터 바로적용
                    lifecycleScope.launch(IO) //IO쓰레드에서 작업후 밑에서 withcontext(main)으로, 메인쓰레드에서 보여줌
                    {
                        //알람 정보를 데이터베이스에 등록/ 알람리스트에 올림                             //itemid에 올려줌
                        alarm = AppDatabase.getAppDataBase(this@AddAlarmActivity).alarmDao().getAlarm(itemId)
                        //withContext 메서드를 사용하면 코루틴의 context를 변경가능(범위)
                        withContext(Main) {
                            alarm.run {
                                //알람 등록했던 정보(시간,별명,노래,진동유무,볼륨세기)를 바탕으로 적용한다
                                tv_set_time.text = "$hour 시 $minute 분"
                                tv_set_name.text = title
                                tv_choose_song.text = if (song == EMART){
                                    "이마트"}else if(song == GGANG){
                                        "깡"
                                    } else "롯데월드"
                                switch_vibration.isChecked = isVibrationNeeded
                                tv_alarm_volume.text = "$volume%"

                                setHourOfDay = hour
                                setMinuteOfHour = minute
                                songType = song
                                isVibrationOn = isVibrationNeeded
                                volumeAmount = volume
                            }
                        }
                    }
                }
            }
        }

        tv_set_time.setOnClickListener(this) //알람 시간 설정하기를 누르면 나오는 뷰
        tv_set_name.setOnClickListener(this) //알람 별명 설정하기를 누르면 나오는 뷰
        tv_choose_song.setOnClickListener(this) //알람노래선택 설정하기를 누르면 나오는 뷰
        tv_alarm_volume.setOnClickListener(this) //알람소리세기 숫자를 누르면 나오는 뷰

        button_cancel.setOnClickListener(this) //취소를 누르면 나오는 뷰(메인액티비티)
        button_confirm.setOnClickListener(this) //확인을 누르면 나오는 뷰 (메인)
        button_delete.setOnClickListener(this) //삭제를 눌렀을 때


        switch_vibration.setOnCheckedChangeListener { _, b ->
            isVibrationOn = b
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //addalarm 리스너의 onClick메서드
    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                // 알람시간 -설정하기  누르면
                R.id.tv_set_time -> {
                    TimePickerDialog(  //시간선택창 띄워줌 (TimePicker)
                        this@AddAlarmActivity, //TimePickerDialog 첫번째 매개변수 Activity
                        OnTimeSetListener{ _, hourOfDay, minute ->  //TimPickerDialog 두번째 매개변수, onTimeSetListener
                            tv_set_time.text = "$hourOfDay 시 $minute 분"  //설정버튼누르면, 콜백됨
                            setHourOfDay = hourOfDay   //시간 전달받고
                            setMinuteOfHour = minute  // 분 전달받는다
                        },
                        cal.get(Calendar.HOUR_OF_DAY), //다시 누를 때 최초 바라본 시간 나타내기
                        cal.get(Calendar.MINUTE), // 다시 누를 때 최초 바라본 분  나타내기
                        false //24시간 모드를 사용할 것인지, AM, PM 구분용
                    ).show()
                }

                //알람 별명 =>설정하기 눌렀을때
                R.id.tv_set_name ->
                {           //익명 클래스
                    NicknameDialog(this,object:
                        OnNickNameSetListener{
                            override fun onOkClick(nickName: String) {
                                //String is nickname
                                tv_set_name.text = nickName //NicknameDialog에서 입력받은 이름을, 알람에 사용
                            }
                        }).show()
                }

                //알람노래선택 => 설정하기
                R.id.tv_choose_song -> {
                    SelectMusicDialog(
                        this,  object :
                            OnMusicSelectedListener {  //SelectMusicDialog에서 받은 type값을 사용한다
                            override fun onOkClick(type: Int) {
                                if (type == LOTTE) {
                                    tv_choose_song.text = "환상의 나라"
                                    songType = LOTTE
                                }else if(type == GGANG){
                                    tv_choose_song.text = "깡"
                                    songType = GGANG
                                } else{
                                    tv_choose_song.text = "이마트"
                                    songType = EMART
                                }
                            }
                        }).show()
                }

                //알람 소리세기, 퍼센트 부분을 눌렀을 때
                R.id.tv_alarm_volume -> {
                    VolumeDialog(this, object : OnVolumeSetListener {
                        override fun onOkClick(volume: Int) { //VolumeDialog에서 전달받은 Volume을 표시한다.
                            tv_alarm_volume.text = "$volume%"
                            volumeAmount = volume
                        }
                    }).show()
                }
                R.id.button_cancel -> {
                    finish()
                }

                //알람리스트에서 노래를 선택하고 삭제버튼을 눌렀을 때 (추가하기 에서 누르는거 아님)
                R.id.button_delete ->{
                    if (::alarm.isInitialized) { //알람리스트에 있는 알람임을 확인(초기화된 상태니깐)
                        //io쓰레드에서 정보삭제
                        lifecycleScope.launch(IO) {
                            val value = AppDatabase.getAppDataBase(this@AddAlarmActivity).alarmDao().delete(alarm) //다시 데이터베이스를 불러서, 해당 알람을 삭제함
                            if (value == 1) {
                                //메인 쓰레드에서 처리
                                withContext(Main) {
                                    Toast.makeText(this@AddAlarmActivity, "삭제 되었습니다.", Toast.LENGTH_SHORT).show()
                                    finish()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "에러 발생!", Toast.LENGTH_SHORT).show()
                    }
                }

                //★ 확인을 누를때
                R.id.button_confirm ->{
                    if (isNew) {
                        //알람을 새롭게 추가 했으면
                        val alarm = Alarm(  //알람리스트에 알람이 새로 쌓이게되므로 (새알람이 생성됨)
                                hour = setHourOfDay,  //데이터베이스 Alarm에 들어갈 정보를 넣어줘서, 저장해야한다.
                                minute = setMinuteOfHour,
                                title = tv_set_name.text.toString(),
                                song = songType,
                                isVibrationNeeded = isVibrationOn,
                                volume = volumeAmount,
                                isOn = true
                            )
                        //IO에서 작업 - 알람 추가(삽입)
                        lifecycleScope.launch(IO) {
                            val result = AppDatabase.getAppDataBase(this@AddAlarmActivity).alarmDao().insertAlarm(alarm)
                            //Alarm에 정보를 넣은후, 데이터베이스에 inset한다.
                            if (result > 0) { //성공적으로 추가됬을 때
                                //메인쓰레드에서 , 추가된걸 메인액티비티 화면에 보여줌
                                withContext(Main) {
                                    Toast.makeText(
                                        this@AddAlarmActivity,
                                        "알람이 추가되었습니다.", //문구를 띄워주면서
                                        Toast.LENGTH_SHORT
                                    ).show() //메인을 보여준다
                                    setAlarm(setHourOfDay, setMinuteOfHour, result.toInt())
                                    //저장된 알람을 설정하는 setAlarm 함수
                                    finish()
                                }
                            }
                        }
                    } else  {
                        //확인을 눌렀는데 isNew 가 아니면, 수정한게 되므로 수정을 나타낸다
                        if (::alarm.isInitialized) { //수정한 내용을 다시 초기화
                            alarm.hour = setHourOfDay
                            alarm.minute = setMinuteOfHour
                            alarm.title = tv_set_name.text.toString()
                            alarm.song = songType
                            alarm.isVibrationNeeded = isVibrationOn
                            alarm.volume = volumeAmount

                            lifecycleScope.launch(IO) {
                                //수정한 알람을, 데이터베이스에 원래있던 자리에 업데이트(수정) 시킨다.
                                AppDatabase.getAppDataBase(this@AddAlarmActivity).alarmDao().update(alarm)
                                withContext(Main) {
                                    Toast.makeText(
                                        this@AddAlarmActivity,
                                        "알람이 수정 되었습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    setAlarm(alarm.hour, alarm.minute, alarm.id) //바뀐 알람 시간,분, 순서를 설정한다
                                    finish()
                                }
                            }
                        } else {
                            Toast.makeText(this, "에러 발생!", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    //알람 설정 함수
    private fun setAlarm(hour: Int, minute: Int, id: Int) {
        val calendar = getInstance(Locale.KOREA)  //한국 시간 기준
        calendar.set(HOUR_OF_DAY, hour)
        calendar.set(MINUTE, minute)

        val pm = this.packageManager //패키지 관리자 인스턴스를 얻을 수 있음 (알람 정보 출처 참고!)
        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        val alarmIntent = Intent(this, AlarmReceiver::class.java)

        alarmIntent.putExtra("id", id) //해당 알람 데이터 저장
        val pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, 0)
        //무조건 써야함 알람을 구분할 고유값, 알람이 울릴때 이동할 클래스의 정보와 변수값 저장

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        //무조건 써야함 알람시스템 작동

        //     alarmManager.setRepeating(
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, //안드로이드가 절전 모드에 있을 때 알람을 울리게 하기 위해 필요(폰전원잠겨있을떄)등
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(   //호완이 되게 하려고??, 사용 불투명함
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                pendingIntent
            )
        }

        // 부팅 후 실행되는 리시버 사용가능하게 설정
        pm.setComponentEnabledSetting(
            receiver,
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
    }
}