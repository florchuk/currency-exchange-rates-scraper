package ua.nazarii.currency.exchange.rates.scraper.utils;

import ua.nazarii.currency.exchange.rates.scraper.dto.RateDto;
import ua.nazarii.currency.exchange.rates.scraper.scrapers.Scraper;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpRequest;
import java.time.Duration;
import java.util.Arrays;
import java.util.Queue;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class ScraperClassReaderTests {
    @Test
    public void getClassesTest() throws Exception {
        // Class names for testing.
        String[] expectedClassNames = new String[]{
                FirstScraper.class.getName(),
                SecondScraper.class.getName()
        };

        // Getting classes.
        Queue<Class<? extends Scraper>> classes = ScraperClassReader.getClasses(expectedClassNames);

        // Comparing.
        assertEquals(
                Arrays.stream(expectedClassNames).sorted(String::compareTo).toList(),
                classes.stream().map(Class::getName).sorted(String::compareTo).toList()
        );
    }

    @Test
    public void getClassesThrowsIllegalArgumentExceptionTest() throws Exception {
        // The class names is empty.
        assertThrows(
                IllegalArgumentException.class,
                () -> ScraperClassReader.getClasses("")
        );

        // One of the class names is empty.
        assertThrows(
                IllegalArgumentException.class,
                () -> ScraperClassReader.getClasses(
                        new String[]{
                                FirstScraper.class.getName(),
                                "",
                                SecondScraper.class.getName()
                        }
                )
        );

        // The core class is not allowed.
        assertThrows(
                IllegalArgumentException.class,
                () -> ScraperClassReader.getClasses(Scraper.class.getName())
        );

        // The abstract classes is not allowed.
        assertThrows(
                IllegalArgumentException.class,
                () -> ScraperClassReader.getClasses(
                        new String[]{
                                FirstScraper.class.getName(),
                                FirstAbstractScraper.class.getName(),
                                SecondAbstractScraper.class.getName()
                        }
                )
        );

        // Classes that not extend the core class is not allowed.
        assertThrows(
                IllegalArgumentException.class,
                () -> ScraperClassReader.getClasses(Object.class.getName())
        );
    }

    @Test
    public void getClassesThrowsClassNotFoundExceptionTest() throws Exception {
        // Not existing classes is not allowed.
        assertThrows(
                Exception.class,
                () -> ScraperClassReader.getClasses("ua.nazarii.currency.exchange.rates.scraper.scrapers.NotExistingScraper")
        );
    }

    // First abstract class for testing.
    private abstract static class FirstAbstractScraper extends Scraper {
        public FirstAbstractScraper() {
            super();
        }
    }

    // Second abstract class for testing.
    private abstract static class SecondAbstractScraper extends Scraper {
        public SecondAbstractScraper() {
            super();
        }
    }

    // First class for testing.
    private static class FirstScraper extends Scraper {
        private final Integer exchangerId = 1;

        public FirstScraper() {
            super();
        }

        @Override
        public Integer getExchangerId() {
            return this.exchangerId;
        }

        @Override
        protected Object[] getItems(Object root) throws Exception {
            return new Object[0];
        }

        @Override
        protected Integer getUnit(Object item) throws Exception {
            return null;
        }

        @Override
        protected String getUnitCurrencyAlphabeticCode(Object item) throws Exception {
            return null;
        }

        @Override
        protected String getRateCurrencyAlphabeticCode(Object item) throws Exception {
            return null;
        }

        @Override
        protected Double getBuyRate(Object item) throws Exception {
            return null;
        }

        @Override
        protected Double getSaleRate(Object item) throws Exception {
            return null;
        }

        @Override
        protected Predicate<RateDto> getPredicate() {
            return null;
        }

        @Override
        protected URI getURI() throws URISyntaxException {
            return null;
        }

        @Override
        protected Duration getTimeout() {
            return null;
        }

        @Override
        protected String getMethod() {
            return null;
        }

        @Override
        protected HttpRequest.BodyPublisher getBodyPublisher() {
            return null;
        }

        @Override
        protected String[] getHeaders() {
            return new String[0];
        }
    }

    // Second class for testing.
    private static class SecondScraper extends Scraper {
        private final Integer exchangerId = 2;

        public SecondScraper() {
            super();
        }

        @Override
        public Integer getExchangerId() {
            return this.exchangerId;
        }

        @Override
        protected Object[] getItems(Object root) throws Exception {
            return new Object[0];
        }

        @Override
        protected Integer getUnit(Object item) throws Exception {
            return null;
        }

        @Override
        protected String getUnitCurrencyAlphabeticCode(Object item) throws Exception {
            return null;
        }

        @Override
        protected String getRateCurrencyAlphabeticCode(Object item) throws Exception {
            return null;
        }

        @Override
        protected Double getBuyRate(Object item) throws Exception {
            return null;
        }

        @Override
        protected Double getSaleRate(Object item) throws Exception {
            return null;
        }

        @Override
        protected Predicate<RateDto> getPredicate() {
            return null;
        }

        @Override
        protected URI getURI() throws URISyntaxException {
            return null;
        }

        @Override
        protected Duration getTimeout() {
            return null;
        }

        @Override
        protected String getMethod() {
            return null;
        }

        @Override
        protected HttpRequest.BodyPublisher getBodyPublisher() {
            return null;
        }

        @Override
        protected String[] getHeaders() {
            return new String[0];
        }
    }
}