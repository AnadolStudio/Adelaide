package com.anadolstudio.adelaide

import com.anadolstudio.core.tasks.Result
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ResultTest {

    @Test
    fun addition_isCorrect() {
        val result: Result<String> = Result.Success("")

        when (result) {
            is Result.Loading -> println("Loading")
            is Result.Success -> println("Success")
            is Result.Error -> println("Error")
        }
    }
}
