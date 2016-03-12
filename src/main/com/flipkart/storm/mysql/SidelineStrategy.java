package com.flipkart.storm.mysql;

import java.io.Serializable;

public interface SidelineStrategy extends Serializable {
    public void sideline(TransactionEvent txEvent);
}
