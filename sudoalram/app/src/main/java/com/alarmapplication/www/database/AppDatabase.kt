package com.alarmapplication.www.database

import android.content.Context
import androidx.room.*
import com.alarmapplication.www.data.model.Alarm

// AlamrmDao 메소드를 가지고 있는 Alarmm 엔티티의 데이터 베이스라고 생각하자
//Room데이터베이스에서 데이터베이스를 생성하고 버전을 관리한다.
//Room은 SQL을 객체로 매핑해 주는 도구, Room을 이용해 데이터베이스를 염

@Database(entities = [Alarm::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun alarmDao(): AlarmDao

    companion object { //companion 객체는 한번만 쓸때~ 모프 강의자료 참고할 것
        val DATABASE_NAME: String = "alarm_db"  // 앞으로 사용할 데이터베이스 이름
        var INSTANCE: AppDatabase? = null

        //MainActivity에서 getAppDateBase 함수를 호출하여 데이터베이스 객체를 반환,삭제할 수 있도록 메소드 생성
        fun getAppDataBase(context: Context): AppDatabase {

            if (INSTANCE == null) {                     // 여기서 return 까지는 필수적인 내용이므로 크게 신경X
                synchronized(AppDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        AppDatabase::class.java,
                        DATABASE_NAME
                    ).fallbackToDestructiveMigration().build()
                }
            }
            return INSTANCE!!
        }
    }

}