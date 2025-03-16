/*
 * Copyright 2017 - 2024 Manah Shivaya (shivaya@ecosync.org)
 * Copyright 2017 - 2018 Shree Rama (rama@ecosync.org)
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
package org.ecosync.notificators;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.ecosync.database.StatisticsManager;
import org.ecosync.model.Event;
import org.ecosync.model.Position;
import org.ecosync.model.User;
import org.ecosync.notification.MessageException;
import org.ecosync.notification.NotificationFormatter;
import org.ecosync.notification.NotificationMessage;
import org.ecosync.sms.SmsManager;

@Singleton
public class NotificatorSms extends Notificator {

    private final SmsManager smsManager;
    private final StatisticsManager statisticsManager;

    @Inject
    public NotificatorSms(
            SmsManager smsManager, NotificationFormatter notificationFormatter, StatisticsManager statisticsManager) {
        super(notificationFormatter, "short");
        this.smsManager = smsManager;
        this.statisticsManager = statisticsManager;
    }

    @Override
    public void send(User user, NotificationMessage message, Event event, Position position) throws MessageException {
        if (user.getPhone() != null) {
            statisticsManager.registerSms();
            smsManager.sendMessage(user.getPhone(), message.getBody(), false);
        }
    }

}
