package com.aegis.infrastructure.observability;

import brave.Tracing;
import brave.propagation.ThreadLocalCurrentTraceContext;
import brave.context.slf4j.MDCScopeDecorator;
import io.micrometer.tracing.Tracer;
import io.micrometer.tracing.brave.bridge.BraveCurrentTraceContext;
import io.micrometer.tracing.brave.bridge.BraveTracer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import zipkin2.reporter.AsyncReporter;
import zipkin2.reporter.Reporter;
import zipkin2.reporter.brave.AsyncZipkinSpanHandler;
import zipkin2.reporter.urlconnection.URLConnectionSender;

@Configuration
public class BraveTracingConfig {

    @Value("${management.zipkin.tracing.endpoint:http://localhost:9411/api/v2/spans}")
    private String zipkinEndpoint;

    @Bean
    public URLConnectionSender sender() {
        return URLConnectionSender.create(zipkinEndpoint);
    }

    @Bean
    public AsyncZipkinSpanHandler zipkinSpanHandler(URLConnectionSender sender) {
        return AsyncZipkinSpanHandler.create(sender);
    }

    @Bean
    public Tracing braveTracing(AsyncZipkinSpanHandler zipkinSpanHandler) {
        return Tracing.newBuilder()
                .localServiceName("aegis-engine")
                .currentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        .addScopeDecorator(MDCScopeDecorator.get())
                        .build())
                .addSpanHandler(zipkinSpanHandler)
                .build();
    }

    @Bean
    public brave.Tracer braveNativeTracer(Tracing tracing) {
        return tracing.tracer();
    }

    @Bean
    public Tracer micrometerTracer(brave.Tracer braveNativeTracer) {
        return new BraveTracer(
                braveNativeTracer,
                new BraveCurrentTraceContext(ThreadLocalCurrentTraceContext.newBuilder()
                        .addScopeDecorator(MDCScopeDecorator.get())
                        .build()),
                new io.micrometer.tracing.brave.bridge.BraveBaggageManager()
        );
    }
}
