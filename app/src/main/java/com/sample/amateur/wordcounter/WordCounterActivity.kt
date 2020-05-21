package com.sample.amateur.wordcounter

import android.os.Bundle
import android.os.CountDownTimer
import android.preference.PreferenceManager
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.activity_main.*


class WordCounterActivity : AppCompatActivity() {

    lateinit var viewModel: CounterViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(this).get(CounterViewModel::class.java)

        fetchLastSavedText()
        observeViewModel()
        setupEditTextListeners()
        btnUndo.setOnClickListener {
            viewModel.remove()
        }
    }

    private fun fetchLastSavedText() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val savedText = sharedPreferences.getString(KEY_SAVED_TEXT, "")
        viewModel.addStringToStack(savedText)
    }

    private fun observeViewModel() {
        viewModel.currentText.observe(this, Observer {
            editText.setText(it)
            editText.setSelection(editText.text.length)
        })

        viewModel.wordsCount.observe(this, Observer {
            tvWordCount.text = it
        })

        viewModel.isStackEmpty.observe(this, Observer { flag ->
            btnUndo.isEnabled = flag
        })
    }

    private fun setupEditTextListeners() {
        editText.addTextChangedListener(object : TextWatcher {
            var timer: CountDownTimer? = null

            override fun afterTextChanged(s: Editable?) {}

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (timer != null) {
                    timer?.cancel()
                }
                timer = object : CountDownTimer(1500, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}
                    override fun onFinish() {
                        viewModel.addStringToStack(editText.text.toString())
                    }
                }.start()
            }
        })
    }

    override fun onStop() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        sharedPreferences.edit().putString(KEY_SAVED_TEXT, editText.text.toString()).apply()
        super.onStop()
    }

    companion object {
        const val KEY_SAVED_TEXT: String = "KEY_SAVED_TEXT"
    }
}
