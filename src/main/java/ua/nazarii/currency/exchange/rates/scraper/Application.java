package ua.nazarii.currency.exchange.rates.scraper;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.Logger;
import ua.nazarii.currency.exchange.rates.scraper.dto.RateDto;
import ua.nazarii.currency.exchange.rates.scraper.scrapers.Scraper;
import ua.nazarii.currency.exchange.rates.scraper.utils.Config;
import ua.nazarii.currency.exchange.rates.scraper.utils.ScraperClassReader;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReentrantLock;

public class Application {
    private Application() {
        //
    }

    /**
     * Running the application.
     * @param logger Instance of the logger.
     * @throws Exception If fatal error occurs.
     */
    public static void run(Logger logger) throws Exception {
        List<RateDto> rateDtos = new ArrayList<>();

        // Reading the scraper classes.
        Queue<Class<? extends Scraper>> scraperClasses =
                ScraperClassReader
                        .getClasses(Config.getProperty("scraper.class-names", "").split(","));

        // Calculating the number of threads.
        int numberOfThreads = Math.max(1, Math.min(Runtime.getRuntime().availableProcessors(), scraperClasses.size()));

        // Creating the worker.
        Worker worker = new Worker(numberOfThreads);

        // Run the worker.
        worker.run(
                // Creating the task for the worker.
                new Task(scraperClasses, rateDtos, new ReentrantLock(), logger),
                logger
        );

        try (
                HttpClient httpClient = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build()
        ) {
            HttpRequest.Builder httpRequestBuilder = HttpRequest.newBuilder();

            httpRequestBuilder
                    .uri(new URI(Config.getProperty("scraper.api.url")))
                    .timeout(Duration.ofSeconds(30))
                    .method(
                            "PUT",
                            HttpRequest.BodyPublishers.ofString(
                                    (new ObjectMapper())
                                            .findAndRegisterModules()
                                            .writeValueAsString(rateDtos)
                            )
                    )
                    .headers(
                            "Content-Type",
                            "application/json",
                            "Accept",
                            "application/json",
                            "Authorization",
                            "Basic " + Base64.getEncoder().encodeToString(
                                    String.format(
                                            "%s:%s",
                                            Config.getProperty("scraper.api.username"),
                                            Config.getProperty("scraper.api.password")
                                    ).getBytes()
                            )
                    );

            HttpRequest httpRequest = httpRequestBuilder.build();

            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

            if (httpResponse.statusCode() != HttpURLConnection.HTTP_OK) {
                throw new Exception(
                        String.format(
                                "Response status code is %d (\"%s\"). " + httpResponse.body(),
                                httpResponse.statusCode(),
                                httpResponse.uri().toString()
                        )
                );
            }

            logger.info(
                    String.format(
                            "%d Dtos is successfully proceeded (\"%s\"). " + httpResponse.body(),
                            rateDtos.size(),
                            httpResponse.uri()
                    )
            );
        }
    }
}