/*
 * Copyright 2018 - 2024 Manah Shivaya (shivaya@ecosync.org)
 * Copyright 2018 Shree Rama (rama@ecosync.org)
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

import org.ecosync.model.Event;
import org.ecosync.model.Notification;
import org.ecosync.model.Position;
import org.ecosync.model.User;
import org.ecosync.notification.MessageException;
import org.ecosync.notification.NotificationFormatter;
import org.ecosync.notification.NotificationMessage;

public abstract class Notificator {

    private final NotificationFormatter notificationFormatter;
    private final String templatePath;

    public Notificator(NotificationFormatter notificationFormatter, String templatePath) {
        this.notificationFormatter = notificationFormatter;
        this.templatePath = templatePath;
    }

    public void send(Notification notification, User user, Event event, Position position) throws MessageException {
        var message = notificationFormatter.formatMessage(notification, user, event, position, templatePath);
        send(user, message, event, position);
    }

    public void send(User user, NotificationMessage message, Event event, Position position) throws MessageException {
        throw new UnsupportedOperationException();
    }

}
