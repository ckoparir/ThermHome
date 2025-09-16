package com.example.thermhome.ui

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import androidx.databinding.DataBindingUtil.*
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.thermhome.R
import com.example.thermhome.databinding.SettingsPageBinding
import com.example.thermhome.repositories.ThermRepository
import com.example.thermhome.util.Converters.Companion.intToFloat
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.*

class SettingsPage : Fragment() {

    private lateinit var binding: SettingsPageBinding
    private var thermRepository: ThermRepository = ThermRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = inflate(inflater, R.layout.settings_page, container, false)

        getConfig()
        initEventHandlers()

        return binding.root
    }

    private fun initEventHandlers() {

        binding.btnHome.setOnClickListener {
            findNavController().navigate(R.id.action_settingsPage_to_mainPage)
        }

        binding.seekBarRate.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = intToFloat(10000, seekBar?.progress!!)
                val str = String.format("%.4f", value)
                binding.textRate.text = str
                binding.config?.rate = value
                if (fromUser)
                    binding.btnSave.visibility = Button.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        binding.seekBarRateLimit.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val value = intToFloat(100, seekBar?.progress!!)
                val str = String.format("%.2f \u2103", value)
                binding.textRateLimit.text = str
                binding.config?.tempSensRate = value
                if (fromUser)
                    binding.btnSave.visibility = Button.VISIBLE
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        binding.swcDayLight.setOnClickListener {
            val switch = it!! as androidx.appcompat.widget.SwitchCompat
            if (switch.isChecked)
                binding.config?.timezone = 2 * 3600
            else
                binding.config?.timezone = 3 * 3600

            binding.btnSave.visibility = Button.VISIBLE
        }

        binding.btnSave.setOnClickListener { saveConfig() }
        binding.btnReset.setOnClickListener { resetConfig() }
    }

    private fun saveConfig() {
        val builder = AlertDialog.Builder(this.context)
        builder.setMessage("Yapılan değişiklikleri kaydetmek istiyor musunuz?")
            .setCancelable(false)
            .setPositiveButton("Evet") { _, _ ->
                postConfig()
                binding.btnSave.visibility = Button.INVISIBLE
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    private fun resetConfig() {
        val builder = AlertDialog.Builder(this.context)
        builder.setMessage("Tüm termostat ayarları ve kayıtlı zaman periodları fabirika ayarlarına dönecek, onaylıyor musunuz?")
            .setCancelable(false)
            .setPositiveButton("Evet") { _, _ ->
                delConfig()
            }
            .setNegativeButton("Hayır") { dialog, _ ->
                dialog.dismiss()
            }
        val alert = builder.create()
        alert.show()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun postConfig() {
        GlobalScope.launch {
            try {
                thermRepository.postConfig(binding.config!!)
            } catch (e: Exception) {
                Snackbar.make(
                    binding.root,
                    resources.getString(R.string.msg_cannot_read_config),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun getConfig() {
        GlobalScope.launch {
            binding.config = thermRepository.getConfig()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun delConfig() {
        GlobalScope.launch {
            thermRepository.delConfig()
            thermRepository.restartMcu()
        }
    }

}
