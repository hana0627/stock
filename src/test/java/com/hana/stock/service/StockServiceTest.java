package com.hana.stock.service;

import com.hana.stock.domain.Stock;
import com.hana.stock.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {

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
        stockRepository.deleteAll();
    }

    @Test
    @DisplayName("재고감소")
    void decrease() {
        stockService.decrease(1L,1L);

        // 100-1 = 99;
        Stock stock = stockRepository.findById(1L).orElseThrow(EntityNotFoundException::new);

        Assertions.assertThat(stock.getQuantity()).isEqualTo(99L);
    }


}