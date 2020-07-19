package com.alarmapplication.www.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.alarmapplication.www.R
import com.alarmapplication.www.data.model.Alarm
import kotlinx.android.synthetic.main.item_alarm.view.*

// RecyclerView 를 사용하기 위해서 필요한것들 - 메인
//어댑터의 역할:리사이클러뷰에 표시될 아이템 뷰를 생성하는 역할, 어댑터가 담당, 사용자 데이터 리스트로부터 아이템 뷰를 만드는 것,

interface OnSwitchCheckedListener {
    fun onSwitchChecked(bool: Boolean, hour: Int, minute: Int, id: Int)
}

class AlarmRecyclerViewAdapter(
    private val onClickListener: OnClickListener,
    private val onSwitchCheckedListener: OnSwitchCheckedListener
) :


//dIffiCallback : 리사이클러뷰를 효율적으로 사용하기 위한 방법이라고 한다. 리스트형태로 
       ListAdapter<Alarm, AlarmRecyclerViewAdapter.AlarmViewHolder>(DiffCallback){

    //Diffutil.ItemCallback<Alarm>() 상속받아 구현
    companion object DiffCallback : DiffUtil.ItemCallback<Alarm>() {
        //아이템이 동일한가의 여부 (areItemsTheSame ->Diffutil 메서드
        override fun areItemsTheSame(oldItem: Alarm, newItem: Alarm): Boolean
        {
            //아이템들이 고유한 ID값을 가질 떄, 해당 메서드를 구현해 비교 처리 한다
            return oldItem === newItem
        }
            ///areContentsTheSame :아이템의 내용이 동인한가의 여부
        override fun areContentsTheSame(oldItem: Alarm, newItem: Alarm): Boolean{
            return oldItem.id == newItem.id
        }
    }

    //리사이클러뷰 어댑터 구현시 필요한 메서드 오버라이드 ( 뷰홀더 객체 생성, 리사이클러뷰 초기화 될 때 호출)
    //item_alarm을 View로 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        return AlarmViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_alarm, parent, false)
        //알람 리스트 디자인 형태 xml 불러옴
        )
    }

    //리사이클러뷰 어댑터 구현시 필요한 메서드 오버라이드 (데이터를 뷰홀더에 바인딩, item_alarm뷰 생성 시호출)
    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int)
    {
        val alarm = getItem(position) //필수구현 부분
        holder.itemView.setOnClickListener{
            onClickListener.onClick(alarm) //
        }
        holder.bind(alarm, onSwitchCheckedListener)
    }


    //화면에 표시될 아이템 뷰를 저장하는 객체 미리 생성된 뷰홀더 객체가 있는 경우, 새로 생성하지 않고
    // 이미 만들어져 있는 뷰홀더를 재활용함, 이 때는 단순히 데이터가 뷰홀더의 아이템 뷰에 바인딩 됨. (bind쓰는이유)
    class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(alarm: Alarm, onSwitchCheckedListener: OnSwitchCheckedListener)
        {
            itemView.tv_name.text = alarm.title    //알람의 이름
            itemView.tv_time.text = "${alarm.hour}시 ${alarm.minute}분" //알람 세팅 시간, 분
            itemView.switch_on_off.isChecked = alarm.isOn //알람 on/off

            //위 디자인 대로 메인 액티비티에 출력된다.
            itemView.switch_on_off.setOnCheckedChangeListener{ _, b ->
                onSwitchCheckedListener.onSwitchChecked(b, alarm.hour, alarm.minute, alarm.id)
            }
        }
    }
}

class OnClickListener(val clickListener: (alarm: Alarm) -> Unit) {
    fun onClick(alarm: Alarm) = clickListener(alarm)
}

