package com.wy8162.service

import com.wy8162.model.hr.Employee
import com.wy8162.plugins.koinModule
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkClass
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.extension.RegisterExtension
import org.koin.test.KoinTest
import org.koin.test.inject
import org.koin.test.junit5.KoinTestExtension
import org.koin.test.junit5.mock.MockProviderExtension
import org.koin.test.mock.declareMock

@ExtendWith(MockKExtension::class)
internal class HelloServiceImplKoinTest : KoinTest {
    @MockK
    lateinit var helloMessage: HelloMessage

    @MockK
    lateinit var employee: Employee

    private val hrService: AbstractHrService by inject<AbstractHrService>()
    private val smsService: SmsService by inject<SmsService>()

    private val helloService by lazy {
        HelloServiceImpl(hrService, smsService)
    }

    @JvmField
    @RegisterExtension
    val koinTestExtension = KoinTestExtension.create {
        modules(koinModule)
    }

    @JvmField
    @RegisterExtension
    val mockProvider = MockProviderExtension.create { clazz ->
        mockkClass(clazz)
    }

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `should say hi`(): Unit = runBlocking {
        declareMock<SmsService>()

        coEvery { smsService.sendSms(any()) } returns HelloMessage(
            message = "Hi, someone",
            status = "Good"
        )

        val r = helloService.textMessage("1234")

        Assertions.assertTrue(r.message.startsWith("Hi"))
    }
}
