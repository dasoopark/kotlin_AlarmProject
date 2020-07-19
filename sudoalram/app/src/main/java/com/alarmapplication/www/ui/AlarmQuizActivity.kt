package com.alarmapplication.www.ui

import android.content.Context
import android.media.MediaPlayer
import android.media.ToneGenerator.MAX_VOLUME
import android.os.Bundle
import android.os.Vibrator
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.alarmapplication.www.R
import com.alarmapplication.www.data.asiaCountries
import com.alarmapplication.www.data.europeCountries
import com.alarmapplication.www.data.model.Alarm
import com.alarmapplication.www.database.AppDatabase
import com.alarmapplication.www.util.*
import kotlinx.android.synthetic.main.activity_alarm_quiz.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ln


class AlarmQuizActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var mPlayer: MediaPlayer
    private lateinit var alarm: Alarm
    private lateinit var answer: String //수도 질문
    private var giveUpCount = 0
    private var volume = 0f
    private val maxVolume=100


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //알람이 울리면, 타이틀바와 상태바를 없애야 하는데 이를 구현해주는 코드

        setContentView(R.layout.activity_alarm_quiz)
        supportActionBar?.hide() //마찬가지로 액션바도 가려서 완전히 알람에 집중하게 한다

        intent?.let{
            val id = it.getIntExtra("id", -1) //AddAlarmActivity =>setalarm에서 받은 저장한인텐트 데이터
            if (id < 0) return
            //알람리시버가 Globscope , IO쓰레드에서 알람 데이터베이스 해당 알람얻음
            lifecycleScope.launch(IO){
                alarm = AppDatabase.getAppDataBase(this@AlarmQuizActivity).alarmDao().getAlarm(id)
                //데이터베이스에서 해당 알람 정보를 받아온다.
                
                //메인쓰레드에서 액티비티에 출력
                withContext(Main) {
                    if (::alarm.isInitialized) {
//                        if(!alarm.isOn) finish()
//                        else
                        setQuizView(alarm) //설정한 뷰가 맞으면, 퀴즈뷰를 띄워올린다.
                    }
                }
            }
        }

        btn_give_up.setOnClickListener(this) //포기하기 버튼
        btn_submit.setOnClickListener(this) //제출 버튼
        showCurrentTime() // 제출밑에 현재 시간 표현하기 위해
    }

    //퀴즈 제출/풀이 함수
    private fun setQuizView(alarm: Alarm)
    {
        if(alarm.isVibrationNeeded)
        {
            val v = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator   //데이터베이스에서 읽은 알람 상태가 진동모드일떄 울리게함
            lifecycleScope.launch(Main) //진동하면서 비동기적으로 여러개 동시에 (소리도 동시에 나게하고 등등)
            {
                while(true) {
                    v.vibrate(500)
                    delay(1000)  //진동간격
                }
            }
        }
        val type = alarm.song //저장된 노래의 타입을 가져옴

         mPlayer  = if (type == LOTTE)
         {
            MediaPlayer.create(this@AlarmQuizActivity, R.raw.lotte)
        }else if(type == GGANG){
             MediaPlayer.create(this@AlarmQuizActivity, R.raw.ggang)
         }else {
            MediaPlayer.create(this@AlarmQuizActivity, R.raw.emart)
        }
        if(::mPlayer.isInitialized)
        {
            mPlayer.isLooping = true //노래를 계속 반복시키게 하기
            if(SettingsManager.playType== INCREASE){  //볼륨 점점 세게를 선택했다면.
                startFadeIn(alarm)

            }else{ //점점세게가 아니라면,
                mPlayer.setVolume(maxVolume.toFloat(), maxVolume.toFloat())
                when(alarm.volume)
                {
                    SMALL_VOLUME->{
                        val volume =(1 - ln((MAX_VOLUME - SMALL_VOLUME).toDouble()) / ln(MAX_VOLUME.toDouble())).toFloat()
                        mPlayer.setVolume(volume, volume)
                    }
                    NORMAL_VOLUME->{
                        val volume = (1 - ln((MAX_VOLUME - NORMAL_VOLUME).toDouble()) / ln(MAX_VOLUME.toDouble())).toFloat()
                        mPlayer.setVolume(volume, volume)
                    }
                    BIG_VOLUME->{
                        val volume =  (1 - ln((MAX_VOLUME - BIG_VOLUME).toDouble()) / ln(MAX_VOLUME.toDouble())).toFloat()
                        mPlayer.setVolume(volume, volume)
                    }
                    BIGGEST_VOLUME->{
                        val volume =(1 - ln((MAX_VOLUME - (BIGGEST_VOLUME-1)).toDouble()) / ln(MAX_VOLUME.toDouble())).toFloat()
                        mPlayer.setVolume(volume, volume)
                    }
                }
            }

            mPlayer.start()
        }

        val randomNumber: Int
        lateinit var countryItem: Triple<Int, String, String> //data-> CountryDates.kt에 있는 나라들 불러오기

        when(SettingsManager.areaType) //선택된 나라 범위에 따라서
        {
            ASIA->{
                        //indices =>인덱스를 가지고 배열이나,리스트를 반복할 떄 사용
                randomNumber = (asiaCountries.indices).random()
                countryItem = asiaCountries[randomNumber]
            }
            EUROPE->{
                randomNumber = (europeCountries.indices).random()
                countryItem = europeCountries[randomNumber]
            }
            ASIA_AND_EUROPE->{
                val addedCountryList = asiaCountries.plus(europeCountries) //리스트 합침
                randomNumber = (addedCountryList.indices).random()
                countryItem = addedCountryList[randomNumber]
            }
        }
                        //리스트 매개변수 처음에는 깃발사진들어가있다
        iv_flag.setImageResource(countryItem.first)
        tv_country_name.text = countryItem.second //두번째 매개변수에는 나라이름
        answer = countryItem.third //세번째 매개변수에는 수도
    }


    //볼륨 소리 점점 커지게 하는 함수
    private fun startFadeIn(alarm: Alarm)
    {
        val FADE_DURATION = 5000
        val FADE_INTERVAL = 250L
        val MAX_VOLUME = (1 - ln((MAX_VOLUME - (alarm.volume-1)).toDouble()) / ln(MAX_VOLUME.toDouble())).toFloat()
        val numberOfSteps = FADE_DURATION / FADE_INTERVAL //Calculate the number of fade steps
        val deltaVolume = MAX_VOLUME / numberOfSteps.toFloat()

        val timer = Timer(true) //타이머 기능을 구현하기 위해 Timer필요
        val timerTask: TimerTask = object : TimerTask() //타이머 기능을 구현하기 위해 TimerTask 필요,
        {
            override fun run() { //추상클래스라 run메서드 세팅필요
                fadeInStep(deltaVolume)
                if (volume >= 1f) {
                    timer.cancel()
                    timer.purge()
                }
            }
        }
            //작업을 일정한 시간 FADE_INTERVAL 이후에 실행하며, FADE_INTERVAL간격마다 작업을 반복
             //일정하게 소리커지게 하려면 필요함.
        timer.schedule(timerTask, FADE_INTERVAL, FADE_INTERVAL)
    }

    //소리 점점 커지게 하는 함수 (볼륨조정)
    private fun fadeInStep(deltaVolume: Float)
    {
        mPlayer.setVolume(volume, volume)
        volume += deltaVolume
    }


    override fun onBackPressed() {
    }

    //여러가지 키 이벤트 발생시, 단말기 특정 작업 처리
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {  //뒤로가기버튼
                true
            }
            KeyEvent.KEYCODE_HOME -> { //홈버튼
                true
            }
            KeyEvent.KEYCODE_MENU -> { //메뉴버튼
                true
            }
            else -> super.onKeyDown(keyCode, event)
        }
    }


    //AlarmQuizActivity 리스너의 onClick
    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                //포기하기 버튼 누를시
                R.id.btn_give_up -> {
                    giveUpCount = giveUpCount.inc()
                    Toast.makeText(this, "남은 포기횟수: ${5-giveUpCount}", Toast.LENGTH_SHORT).show()
                    if(giveUpCount>4){
                        if(::mPlayer.isInitialized) {
                            mPlayer.stop()
                        }
                        Toast.makeText(this, "포기하였습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                //문제 제출
                R.id.btn_submit -> {
                    val typedAnswer = edt_capital.text.toString() //문제 적은거 입력받아서 비교
                    if(typedAnswer.isEmpty()){
                        Toast.makeText(this, "답을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        return
                    }
                    if(::answer.isInitialized) {
                        if (typedAnswer == answer) {
                            //correct
                            if(::mPlayer.isInitialized) {
                                mPlayer.stop()
                            }
                            Toast.makeText(this, "정답입니다.", Toast.LENGTH_SHORT).show()
                            finish()
                        }else{
                            Toast.makeText(this, "오답입니다.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                else -> {

                }
            }
        }
    }

    private fun showCurrentTime(){
        lifecycleScope.launch {
            //알람 울리면서
            withContext(Main) {
                //메인스레드에서 시계 보여줌
                while(true) {
                    val c: Calendar = Calendar.getInstance()
                    val df = SimpleDateFormat("yyyy년 MM월 dd일 HH시 mm분 ss초")
                    val formattedDate = df.format(c.time)
                    tv_current_time.text = "현재 시간: $formattedDate"
                    delay(1000)
                }
            }
        }
    }
}