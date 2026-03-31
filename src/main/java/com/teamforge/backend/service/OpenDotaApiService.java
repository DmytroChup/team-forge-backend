package com.teamforge.backend.service;

import com.teamforge.backend.dto.opendota.OpenDotaWinLossResponse;
import com.teamforge.backend.exception.ExternalApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenDotaApiService {

    private final RestClient restClient;

    private static final String OPENDOTA_API_BASE_URL = "https://api.opendota.com/api";

    /**
     * Fetches the win/loss count for a given player from the OpenDota API.
     * @param accountId The 32-bit Steam account ID.
     * @return An Optional containing the win/loss data, or empty if the request fails.
     */
    public Optional<OpenDotaWinLossResponse> fetchPlayerWinLoss(String accountId) {
        try {
            OpenDotaWinLossResponse response = restClient.get()
                    .uri(OPENDOTA_API_BASE_URL + "/players/{accountId}/wl", accountId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, resp) -> {
                        // Log the error response from the external API
                        log.error("Error fetching data from OpenDota API. Status: {}, Body: {}", resp.getStatusCode(), resp.getStatusText());
                        // Explicitly throw an exception to halt further processing and parsing
                        throw new ExternalApiException("OpenDota API returned an error: " + resp.getStatusCode());
                    })
                    .body(OpenDotaWinLossResponse.class);

            return Optional.ofNullable(response); // response can be null if the body is empty
        } catch (ExternalApiException e) {
            // Rethrow our custom exception so it can be handled globally
            throw e;
        } catch (Exception e) {
            log.error("Exception occurred while calling OpenDota API for accountId: {}", accountId, e);
            return Optional.empty();
        }
    }
}