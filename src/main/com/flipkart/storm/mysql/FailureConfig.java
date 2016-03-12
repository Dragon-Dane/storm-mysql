package com.flipkart.storm.mysql;

import java.io.Serializable;

public class FailureConfig implements Serializable {

    private final int               numMaxRetries;
    private final long              numMaxTotalFailAllowed;
    private final SidelineStrategy  sidelineStrategy;

    public FailureConfig(int numMaxRetries, long numMaxTotalFailAllowed) {
        this (numMaxRetries, numMaxTotalFailAllowed, new DefaultLogSidelineStrategy());
    }

    public FailureConfig(int numMaxRetries, long numMaxTotalFailAllowed, SidelineStrategy sidelineStrategy) {
        this.numMaxRetries              = numMaxRetries;
        this.numMaxTotalFailAllowed     = numMaxTotalFailAllowed;
        this.sidelineStrategy           = sidelineStrategy;
    }

    public int getNumMaxRetries() {
        return numMaxRetries;
    }

    public long getNumMaxTotalFailAllowed() {
        return numMaxTotalFailAllowed;
    }

    public SidelineStrategy getSidelineStrategy() {
        return sidelineStrategy;
    }
}
