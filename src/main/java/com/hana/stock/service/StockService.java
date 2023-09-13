package com.hana.stock.service;

import com.hana.stock.domain.Stock;
import com.hana.stock.repository.StockRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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


}
