package com.wy8162.opentelemetry

import io.ktor.server.application.ApplicationCallPipeline
import io.ktor.server.application.BaseApplicationPlugin
import io.ktor.server.response.ApplicationSendPipeline
import io.ktor.util.AttributeKey
import io.ktor.util.pipeline.PipelinePhase
import io.opentelemetry.context.ContextKey

/**
 * Open Telemetry instrumentation - see https://github.com/open-telemetry/opentelemetry-java-instrumentation/tree/main/instrumentation/ktor/ktor-2.0/library
 */
class OtelMetrics(config: Configuration) {
    private val contextKey: ContextKey<String> = ContextKey.named(config.contextKey)

    class Configuration {
        var contextKey = "OtelMetrics"
    }

    companion object OtelFeature : BaseApplicationPlugin<ApplicationCallPipeline, Configuration, OtelMetrics> {
        override val key = AttributeKey<OtelMetrics>("OtelMetrics")

        override fun install(
            pipeline: ApplicationCallPipeline,
            configure: Configuration.() -> Unit
        ): OtelMetrics {
            val configuration = Configuration().apply(configure)
            val otelMetrics = OtelMetrics(configuration)

            val prePhase = PipelinePhase("PreOtelMetrics")
            val postPhase = PipelinePhase("PostOtelMetrics")
            pipeline.insertPhaseBefore(ApplicationCallPipeline.Monitoring, prePhase)
            pipeline.intercept(prePhase) {
                proceed()
            }
            pipeline.sendPipeline.insertPhaseAfter(ApplicationSendPipeline.After, postPhase)
            pipeline.sendPipeline.intercept(postPhase) {
                proceed()
            }

            return otelMetrics
        }
    }
}
