package com.gyanexpert.kafka.customer;

import java.util.function.BiFunction;

import com.gyanexpert.kafka.customer.domain.CustomerBalance;
import com.gyanexpert.kafka.customer.domain.CustomerTransaction;

public class BalanceUpdateEventHandler implements BiFunction<CustomerTransaction, CustomerBalance, CustomerBalance>{

    @Override
    public CustomerBalance apply(CustomerTransaction txn, CustomerBalance balance) {
        System.out.println("Updating Balance for "+txn.getCustomerId()+"==with Amount:"+txn.getAmount());

        balance.setCustomerId(txn.getCustomerId());
        balance.setBalance(balance.getBalance()+txn.getAmount());
        
        System.out.println("Updated Balance for "+balance.getCustomerId()+"==with Amount:"+balance.getBalance());
        return balance;
    }
    
}
