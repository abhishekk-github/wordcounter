package com.sample.amateur.wordcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import java.text.ChoiceFormat
import java.util.*

class CounterViewModel : ViewModel() {

    var textStack = MutableLiveData<Deque<String>>()

    var currentText: LiveData<String> =
        Transformations.map(textStack) {
            it.peek()
        }

    var wordsCount: LiveData<String> =
        Transformations.map(textStack) {
            wordsCount(it.peek() ?: "")
        }

    var isStackEmpty: LiveData<Boolean> =
        Transformations.map(textStack) {
            it.isNotEmpty()
        }

    private fun wordsCount(text: String): String {
        var count = 0
        if (text.isNotEmpty()) {
            count = Regex("""(\s+|(\r\n|\r|\n))""").findAll(text.trim()).count() + 1
        }
        return ChoiceFormat(
            doubleArrayOf(0.0, 1.0, ChoiceFormat.nextDouble(1.0)),
            arrayOf("0 words, 0 characters", "$count word, ${text.length} characters", "$count words , ${text.length} characters")
        ).format(count)
    }

    fun addStringToStack(data: String) {
        if(textStack.value?.isNotEmpty() != false && data == textStack.value?.peek()) return
        textStack.value?.push(data)
        textStack.value = textStack.value
    }

    fun remove() {
        if (textStack.value.isNullOrEmpty()) return
        textStack.value?.pop()
        textStack.postValue(textStack.value)
    }

    init {
        textStack.value = ArrayDeque<String>()
    }

    override fun onCleared() {
        super.onCleared()
    }
}