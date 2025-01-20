package ua.nazarii.currency.exchange.rates.scraper.scrapers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ua.nazarii.currency.exchange.rates.scraper.dto.RateDto;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class PrivatBankAtBranchesScraper extends Scraper {
    // The unique scraper (exchanger) identifier.
    private final Integer exchangerId = 2;

    /**
     * Constructor.
     */
    public PrivatBankAtBranchesScraper() {
        super();
    }

    @Override
    public Integer getExchangerId() {
        return this.exchangerId;
    }

    @Override
    protected Object getRoot() throws Exception {
        Object root = super.getRoot();

        if (root instanceof String) {
            return (new ObjectMapper()).readValue(((String) root), new TypeReference<List<JsonNode>>() {});
        }

        throw new IllegalArgumentException(
                String.format(
                        "Invalid root response Object (\"%s\").",
                        this.getClass().getName()
                )
        );
    }

    @Override
    protected Object[] getItems(Object root) throws Exception {
        if (root instanceof List<?> list && !list.isEmpty() && list.getFirst() instanceof JsonNode) {
            return list.toArray();
        }

        throw new IllegalArgumentException(
                String.format(
                        "Invalid root response Object (\"%s\").",
                        this.getClass().getName()
                )
        );
    }

    @Override
    protected Integer getUnit(Object item) throws Exception {
        return 1;
    }

    @Override
    protected String getUnitCurrencyAlphabeticCode(Object item) throws Exception {
        return ((JsonNode) item).get("ccy").asText();
    }

    @Override
    protected String getRateCurrencyAlphabeticCode(Object item) throws Exception {
        return ((JsonNode) item).get("base_ccy").asText();
    }

    @Override
    protected Double getBuyRate(Object item) throws Exception {
        return ((JsonNode) item).get("buy").asDouble();
    }

    @Override
    protected Double getSaleRate(Object item) throws Exception {
        return ((JsonNode) item).get("sale").asDouble();
    }

    @Override
    protected Predicate<RateDto> getPredicate() {
        return new Predicate<>() {
            private final List<String> allowedCurrencyCodes = Arrays.stream(new String[]{"USD", "EUR", "UAH"}).toList();

            @Override
            public boolean test(RateDto rateDto) {
                return this.allowedCurrencyCodes.contains(rateDto.getUnitCurrencyAlphabeticCode())
                        && this.allowedCurrencyCodes.contains(rateDto.getRateCurrencyAlphabeticCode())
                        && !rateDto.getBuyRate().equals(0.0)
                        && !rateDto.getSaleRate().equals(0.0);
            }
        };
    }

    @Override
    protected URI getURI() throws URISyntaxException {
        return new URI("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5");
    }

    @Override
    protected Duration getTimeout() {
        return Duration.ofSeconds(30);
    }

    @Override
    protected String getMethod() {
        return "GET";
    }

    @Override
    protected HttpRequest.BodyPublisher getBodyPublisher() {
        return HttpRequest.BodyPublishers.noBody();
    }

    @Override
    protected String[] getHeaders() {
        return new String[0];
    }
}