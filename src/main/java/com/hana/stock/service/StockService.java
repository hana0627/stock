package com.hana.stock.service;

import com.hana.stock.domain.Stock;
import com.hana.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    @Transactional
    public void decrease(Long id, Long quantity) {
        //Stock조회
        //재고를 감소
        //갱신된 값을 저장
        Stock stock = stockRepository.findById(id).orElseThrow(EntityNotFoundException::new);

        stock.decrease(quantity);
    }


    @Transactional
    public void decreaseWithPessimisticLock(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithPessimisticLock(id);
        stock.decrease(quantity);
    }


    @Transactional
    public void decreaseWithOptimisticLock(Long id, Long quantity) {
        Stock stock = stockRepository.findByIdWithOptimisticLock(id);
        stock.decrease(quantity);
    }


    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void decreaseWithNamedLock(Long id, Long quantity) {
        Stock stock = stockRepository.findById(id).orElseThrow();

        stock.decrease(quantity);

        stockRepository.saveAndFlush(stock);
    }

}
