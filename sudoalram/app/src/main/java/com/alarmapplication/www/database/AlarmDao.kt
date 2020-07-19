package com.alarmapplication.www.database

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.alarmapplication.www.data.model.Alarm

//Dao란 Database Access Object, 데이터베이스에 접근해서 실질적으로 insert, delete 등을 수행하는
// 메소드를 포함한당 즉 DAO란 데이터 베이스에 접근해서, 질의를 수행하는거


//인터페이스로 처리하고, AlamDao_impl 에서 구현한다.
@Dao //어노테이션 교수님 강의 자료 참고 (15강)
interface AlarmDao{

    @Insert(onConflict = REPLACE)  //onConflict = REPLACE 고유키 겹치면 덮어쓰는 기능이라고 한다
    fun insertAlarm(alarm: Alarm): Long     //오른쪽은 엔티티 이름

    @Delete
    fun delete(alarm: Alarm): Int

    @Query("DELETE From alarm") //알람 엔티티에서 지울거
    fun deleteAll()

    @Update
    fun update(alarm: Alarm)

    @Query("UPDATE alarm SET isOn =:isOn WHERE id=:id") //알람 엔티티에서  알람 껏다 켯다 수정하는거
    fun updateIsOn(isOn: Boolean, id: Int)

    @Query("SELECT * FROM alarm")  //모든 알람에 대한 정보
    fun getAll(): List<Alarm>

    @Query("SELECT * FROM alarm WHERE id = :id") //원하는 알람 ( 리스트에 있는거 클릭했을 때) 얻을 수있는 알람정보
    fun getAlarm(id: Int): Alarm
}