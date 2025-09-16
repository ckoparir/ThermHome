package com.example.thermhome.ui.adapter

import android.app.TimePickerDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TimePicker
import androidx.recyclerview.widget.RecyclerView
import com.example.thermhome.R
import com.example.thermhome.data.TempConfig
import com.example.thermhome.ui.IExternalMethod
import com.example.thermhome.ui.NumberDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial


class ScheduleAdapter(private val schedules: ArrayList<TempConfig.TempSchedule>) : RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder>() {

    private lateinit var recyclerView: RecyclerView

    class ScheduleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val btnEnd: Button = itemView.findViewById(R.id.btnEnd)
        val btnTemp: Button = itemView.findViewById(R.id.btnTemp)
        val btnStart: Button = itemView.findViewById(R.id.btnStart)
        val btnDelete: ImageButton = itemView.findViewById(R.id.btnDelete)
        val swcEnable: SwitchMaterial = itemView.findViewById(R.id.swcEnable)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ScheduleViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.schedule, parent, false)
        return ScheduleViewHolder(view)
    }

    override fun getItemCount(): Int {
        return schedules.size
    }

    override fun onBindViewHolder(holder: ScheduleViewHolder, position: Int) {
        val schedule = schedules[position]
        holder.btnTemp.text = String.format("%.2f \u2103", schedule.set)
        holder.btnStart.text = String.format("%02d:%02d", schedule.start.hrs, schedule.start.min)
        holder.btnEnd.text = String.format("%02d:%02d", schedule.end.hrs, schedule.end.min)
        holder.swcEnable.isChecked = schedule.scheduled

        holder.btnEnd.setOnClickListener { btnEndOnClick(schedule, position) }
        holder.btnTemp.setOnClickListener { btnTempOnClick(holder.btnTemp, schedule, position) }
        holder.btnDelete.setOnClickListener { btnDeleteOnClick(schedule, position) }
        holder.btnStart.setOnClickListener { btnStartOnClick(schedule, position) }
        holder.swcEnable.setOnCheckedChangeListener { _, isChecked ->
            if (!recyclerView.isComputingLayout) {
                schedule.scheduled = isChecked
                notifyItemRangeChanged(position, schedules.size)
            }
        }
    }

    private fun btnTempOnClick(button: Button, schedule: TempConfig.TempSchedule, index: Int) {
        val dialog = NumberDialog(this.recyclerView.rootView.context)
        dialog.show(button, object : IExternalMethod {
            override fun externalMethod() {
                schedule.set = (button.text as String).toDouble()
                notifyItemRangeChanged(index, schedules.size)
            }
        })
    }

    private fun btnDeleteOnClick(schedule: TempConfig.TempSchedule, index: Int) {
        if (schedules.size == 1) {
            Snackbar.make(this.recyclerView, recyclerView.resources.getString(R.string.msg_cannot_delete), Snackbar.LENGTH_SHORT).show()
            return
        }
        schedules.remove(schedule)
        notifyItemRangeChanged(index, schedules.size)
    }

    private fun btnStartOnClick(schedule: TempConfig.TempSchedule, index: Int) {
        openDateTimeDialog(schedule.start, index)
    }

    private fun btnEndOnClick(schedule: TempConfig.TempSchedule, index: Int) {
        openDateTimeDialog(schedule.end, index)
    }

    private fun openDateTimeDialog(dateTime: TempConfig.TempSchedule.DateTimeData, index: Int) {
        val dialog = TimePickerDialog(
            this.recyclerView.rootView.context,
            { _: TimePicker, hrs: Int, min: Int ->
                dateTime.hrs = hrs
                dateTime.min = min
                notifyItemRangeChanged(index, schedules.size)
            }, dateTime.hrs, dateTime.min, true
        )
        dialog.show()
    }
}

