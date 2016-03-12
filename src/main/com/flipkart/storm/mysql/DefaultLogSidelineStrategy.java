package com.flipkart.storm.mysql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultLogSidelineStrategy implements SidelineStrategy {

    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultLogSidelineStrategy.class);

    public void sideline(TransactionEvent transactionEvent) {
        LOGGER.error("Sideline : {}" , transactionEvent.toString());
    }
}
