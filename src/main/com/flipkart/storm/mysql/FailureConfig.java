/**
 * Copyright 2016 Flipkart Internet Pvt. Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
