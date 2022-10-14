package com.wy8162.service

import com.wy8162.model.hr.Employee
import io.mockk.MockKAnnotations
import io.mockk.coVerify
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.junit5.MockKExtension
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
internal class HelloServiceImplTest {
    @MockK
    lateinit var helloMessage: HelloMessage

    @MockK
    lateinit var employee: Employee

    @RelaxedMockK
    lateinit var hrService: AbstractHrService

    @RelaxedMockK
    lateinit var smsService: SmsService

    private val helloService by lazy {
        HelloServiceImpl(hrService, smsService)
    }

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `should say hi`(): Unit = runBlocking {
        val r = helloService.sayHi("Jack")

        assertTrue(r.message.startsWith("Hi"))

        helloService.textMessage("message")

        coVerify { smsService.sendSms(any<String>()) }
    }
}
