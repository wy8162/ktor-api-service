package com.wy8162.service

import com.wy8162.model.hr.Employee

data class HelloMessage(
    val message: String,
    val status: String,
    val employee: Employee? = null
)

interface HelloService {
    suspend fun sayHi(name: String): HelloMessage
    suspend fun textMessage(name: String): HelloMessage
}

interface SmsService {
    suspend fun sendSms(message: String): HelloMessage
}

class SmsServiceImpl : SmsService {
    override suspend fun sendSms(message: String): HelloMessage {
        println("SMS: $message")
        return HelloMessage(message = "$message was sent.", status = "All Good")
    }

}

class HelloServiceImpl(
    private val hrService: HrService,
    private val smsService: SmsService
) : HelloService {
    override suspend fun sayHi(name: String): HelloMessage {
        if (name == "Jack") return HelloMessage("Hi, $name", "Good")

        if (name.toCharArray().none { it.isDigit() }) {
            return HelloMessage("Hey, $name", "Good")
        }

        val emp = hrService.getEmployee(name.toInt())
        return emp.fold(
            { HelloMessage("Bad, employee not found", "Not Found") },
            { HelloMessage(message = "Hello, ${it.firstName}", status = "Good", employee = it) }
        )
    }

    override suspend fun textMessage(message: String): HelloMessage {
        return smsService.sendSms(message)
    }
}
