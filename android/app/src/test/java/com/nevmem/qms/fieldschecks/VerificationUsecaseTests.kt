package com.nevmem.qms.fieldschecks

import com.nevmem.qms.fragments.registration.checks.Check
import com.nevmem.qms.fragments.registration.checks.SimplePasswordCheck
import com.nevmem.qms.fragments.registration.checks.TooShortCheck
import com.nevmem.qms.fragments.registration.checks.VerificationUsecase
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class VerificationUsecaseTests {
    private val mainThreadSurrogate = newSingleThreadContext("UI thread")

    @Before
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        mainThreadSurrogate.close()
    }

    @Test
    fun testLengthPasswordCheck() = runBlocking {
        val checker = TooShortCheck()
        check(checker.check(null) is Check.Result.Error)
        check(checker.check("") is Check.Result.Error)
        check(checker.check("abcdef") == Check.Result.Ok)
    }

    @Test
    fun testSimplePasswordCheck() = runBlocking {
        val checker = SimplePasswordCheck()
        check(checker.check("") is Check.Result.Error)
        check(checker.check("a") is Check.Result.Error)
        check(checker.check("9") is Check.Result.Error)
        check(checker.check("a9") == Check.Result.Ok)
        check(checker.check("aabcdef0") == Check.Result.Ok)
    }
}
