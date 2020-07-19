package com.alarmapplication.www.ui

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.alarmapplication.www.R
import com.alarmapplication.www.alarm.AlarmReceiver
import com.alarmapplication.www.alarm.DeviceBootReceiver
import com.alarmapplication.www.database.AppDatabase
import com.alarmapplication.www.data.model.Alarm
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*


class MainActivity : AppCompatActivity(), View.OnClickListener {

    //★★★★★ Room 데이터 베이스를 사용했기 때문에
    // RecyclerView와 Adapter 를 사용해야 함  각 View 마다 Room의 데이터를 뿌려줘야 하므로
    // 앱에서 대량의 데이터, 자주 변경되는 데이터 기반한 요소의 스크롤 목록 표시용 (알람리스트)

    private lateinit var alarmRecyclerViewAdapter: AlarmRecyclerViewAdapter     //편의상 lateinit (오류뜸)
    private var alarmList = mutableListOf<Alarm>() //데이터베이스에 저장된 알람을 mutableListOf형태로 저장

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title = "수도알람"
        initView()
    }

    // 시작 되고 보이는 화면
    private fun initView() {
        ll_add_alarm.setOnClickListener(this)  // 플러스 버튼
        ll_remove_all_alarms.setOnClickListener(this) //취소버튼

       // apply 함수를 호출하는 객체를 블록의 리시버로 전달하고, 객체 자체를 반환한다.
        recyclerView.apply{
            //리사이클러 뷰를 위한 어댑터 받음
            alarmRecyclerViewAdapter = AlarmRecyclerViewAdapter(OnClickListener {
                val intent = Intent(this@MainActivity, AddAlarmActivity::class.java)
                //addAlarmActivity에서 인텐트 저장한 데이터 가져와야함
                intent.putExtra("isNew", false) //새로운 알람 등록될 때
                intent.putExtra("itemId", it.id) //알람 아이템 리스트 목록 (몇번째알람)
                startActivity(intent)
            },
                object: OnSwitchCheckedListener{
                    //알람을 킬려고 초록불을 켜두었을때 알람의 정보를 (껏다 키면, 알람이 켜진 사실을 알려야 한다)
                override fun onSwitchChecked(bool: Boolean, hour: Int, minute: Int, id: Int) {
                    if(bool){
                        lifecycleScope.launch(IO){
                            //true면, io쓰레드에서(데이터베이스 환경, 알람의 상태가 켜진것을 전해줘야함 (업데이트) )
                            AppDatabase.getAppDataBase(this@MainActivity).alarmDao().updateIsOn(true, id)
                            withContext(Main) {
                                //메인 쓰레드에서도, (리사이클러뷰 화면) 켜진 알람의 내용 설정
                                setAlarm(hour, minute, id) //알람이 꺼졋다 켜졌을때, 다시 알람을 설정해서 실행되게 한다
                            }
                        }
                    }else{ //알람을 꺼뒀을 때
                        lifecycleScope.launch(IO){
                            AppDatabase.getAppDataBase(this@MainActivity).alarmDao().updateIsOn(false, id)
                        }
                    }
                }
            })
            //리사이클러뷰의 레이아웃 매니저 ( 아이템뷰를 어떻게 레이아웃할지)
            layoutManager = LinearLayoutManager(this@MainActivity)
            val dividerItemDecoration = DividerItemDecoration(this@MainActivity, VERTICAL)
            addItemDecoration(dividerItemDecoration)
            adapter = alarmRecyclerViewAdapter
        }
    }
//리스너 구현해야할 이벤트 메서드
    override fun onClick(v: View?) {
        v?.let {
            when (it.id)
            {
                //알람 추가 버튼을 누르면
                R.id.ll_add_alarm -> {
                    val intent = Intent(this, AddAlarmActivity::class.java)
                    intent.putExtra("isNew", true) //AddAlamActivity를 실행시키는 암호 전달
                    startActivity(intent) //인텐트로 화면전환 => 알람추가 창
                }
                //알람 모두 제거 버튼을 누르면
                R.id.ll_remove_all_alarms -> {
                    lifecycleScope.launch(IO) {
                        //io쓰레드에서, 데이터베이스에 저장된 알람들을 모두 지운다
                        AppDatabase.getAppDataBase(this@MainActivity).alarmDao().deleteAll()
                        fetchAlarmsFromDb() //아이템에 등록 (빈화면)
                    }
                    Toast.makeText(this, "전체 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                }
                else -> {

                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::alarmRecyclerViewAdapter.isInitialized) {
            fetchAlarmsFromDb()
        }
    }
    //데이터베이스에 있는 알람들을 가져옴
    private fun fetchAlarmsFromDb(){
        lifecycleScope.launch(IO) {
            //io쓰레드에서 데이터베이스에 저장된 알람 mutablelist형태로 가져옴
            alarmList = AppDatabase.getAppDataBase(this@MainActivity).alarmDao().getAll().toMutableList()
            withContext(Main) {
                //메인 쓰레드에서 데이터베이스에 등록된 알람 모두 리스트에 등록 
                alarmRecyclerViewAdapter.submitList(alarmList)
            }
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.settings, menu);
        return true;
    }

    //톱니바퀴 모양 (설정)
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        if (item.itemId == R.id.action_setting) {
            startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item);
    }


    //알람 설정하는 함수 AddAlarmActivity 마지막에서 사용한 형태와 같음, 주석 중복되므로 표기X
    private fun setAlarm(hour: Int, minute: Int, id: Int) {
        val calendar = Calendar.getInstance(Locale.KOREA)
        calendar.set(Calendar.HOUR_OF_DAY, hour)
        calendar.set(Calendar.MINUTE, minute)

        val pm = this.packageManager
        val receiver = ComponentName(this, DeviceBootReceiver::class.java)
        val alarmIntent = Intent(this, AlarmReceiver::class.java)
        alarmIntent.putExtra("id", id)
        val pendingIntent = PendingIntent.getBroadcast(this, id, alarmIntent, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        //     alarmManager.setRepeating(
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY, pendingIntent
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
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