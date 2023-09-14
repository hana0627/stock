package com.hana.stock.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Getter private Long productId;
    @Getter private Long quantity;

    @Version
    private Long version;


    public static Stock of(Long productId, Long quantity) {
        return new Stock(productId,quantity);
    }

    public Stock(Long productId, Long quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }


    //재고감소 메소드

    public void decrease(Long quantity) {
        if(this.quantity - quantity <0) {
            throw new RuntimeException("재고는 0개 미만이 될 수 없습니다.");
        }

        this.quantity -= quantity;
    }



}
