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

import backtype.storm.task.TopologyContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

/**
 * The default log sideline strategy.
 */
public class DefaultLogSidelineStrategy implements SidelineStrategy {

    /** The Logger. */
    public static final Logger LOGGER = LoggerFactory.getLogger(DefaultLogSidelineStrategy.class);

    /**
     * Log the message as an error.
     *
     * @param transactionEvent the transaction event
     */
    public void sideline(TransactionEvent transactionEvent) {
        LOGGER.error("Sideline : {}", transactionEvent.toString());
    }

    /**
     * Initialize members.
     *
     * @param conf the configuration
     * @param context the topology context
     */
    public void initialize(Map conf, TopologyContext context) {
        //No initialization required here.....
    }
}
