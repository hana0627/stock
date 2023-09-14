package com.hana.stock.facade;

import com.hana.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OptimisticStockFacade {

    private final StockService stockService;



    public void decrease(Long id, Long quantity) throws InterruptedException {

        while(true) {
            try{
                stockService.decreaseWithOptimisticLock(id, quantity);

                break;
            } catch (Exception e) {
                Thread.sleep(10);
            }
        }

    }


}
