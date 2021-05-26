package com.gyanexpert.kafka.customer;

import java.util.function.Function;

import com.gyanexpert.kafka.customer.domain.CustomerBalance;
import com.gyanexpert.kafka.customer.domain.CustomerTransaction;

import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.kstream.Grouped;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.support.serializer.JsonSerde;
import org.springframework.stereotype.Component;

@Component
public class CustomerBalanceAggregator{
    private final KeyValueBytesStoreSupplier storeSupplier;

    private final BalanceUpdateEventHandler balanceUpdateEventUpdater = new BalanceUpdateEventHandler();

    private final  Serde<CustomerTransaction> transactionSerde;

    private final Serde<CustomerBalance> balanceSerde;

    private final  Serde<Integer> keySerde;

    public CustomerBalanceAggregator(KeyValueBytesStoreSupplier storeSupplier){
        this.storeSupplier = storeSupplier;
        this.keySerde = Serdes.Integer();
        this.transactionSerde = new JsonSerde<>(CustomerTransaction.class);
        this.balanceSerde = new JsonSerde<>(CustomerBalance.class);
    }

    @Bean
    public Function<KStream<Integer, CustomerTransaction>, KStream<Integer, CustomerBalance>> process() {
        System.out.println("*********************Inside Process==================================>");
        return input -> input
                    .groupByKey(Grouped.with(keySerde, transactionSerde))
                    .aggregate(CustomerBalance::new,
                            (key, updateEvent, summaryEvent) -> balanceUpdateEventUpdater.apply(updateEvent, summaryEvent),
                              Materialized.<Integer, CustomerBalance>as(storeSupplier)
                                    .withKeySerde(keySerde)
                                    .withValueSerde(balanceSerde))
                    .toStream().peek((k, v) -> System.out.println("Debug "+k+"==Balance:"+v));
        }
}