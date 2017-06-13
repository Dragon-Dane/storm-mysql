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

import org.apache.storm.task.TopologyContext;

import java.io.Serializable;
import java.util.Map;

/**
 * The interface for hooking up sideline strategies.
 */
public interface SidelineStrategy extends Serializable {

    /**
     * The sideline function. Once this function returns, the event will be acked.
     * There is possibility of data loss if events are not sidelined properly before
     * the function returns.
     *
     * @param txEvent the transaction event
     */
    void sideline(TransactionEvent txEvent);

    /**
     * This function should be used to initialize all member variables.
     * Do not use the constructor.
     *
     * @param conf the configuration
     * @param context the topology context
     */
    void initialize(Map conf, TopologyContext context);
}
