package com.alarmapplication.www.ui

import android.app.TimePickerDialog
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.alarmapplication.www.R
import com.alarmapplication.www.ui.dialogs.OnAreaSelectedListener
import com.alarmapplication.www.ui.dialogs.OnSongPlayTypeSelected
import com.alarmapplication.www.ui.dialogs.SelectAreaDialog
import com.alarmapplication.www.ui.dialogs.SelectSongPlayTypeDialog
import com.alarmapplication.www.util.*
import kotlinx.android.synthetic.main.activity_add_alarm.*
import kotlinx.android.synthetic.main.activity_settings.*
import kotlinx.android.synthetic.main.dialog_select_song.*
import java.util.*

class SettingsActivity : AppCompatActivity(), View.OnClickListener {
    //저장된 데이터를 바 옆에 표시해주기 위한 액티비티!
    private var areaType: Int = ASIA  //지역 설정 변수
    private var showIcon: Boolean = true //알람 아이콘 표시 체크
    private var volumeType: Int = NORMAL //음악 재생 방법, 일반 재생인지 점점세게인지

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        supportActionBar?.setDisplayHomeAsUpEnabled(true) //뒤로가기 버튼 활성화화
       supportActionBar?.setHomeButtonEnabled(true)  //action_item으로 작동? 오류해결
        title = "설정" //상단에서 오른쪽 디스켓 모양

        tv_select_area.setOnClickListener(this) //지역 선택 누르면 창 보여주기
        // ★★ setOnclickListener(this) 를 하면 바로 뷰 객체를 받아올 수 있다!

        switch_alarm_icon.setOnCheckedChangeListener{ _, b ->
            showIcon = b   //알람아이콘 깜빡이 변경(on/off)
        }


        when (SettingsManager.playType)
        {
            //SettingsManger의 playType 값에 따라
            NORMAL ->
            {
                tv_song_volume_type.text = "일반 재생"    //volumeType을 정해줌 (일반인지 점점세게인지)
                volumeType = NORMAL
            }
            INCREASE ->
            {
                tv_song_volume_type.text = "점점 세게"
                volumeType = INCREASE
            }
        }

        //음악 재생방법 의 일반 재생을 누르면
        tv_song_volume_type.setOnClickListener {
            SelectSongPlayTypeDialog(this, object : OnSongPlayTypeSelected
            {
                override fun onOkClick(type: Int) {
                    when (type) {
                        NORMAL ->
                        {
                            tv_song_volume_type.text = "일반 재생"
                        }
                        INCREASE ->
                        {
                            tv_song_volume_type.text = "점점 세게"
                        }
                    }
                    volumeType = type
                }
            }).show()
        }

        //나라 타입에 따라서,
        when (SettingsManager.areaType) {
            ASIA ->
            {
                tv_select_area.text = "아시아"
            }
            EUROPE -> {
                tv_select_area.text = "유럽"
            }
            ASIA_AND_EUROPE -> {
                tv_select_area.text = "아시아+유럽"
            }
        }
        switch_alarm_icon.isChecked = SettingsManager.showIcon //체크되는곳에 불들어오게 함
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.menu_save, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when (item.itemId)
        {
            android.R.id.home ->
            {
                finish()
                return true
            }
            R.id.action_save ->  //비스켓 모양 누르면
            {
                saveSettings() // 설정에서 고른 모든 옵션을 저장함
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    //비스켓 모양을 누르면 설정에서 세팅한 내용들을 모두 저장 시켜 준다.
    private fun saveSettings() {
        SettingsManager.areaType = areaType
        SettingsManager.showIcon = showIcon
        SettingsManager.playType = volumeType
        Toast.makeText(this@SettingsActivity, "저장 되었습니다.", Toast.LENGTH_SHORT).show()
    }

    //누르지 않을 떄 옆에 저장되있는 값을 출력시켜놓기 위해서
    override fun onClick(p0: View?)
    {
        p0?.let {
            when (it.id)
            {
                R.id.tv_select_area -> {
                    SelectAreaDialog(this@SettingsActivity, object : OnAreaSelectedListener
                    {
                        override fun onOkClick(type: Int) //SelectAreaDialog.kt 안에 있는 onOkClick 인터페이스 함수
                        {
                            areaType = type 
                            when (type){
                                ASIA -> {
                                    tv_select_area.text = "아시아"
                                }
                                EUROPE -> {
                                    tv_select_area.text = "유럽"
                                }
                                else -> {
                                    tv_select_area.text = "아시아+유럽"
                                }
                            }
                        }
                    }).show()
                }
            }
        }
    }


}