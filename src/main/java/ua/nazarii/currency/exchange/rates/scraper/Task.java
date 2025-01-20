package ua.nazarii.currency.exchange.rates.scraper;

import org.apache.logging.log4j.Logger;
import ua.nazarii.currency.exchange.rates.scraper.dto.RateDto;
import ua.nazarii.currency.exchange.rates.scraper.scrapers.Scraper;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.locks.Lock;

public class Task implements Runnable {
    private final Queue<Class<? extends Scraper>> scraperClasses;

    private final List<RateDto> rateDtos;

    private final Lock lock;

    private final Logger logger;

    /**
     * Constructor.
     * @param scraperClasses Queue of the scraper classes.
     * @param rateDtos List of the Dtos.
     * @param lock Instance of the thread lock.
     * @param logger Instance of the logger.
     */
    public Task(
            Queue<Class<? extends Scraper>> scraperClasses,
            List<RateDto> rateDtos,
            Lock lock,
            Logger logger
    ) {
        this.scraperClasses = scraperClasses;
        this.rateDtos = rateDtos;
        this.lock = lock;
        this.logger = logger;
    }

    @Override
    public void run() {
        int successCounter = 0;
        int failureCounter = 0;

        this.logger.info("Thread successfully started.");

        // Running until all the scraper classes is over (will not be processed).
        while (true) {
            Class<? extends Scraper> scraperClass;

            try {
                // Locking queue, before fetching scraper class from the queue.
                this.lock.lock();

                // Fetching scraper class from the queue.
                scraperClass = this.scraperClasses.remove();
            } catch (NoSuchElementException e) {
                this.logger.info("There is no items in the queue left.");

                // Exiting out of the task.
                break;
            } finally {
                // Unlocking queue, after fetching scraper class from the queue.
                this.lock.unlock();
            }

            try {
                Scraper scraper = scraperClass.getConstructor().newInstance();

                this.logger.info(
                        String.format(
                                "The instance of \"%s\" is successfully created.",
                                scraperClass.getName()
                        )
                );

                // Fetching.
                List<RateDto> rateDtos = Arrays.asList(scraper.getRateDtos());

                this.logger.info(
                        String.format(
                                "%d Dtos is successfully fetched by \"%s\".",
                                rateDtos.size(),
                                scraperClass.getName()
                        )
                );

                this.lock.lock();

                try {
                    this.rateDtos.addAll(rateDtos);
                } finally {
                    this.lock.unlock();
                }

                this.logger.info(
                        String.format(
                                "%d Dtos (fetched by \"%s\") is successfully proceeded.",
                                rateDtos.size(),
                                scraperClass.getName()
                        )
                );

                ++successCounter;
            } catch (Exception e) {
                this.logger.error(e.getMessage(), e);

                ++failureCounter;
            }
        }

        this.logger.info(
                String.format(
                        "Thread successfully ended. Successfully proceeded %d operations, failed %d.",
                        successCounter,
                        failureCounter
                )
        );
    }
}