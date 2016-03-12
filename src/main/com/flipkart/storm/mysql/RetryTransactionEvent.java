package com.flipkart.storm.mysql;

public class RetryTransactionEvent {

    private final TransactionEvent  txEvent;
    private final int               numRetries;

    public RetryTransactionEvent(TransactionEvent txEvent, int numRetries) {
        this.txEvent    = txEvent;
        this.numRetries = numRetries;
    }

    public TransactionEvent getTxEvent() {
        return txEvent;
    }

    public int getNumRetries() {
        return numRetries;
    }
}
