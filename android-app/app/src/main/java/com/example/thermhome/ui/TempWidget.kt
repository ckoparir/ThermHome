package com.example.thermhome.ui

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.animation.doOnCancel
import androidx.core.animation.doOnStart
import androidx.databinding.DataBindingUtil.*
import androidx.fragment.app.Fragment
import com.example.thermhome.R
import com.example.thermhome.databinding.TempWidgetBinding
import com.example.thermhome.repositories.ThermRepository
import kotlinx.coroutines.*


class TempWidget : Fragment() {

    private lateinit var progress: ObjectAnimator
    private lateinit var binding: TempWidgetBinding
    private var thermRepository: ThermRepository = ThermRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = inflate(inflater, R.layout.temp_widget, container, false)

        initProgress()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkStatus()
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun checkStatus() {
        GlobalScope.launch {
            while (true) {
                activity?.runOnUiThread(Runnable {
                    progress.start()
                })
                while (true) {
                    binding.statusViewModel = thermRepository.getStatus()
                    delay(3000)
                    if (binding.statusViewModel?.temp!! > 0) break
                }
                activity?.runOnUiThread(Runnable {
                    progress.cancel()
                })
                delay(15000)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun initProgress() {

        progress = ObjectAnimator.ofFloat(binding.prgSet, "rotation", 0.0f, 360.0f).apply {
            duration = 750L
            repeatCount = Animation.INFINITE
            interpolator = DecelerateInterpolator()

            doOnStart {
                binding.txtInfo.setText(R.string.reading_temp)
                setProgressDrawable(R.drawable.progressbar_gradient)
            }

            doOnCancel {
                if (binding.statusViewModel?.relayon!!) {
                    binding.txtInfo.setText(R.string.relay_on)
                    setProgressDrawable(R.drawable.progressbar_solid_red)
                } else {
                    binding.txtInfo.setText(R.string.relay_off)
                    setProgressDrawable(R.drawable.progressbar_solid_green)
                }
            }
        }
    }

    private fun setProgressDrawable(@DrawableRes resId: Int) {
        binding.prgSet.progressDrawable = getDrawable(binding.root.context, resId)
    }
}

