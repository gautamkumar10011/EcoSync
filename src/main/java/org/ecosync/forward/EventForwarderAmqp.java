/*
 * Copyright 2023 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync.forward;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.ecosync.config.Config;
import org.ecosync.config.Keys;

import java.io.IOException;

public class EventForwarderAmqp implements EventForwarder {

    private final AmqpClient amqpClient;
    private final ObjectMapper objectMapper;

    public EventForwarderAmqp(Config config, ObjectMapper objectMapper) {
        String connectionUrl = config.getString(Keys.EVENT_FORWARD_URL);
        String exchange = config.getString(Keys.EVENT_FORWARD_EXCHANGE);
        String topic = config.getString(Keys.EVENT_FORWARD_TOPIC);
        this.objectMapper = objectMapper;
        amqpClient = new AmqpClient(connectionUrl, exchange, topic);
    }

    @Override
    public void forward(EventData eventData, ResultHandler resultHandler) {
        try {
            String value = objectMapper.writeValueAsString(eventData);
            amqpClient.publishMessage(value);
            resultHandler.onResult(true, null);
        } catch (IOException e) {
            resultHandler.onResult(false, e);
        }
    }
}
