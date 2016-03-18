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

/**
 * The transaction state for the bin log events.
 */
public enum TransactionState {

    /**
     * The default transaction state.
     */
    NONE,
    /**
     * The start transaction state. Mostly should be assigned on the bin log QUERY_EVENT.
     */
    STARTED,
    /**
     * The end transaction state. Should be assigned on the XID_EVENT.
     */
    END
}
