package com.example.thermhome.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import com.example.thermhome.R
import com.example.thermhome.constants.App.TAG
import com.example.thermhome.data.TempConfig
import com.example.thermhome.databinding.MainPageBinding
import com.example.thermhome.repositories.ThermRepository
import com.example.thermhome.ui.adapter.ScheduleAdapter
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class MainPage : Fragment() {

    private var tempConfig: TempConfig? = null
    private lateinit var binding: MainPageBinding
    private lateinit var scheduleDataObserver: AdapterDataObserver
    private var thermRepository: ThermRepository = ThermRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.main_page, container, false)

        binding.lstSchedule.setHasFixedSize(true)
        binding.lstSchedule.layoutManager = LinearLayoutManager(this.context)

        initEvents()
        getConfig()

        return binding.root
    }

    private fun initEvents() {

        binding.btnRefresh.setOnClickListener { onClickRefreshButton() }
        binding.btnAdd.setOnClickListener { onClickAddButton() }
        binding.btnSettings.setOnClickListener { onClickSettingsButton() }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getConfig() {

        GlobalScope.launch {
            try {
                tempConfig = thermRepository.getConfig()
                updateScheduleAdapter()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    private fun updateScheduleAdapter() {

        requireActivity().runOnUiThread {

            val adapter = ScheduleAdapter((tempConfig ?: TempConfig()).tempSchedule)

            binding.lstSchedule.adapter = adapter

            scheduleDataObserver = object : AdapterDataObserver() {
                override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                    super.onItemRangeChanged(positionStart, itemCount)
                    postConfig(adapter.itemCount)
                }

                override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                    super.onItemRangeInserted(positionStart, itemCount)
                    postConfig(adapter.itemCount)
                }
            }
            adapter.registerAdapterDataObserver(scheduleDataObserver)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun postConfig(newSize: Int) {
        tempConfig?.size = newSize
        GlobalScope.launch {
            thermRepository.postConfig(tempConfig ?: TempConfig())
        }
    }

    private fun onClickSettingsButton() {
        findNavController().navigate(R.id.action_mainPage_to_settingsPage)
    }

    private fun onClickAddButton() {
        try {
            tempConfig = tempConfig ?: TempConfig()
            if (tempConfig!!.tempSchedule.size > 3) {
                Snackbar.make(binding.mainPage, resources.getString(R.string.msg_cannot_insert), Snackbar.LENGTH_SHORT).show()
                return
            }
            tempConfig!!.tempSchedule.add(TempConfig.TempSchedule())
            val adapter = binding.lstSchedule.adapter!!
            val index = adapter.itemCount
            adapter.notifyItemInserted(index)
        } catch (e: Exception) {
            Log.e(TAG, e.message.toString())
        }
    }

    private fun onClickRefreshButton() {
        getConfig()
    }
}
