package com.nevmem.qms.rating.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.nevmem.qms.common.utils.runOnUi
import com.nevmem.qms.rating.R
import com.nevmem.qms.rating.Rating
import com.nevmem.qms.rating.RatingsManager
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

class RatingView : LinearLayoutCompat {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var ratingsManager: RatingsManager

    private var ratingId: String? = null

    private var job: Job? = null

    private var rating: Rating? = null
        set(value) {
            if (field == value)
                return
            field = value
            updateUi()
        }

    private val textColor: Int
        get() {
            val nonNull = rating
            if (nonNull?.score == null) {
                return Color.WHITE
            }
            if (nonNull.score > nonNull.maxScore * 0.9)
                return context.getColor(R.color.successColor)
            if (nonNull.score > nonNull.maxScore * 0.7)
                return Color.WHITE
            return context.getColor(R.color.errorColor)
        }

    private val text: AppCompatTextView
        get() = findViewById(R.id.ratingText)

    private val icon: ImageView
        get() = findViewById(R.id.icon)

    override fun onFinishInflate() {
        super.onFinishInflate()
        LayoutInflater.from(context).inflate(R.layout.layout_rating_view, this, true)
        orientation = HORIZONTAL
        gravity = Gravity.CENTER_VERTICAL
        clipChildren = false
        clipToPadding = false
        setBackgroundResource(R.drawable.rating_view_background)
        val padding = context.resources.getDimensionPixelSize(R.dimen.indent)
        setPadding(padding, padding / 2, padding, padding / 2)
        updateUi()
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        startLoadIfPossible()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        job?.cancel()
    }

    fun setRatingsManager(ratingsManager: RatingsManager) {
        this.ratingsManager = ratingsManager
        startLoadIfPossible()
    }

    fun setRatingId(ratingId: String) {
        this.ratingId = ratingId
        startLoadIfPossible()
    }

    private fun startLoadIfPossible() {
        val id = ratingId
        if (id != null && ::ratingsManager.isInitialized) {
            job?.cancel()
            job = GlobalScope.launch {
                val result = try {
                    val result = ratingsManager.loadRating(id)
                    result
                } catch (exception: Exception) {
                    Rating(null, 5.0f)
                }
                runOnUi {
                    rating = result
                }
            }
        }
    }

    private fun updateUi() {
        val score = rating?.score
        if (score != null) {
            text.text = ((score * 10).roundToInt() / 10.0).toString()
        } else {
            text.text = "-"
        }
        icon.imageTintList = ColorStateList.valueOf(textColor)
        text.setTextColor(textColor)
    }
}
