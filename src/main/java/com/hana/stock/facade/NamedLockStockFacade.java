package com.hana.stock.facade;

import com.hana.stock.repository.LockRepository;
import com.hana.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class NamedLockStockFacade {

    private final LockRepository lockRepository;

    private final StockService stockService;


    @Transactional
    public void decrease(Long id, Long quantity) {
        try {
            lockRepository.getLock(id.toString());
            stockService.decreaseWithNamedLock(id, quantity);
        } finally {
            lockRepository.releaseLock(id.toString());
        }
    }
}