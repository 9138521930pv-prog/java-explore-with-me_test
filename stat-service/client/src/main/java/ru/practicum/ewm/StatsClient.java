package ru.practicum.ewm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;

import jakarta.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;


@Service
public class StatsClient extends BaseClient {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(Constants.DATE_FORMAT);

    @Value("${server.application.name:ewm-main-service}")
    private String applicationName;

    public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                        .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                        .build()
        );
    }

    public ResponseEntity<Object> saveHit(HttpServletRequest request) {
        final EndpointRequest hit = EndpointRequest.builder()
                .app(applicationName)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(Timestamp.from(Instant.now()).toLocalDateTime())
                .build();
        return post(hit);
    }

    public ResponseEntity<Object> getHit(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        StringBuilder uriBuilder = new StringBuilder("/stats?start={start}&end={end}");
        HashMap<String, Object> parameters = new HashMap<>();
        parameters.put("start", start.format(formatter));
        parameters.put("end", end.format(formatter));

        if (uris != null && !uris.isEmpty()) {
            parameters.put("uris", String.join(",", uris));
        }
        if (unique) {
            parameters.put("unique", true);
        }
        return get(uriBuilder.toString(), parameters);
    }
}