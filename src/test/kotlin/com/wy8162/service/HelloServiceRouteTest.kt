package com.wy8162.service

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.http.HttpStatusCode
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.junit5.MockKExtension
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.koin.test.inject
import org.koin.test.mock.declareMock

@ExtendWith(MockKExtension::class)
internal class HelloServiceRouteTest : BaseIntegrationTest() {
    private val helloService: HelloService by inject<HelloService>()

    @Before
    fun setUp() = MockKAnnotations.init(this)

    @Test
    fun `should get bad request because unauthorized access`() = runTest {
        val response = client.get("/api/v1/users/hello/101")

        assertThat(response.status).isEqualTo(HttpStatusCode.BadRequest)
    }

    @Test
    fun `should say hi`() = runTest { httpClient ->
        declareMock<HelloService>()

        coEvery { helloService.sayHi(any()) } returns HelloMessage(
            message = "This is a test",
            status = "Good"
        )

        val response = httpClient.get("/api/v1/users/hello/101") {
            header("role", "system")
            header("requester", "admin")
        }

        assertThat(response.status).isEqualTo(HttpStatusCode.OK)
        val message = response.body<HelloResponse>()
        assertThat(message.message.message).isEqualTo("This is a test")
    }

    internal class HelloResponse(
        val message: HelloMessage,
        val hello: String
    )
}
