package com.decodetalkers.personalinterestsmanager.ui.customview

import android.app.Activity
import android.app.Dialog
import android.os.Bundle
import android.view.View
import com.decodetalkers.personalinterestsmanager.R
import kotlinx.android.synthetic.main.rating_dialogue.*

class RatingDialogue(activity: Activity, var onRate: (rating: Float) -> Unit) : Dialog(activity), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        setContentView(R.layout.rating_dialogue)
        rating_btnCancel.setOnClickListener(this)
        rating_btnSubmit.setOnClickListener(this)
        ratingBar.rating = 1F
    }

    override fun onClick(p0: View) {
        when(p0.id){
            R.id.rating_btnCancel ->{
                dismiss()
            }
            R.id.rating_btnSubmit ->{
                onRate(ratingBar.rating)
                dismiss()
            }
        }
    }
}