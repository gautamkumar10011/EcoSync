/*
 * Copyright 2016 - 2024 Manah Shivaya (shivaya@ecosync.org)
 * Copyright 2016 - 2017 Shree Rama (rama@ecosync.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ecosync.reports.model;

public class SummaryReportItem extends BaseReportItem {

    public long getEngineHours() {
        return endHours - startHours;
    }

    private long startHours; // milliseconds

    public long getStartHours() {
        return startHours;
    }

    public void setStartHours(long startHours) {
        this.startHours = startHours;
    }

    private long endHours; // milliseconds

    public long getEndHours() {
        return endHours;
    }

    public void setEndHours(long endHours) {
        this.endHours = endHours;
    }

}
