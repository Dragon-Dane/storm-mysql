package com.flipkart.storm.mysql;

public enum CentralTxEventBuilder {
    INSTANCE;

    private final TransactionEvent.builder txBuilder = new TransactionEvent.builder();

    public TransactionEvent.builder getBuilder(){
        return txBuilder;
    }
}
