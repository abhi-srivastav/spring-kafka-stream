package com.gyanexpert.kafka.customer;

import com.gyanexpert.kafka.customer.domain.Customer;
import com.gyanexpert.kafka.customer.domain.CustomerBalance;
import com.gyanexpert.kafka.customer.domain.CustomerTransaction;

import org.apache.kafka.streams.state.QueryableStoreType;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.binder.kafka.streams.InteractiveQueryService;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {

    @Autowired
    private StreamBridge streamBridge;

    @Autowired
    private InteractiveQueryService queryService;

    @PostMapping(path = "/customer", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public Customer createCustomer(@RequestBody Customer customer){
        System.out.println("Sending " + customer);
		streamBridge.send("customers", MessageBuilder.withPayload(customer).setHeader(KafkaHeaders.MESSAGE_KEY, customer.getCustomerId()).build());
        return customer;
    }

    @PostMapping(path = "/balance", consumes = {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    public CustomerTransaction updateBalance(@RequestBody CustomerTransaction txn){
        System.out.println("Sending " + txn);
		streamBridge.send("customer-transaction", MessageBuilder.withPayload(txn).setHeader(KafkaHeaders.MESSAGE_KEY, txn.getCustomerId()).build());
        return txn;
    }

    @GetMapping("/balance/{customerId}")
    public CustomerBalance getBalance(@PathVariable(required = false) Integer customerId){
        ReadOnlyKeyValueStore<Integer, CustomerBalance> keyValueStore = queryService.getQueryableStore(CustomerApplication.STORE_NAME, QueryableStoreTypes.<Integer, CustomerBalance>keyValueStore());
        CustomerBalance balance = keyValueStore.get(customerId);
        if (balance == null) {
            throw new IllegalArgumentException("...");
        }
        return balance;
    }
}
