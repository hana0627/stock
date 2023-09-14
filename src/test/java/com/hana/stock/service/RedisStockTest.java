package com.hana.stock.service;

import com.hana.stock.domain.Stock;
import com.hana.stock.facade.LettuceLockStockFacade;
import com.hana.stock.facade.RedissonLockStockFacade;
import com.hana.stock.repository.StockRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
public class RedisStockTest {

    @Autowired
    private LettuceLockStockFacade lettuceLockStockFacade;
    @Autowired
    private RedissonLockStockFacade redissonLockStockFacade;
    @Autowired
    private StockService stockService;

    @Autowired
    private StockRepository stockRepository;
    @BeforeEach
    void before() {
        Stock stock = Stock.of(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void after(){
//        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("lettuce을 이용한 100개의 요청")
    public void lettuce() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    lettuceLockStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 100 - (100 * 1) = 0
        assertThat(stock.getQuantity()).isEqualTo(0L);
    }

    @Test
    @DisplayName("redisson을 이용한 100개의 요청")
    public void redisson() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    redissonLockStockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow();

        // 100 - (100 * 1) = 0
        assertThat(stock.getQuantity()).isEqualTo(0L);
    }

}
