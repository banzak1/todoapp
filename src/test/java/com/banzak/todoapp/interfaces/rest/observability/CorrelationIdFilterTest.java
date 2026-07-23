package com.banzak.todoapp.interfaces.rest.observability;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CorrelationIdFilter")
class CorrelationIdFilterTest {

    private final CorrelationIdFilter filter = new CorrelationIdFilter();

    @AfterEach
    void clearMdc() {
        MDC.clear();
    }

    @Test
    @DisplayName("should generate and return a correlation ID when header is absent")
    void shouldGenerateAndReturnCorrelationId_whenHeaderIsAbsent() throws Exception {
        var request = new MockHttpServletRequest();
        var response = new MockHttpServletResponse();
        var correlationIdDuringRequest = new AtomicReference<String>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                correlationIdDuringRequest.set(MDC.get(CorrelationIdFilter.MDC_KEY)));

        var responseCorrelationId = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
        assertThat(responseCorrelationId).isEqualTo(correlationIdDuringRequest.get());
        assertThat(UUID.fromString(responseCorrelationId).toString()).isEqualTo(responseCorrelationId);
        assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
    }

    @Test
    @DisplayName("should preserve and return a valid correlation ID when header is present")
    void shouldPreserveAndReturnCorrelationId_whenHeaderIsValid() throws Exception {
        var correlationId = UUID.randomUUID().toString();
        var request = new MockHttpServletRequest();
        request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, correlationId);
        var response = new MockHttpServletResponse();
        var correlationIdDuringRequest = new AtomicReference<String>();

        filter.doFilter(request, response, (servletRequest, servletResponse) ->
                correlationIdDuringRequest.set(MDC.get(CorrelationIdFilter.MDC_KEY)));

        assertThat(correlationIdDuringRequest.get()).isEqualTo(correlationId);
        assertThat(response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER)).isEqualTo(correlationId);
        assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
    }

    @Test
    @DisplayName("should replace invalid correlation IDs")
    void shouldReplaceCorrelationId_whenHeaderIsInvalid() throws Exception {
        for (var invalidCorrelationId : new String[]{"", "not-a-uuid", "a".repeat(129)}) {
            var request = new MockHttpServletRequest();
            request.addHeader(CorrelationIdFilter.CORRELATION_ID_HEADER, invalidCorrelationId);
            var response = new MockHttpServletResponse();

            filter.doFilter(request, response, (servletRequest, servletResponse) -> {
            });

            var responseCorrelationId = response.getHeader(CorrelationIdFilter.CORRELATION_ID_HEADER);
            assertThat(responseCorrelationId).isNotEqualTo(invalidCorrelationId);
            assertThat(UUID.fromString(responseCorrelationId).toString()).isEqualTo(responseCorrelationId);
            assertThat(MDC.get(CorrelationIdFilter.MDC_KEY)).isNull();
        }
    }
}
