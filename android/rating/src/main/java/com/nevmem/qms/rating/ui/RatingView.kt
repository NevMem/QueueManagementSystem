package com.nevmem.qms.rating.ui

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import com.nevmem.qms.rating.R
import com.nevmem.qms.rating.Rating
import com.nevmem.qms.rating.RatingsManager
import kotlin.math.roundToInt

class RatingView : LinearLayoutCompat {
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)

    private lateinit var ratingsManager: RatingsManager

    private var rating: Rating? = null
        set(value) {
            if (field == value)
                return
            field = value
            updateUi()
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

    fun setRatingsManager(ratingsManager: RatingsManager) {
        this.ratingsManager = ratingsManager
    }

    fun setRatingId(ratingId: String) {
        rating = Rating(4.9, 5.0)
    }

    private fun updateUi() {
        val score = rating?.score
        if (score != null) {
            text.text = ((score * 10).roundToInt() / 10.0).toString()
        } else {
            text.text = "-"
        }
    }
}
