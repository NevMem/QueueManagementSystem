package com.nevmem.qms.fragments.join.step

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.nevmem.qms.R
import com.nevmem.qms.fragments.join.JoinStep
import com.nevmem.qms.fragments.join.JoinUsecase
import com.nevmem.qms.status.FetchStatus
import com.yandex.metrica.YandexMetrica
import kotlinx.android.synthetic.main.fragment_step_do_join.*
import org.koin.android.ext.android.inject

class DoJoinStep : JoinStep {
    override fun createFragment(): Fragment = DoJoinFragment()

    companion object {
        class DoJoinFragment : Fragment(R.layout.fragment_step_do_join) {

            private val usecase: JoinUsecase by inject()

            override fun onResume() {
                super.onResume()

                usecase.fetchStatus?.let { status ->
                    if (status is FetchStatus.Success) {
                        status.payload.imageUrl?.let { url ->
                            Glide.with(this)
                                .load(url)
                                .into(image)
                        }

                        image.isVisible = status.payload.imageUrl != null

                        inviteDescription.text = status.payload.description
                    }
                }
            }
        }
    }
}
