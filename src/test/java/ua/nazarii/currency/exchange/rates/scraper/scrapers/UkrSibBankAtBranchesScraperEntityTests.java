package ua.nazarii.currency.exchange.rates.scraper.scrapers;

import ua.nazarii.currency.exchange.rates.scraper.dto.RateDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class UkrSibBankAtBranchesScraperEntityTests {
    private static String response;

    @Spy
    private final Scraper scraper = new UkrSibBankAtBranchesScraper();

    // Expected data. Sorted by unit currency alphabetic code.
    private final List<RateDto> expectedRateDtos = Arrays.stream(
            new RateDto[]{
                    new RateDto(
                            this.scraper.getExchangerId(),
                            1,
                            "USD",
                            "UAH",
                            1.01,
                            2.02
                    ),
                    new RateDto(
                            this.scraper.getExchangerId(),
                            1,
                            "EUR",
                            "UAH",
                            3.03,
                            4.04
                    )
            }).sorted(Comparator.comparing(RateDto::getUnitCurrencyAlphabeticCode)).toList();

    @BeforeAll
    public static void initAll() throws IOException, NullPointerException {
        try (
                InputStream inputStream =
                        UkrSibBankAtBranchesScraperEntityTests.class.getClassLoader()
                                .getResourceAsStream("scrapers/response-ukrsibbank-at-branches.html")
        ) {
            UkrSibBankAtBranchesScraperEntityTests.response = new String(Objects.requireNonNull(inputStream).readAllBytes());
        }
    }

    @Test
    public void getRateDtosTest() throws Exception {
        // Replacing response for the testing.
        Mockito.doReturn(UkrSibBankAtBranchesScraperEntityTests.response).when(this.scraper).getResponse();

        // Getting data from testing response.
        RateDto[] rateDtos = this.scraper.getRateDtos();

        // Size of expected and tested response data should be equal.
        assertEquals(this.expectedRateDtos.size(), rateDtos.length);

        // Sorting by unit currency alphabetic code.
        List<RateDto> actualRateDtos =
                Arrays.stream(rateDtos)
                        .sorted(Comparator.comparing(RateDto::getUnitCurrencyAlphabeticCode))
                        .toList();

        // Synchronizing when was created and when was updated the Dtos (for expected and tested response data).
        this.expectedRateDtos.get(0).setCreatedAt(actualRateDtos.get(0).getCreatedAt());
        this.expectedRateDtos.get(0).setUpdatedAt(actualRateDtos.get(0).getUpdatedAt());

        this.expectedRateDtos.get(1).setCreatedAt(actualRateDtos.get(1).getCreatedAt());
        this.expectedRateDtos.get(1).setUpdatedAt(actualRateDtos.get(1).getUpdatedAt());

        // Expected and tested response data should be equal.
        assertEquals(this.expectedRateDtos, actualRateDtos);
    }
}