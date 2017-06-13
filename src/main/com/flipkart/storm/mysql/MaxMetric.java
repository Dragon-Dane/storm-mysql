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


import org.apache.storm.metric.api.ICombiner;

/**
 * Max of all data points in a time slice.
 */
public class MaxMetric implements ICombiner<Integer> {

    @Override
    public Integer identity() {
        return 0;
    }

    @Override
    public Integer combine(Integer val1, Integer val2) {
        if (val1 == null) {
            return val2;
        }
        if (val2 == null) {
            return val1;
        }
        return Math.max(val1, val2);
    }

}
