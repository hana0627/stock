package com.hana.stock.service;

import com.hana.stock.domain.Stock;
import com.hana.stock.facade.NamedLockStockFacade;
import com.hana.stock.facade.OptimisticStockFacade;
import com.hana.stock.repository.StockRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityNotFoundException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class StockServiceTest {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private OptimisticStockFacade optimisticStockFacade;
    @Autowired
    private NamedLockStockFacade namedLockStockFacade;

    @BeforeEach
    void before() {
        Stock stock = Stock.of(1L, 100L);
        stockRepository.saveAndFlush(stock);
    }

    @AfterEach
    void after(){
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("재고감소")
    void decrease() {
        stockService.decrease(1L,1L);

        // 100-1 = 99;
        Stock stock = stockRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        assertThat(stock.getQuantity()).isEqualTo(99L);
    }



    @Test
    @DisplayName("동시에 100개의 요청")
    void multiThread() throws InterruptedException {
        int threadCount = 100;

        // 32개의 고정된 쓰레드풀을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행중인 작업이 완료될 때 까지 대기할 수 있도록 만들어주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        assertThat(stock.getQuantity()).isNotEqualTo(0L);
    }


    @Test
    @DisplayName("PessimisticLock을 이용한 100개의 요청")
    void multiThread1() throws InterruptedException {
        int threadCount = 100;

        // 32개의 고정된 쓰레드풀을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행중인 작업이 완료될 때 까지 대기할 수 있도록 만들어주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    stockService.decreaseWithPessimisticLock(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        assertThat(stock.getQuantity()).isEqualTo(0L);
        // 820ms
    }



    @Test
    @DisplayName("OptimisticLock을 이용한 100개의 요청")
    void multiThread2() throws InterruptedException {
        int threadCount = 100;

        // 32개의 고정된 쓰레드풀을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행중인 작업이 완료될 때 까지 대기할 수 있도록 만들어주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    optimisticStockFacade.decrease(1L, 1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        assertThat(stock.getQuantity()).isEqualTo(0L);
        // 1s 684ms
    }

    @Test
    @DisplayName("NamedLock을 이용한 100개의 요청")
    void multiThread3() throws InterruptedException {
        int threadCount = 100;

        // 32개의 고정된 쓰레드풀을 생성
        ExecutorService executorService = Executors.newFixedThreadPool(32);

        // CountDownLatch : 다른 스레드에서 수행중인 작업이 완료될 때 까지 대기할 수 있도록 만들어주는 클래스
        CountDownLatch latch = new CountDownLatch(threadCount);


        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    namedLockStockFacade.decrease(1L, 1L);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();

        Stock stock = stockRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        assertThat(stock.getQuantity()).isEqualTo(0L);
        // 1s 684ms
    }

}
