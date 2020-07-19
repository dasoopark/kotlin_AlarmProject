package com.alarmapplication.www.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.alarmapplication.www.util.LOTTE

//Room 라이브러리 형태 , 저장소의 역할

//데이터베이스 테이블 이름 = alarm
@Entity(tableName = "alarm")

//data class : 데이터는 보유하지만, 아무것도 제공하고 싶지 않을 때 ( 출처:[kotlin] Data Classes) - 주로 데이터베이스 용도
data class Alarm(

    @PrimaryKey(autoGenerate = true) //각각의 Entity는 고유 식별자인 PrimaryKey(기본키)가 무조건 필요하다!!
    @ColumnInfo(name = "id")           //쓸 일이 딱히 없다면 autoGenerate를 통해서 자동으로 생성되게한다!
    val id: Int=0, //알람등록된 번호를 뜻함

    //이제부터 alarm 엔티티가 저장할 내용들
    /*
    알람 추가 시 나열되는 행
    울리는 알람 시간(시,분), 알람 별명, 알람 노래, 진동 여부, 알람 소리 세기, 온 오프여부
     */
    @ColumnInfo(name = "hour")
    var hour: Int,

    @ColumnInfo(name = "minute")
    var minute: Int,

    @ColumnInfo(name = "title")
    var title: String,

    @ColumnInfo(name = "song")
    var song: Int= LOTTE,

    @ColumnInfo(name = "isVibrationNeeded") //진동 울리는 여부
    var isVibrationNeeded: Boolean,

    @ColumnInfo(name = "volume")
    var volume: Int,

    @ColumnInfo(name = "isOn")
    var isOn: Boolean = true
)
