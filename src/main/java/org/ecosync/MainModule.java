/*
 * Copyright 2018 - 2023 Manah Shivaya (shivaya@ecosync.org)
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
package org.ecosync;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsonp.JSONPModule;
import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.name.Names;
import io.netty.util.HashedWheelTimer;
import io.netty.util.Timer;
import org.apache.velocity.app.VelocityEngine;
import org.ecosync.broadcast.BroadcastService;
import org.ecosync.broadcast.MulticastBroadcastService;
import org.ecosync.broadcast.RedisBroadcastService;
import org.ecosync.broadcast.NullBroadcastService;
import org.ecosync.config.Config;
import org.ecosync.config.Keys;
import org.ecosync.database.LdapProvider;
import org.ecosync.database.OpenIdProvider;
import org.ecosync.database.StatisticsManager;
import org.ecosync.forward.EventForwarder;
import org.ecosync.forward.EventForwarderJson;
import org.ecosync.forward.EventForwarderAmqp;
import org.ecosync.forward.EventForwarderKafka;
import org.ecosync.forward.EventForwarderMqtt;
import org.ecosync.forward.PositionForwarder;
import org.ecosync.forward.PositionForwarderJson;
import org.ecosync.forward.PositionForwarderAmqp;
import org.ecosync.forward.PositionForwarderKafka;
import org.ecosync.forward.PositionForwarderRedis;
import org.ecosync.forward.PositionForwarderUrl;
import org.ecosync.forward.PositionForwarderMqtt;
import org.ecosync.forward.PositionForwarderWialon;
import org.ecosync.geocoder.AddressFormat;
import org.ecosync.geocoder.BanGeocoder;
import org.ecosync.geocoder.BingMapsGeocoder;
import org.ecosync.geocoder.FactualGeocoder;
import org.ecosync.geocoder.GeoapifyGeocoder;
import org.ecosync.geocoder.GeocodeFarmGeocoder;
import org.ecosync.geocoder.GeocodeXyzGeocoder;
import org.ecosync.geocoder.Geocoder;
import org.ecosync.geocoder.GisgraphyGeocoder;
import org.ecosync.geocoder.GoogleGeocoder;
import org.ecosync.geocoder.HereGeocoder;
import org.ecosync.geocoder.LocationIqGeocoder;
import org.ecosync.geocoder.MapQuestGeocoder;
import org.ecosync.geocoder.MapTilerGeocoder;
import org.ecosync.geocoder.MapboxGeocoder;
import org.ecosync.geocoder.MapmyIndiaGeocoder;
import org.ecosync.geocoder.NominatimGeocoder;
import org.ecosync.geocoder.OpenCageGeocoder;
import org.ecosync.geocoder.PositionStackGeocoder;
import org.ecosync.geocoder.PlusCodesGeocoder;
import org.ecosync.geocoder.TomTomGeocoder;
import org.ecosync.geocoder.GeocodeJsonGeocoder;
import org.ecosync.geolocation.GeolocationProvider;
import org.ecosync.geolocation.GoogleGeolocationProvider;
import org.ecosync.geolocation.OpenCellIdGeolocationProvider;
import org.ecosync.geolocation.UnwiredGeolocationProvider;
import org.ecosync.handler.CopyAttributesHandler;
import org.ecosync.handler.FilterHandler;
import org.ecosync.handler.GeocoderHandler;
import org.ecosync.handler.GeolocationHandler;
import org.ecosync.handler.SpeedLimitHandler;
import org.ecosync.handler.TimeHandler;
import org.ecosync.helper.ObjectMapperContextResolver;
import org.ecosync.helper.WebHelper;
import org.ecosync.mail.LogMailManager;
import org.ecosync.mail.MailManager;
import org.ecosync.mail.SmtpMailManager;
import org.ecosync.session.cache.CacheManager;
import org.ecosync.sms.HttpSmsClient;
import org.ecosync.sms.SmsManager;
import org.ecosync.sms.SnsSmsClient;
import org.ecosync.speedlimit.OverpassSpeedLimitProvider;
import org.ecosync.speedlimit.SpeedLimitProvider;
import org.ecosync.storage.DatabaseStorage;
import org.ecosync.storage.MemoryStorage;
import org.ecosync.storage.Storage;
import org.ecosync.web.WebServer;
import org.ecosync.api.security.LoginService;

import jakarta.annotation.Nullable;
import jakarta.inject.Singleton;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainModule extends AbstractModule {

    private final String configFile;

    public MainModule(String configFile) {
        this.configFile = configFile;
    }

    @Override
    protected void configure() {
        bindConstant().annotatedWith(Names.named("configFile")).to(configFile);
        bind(Config.class).asEagerSingleton();
        bind(Timer.class).to(HashedWheelTimer.class).in(Scopes.SINGLETON);
    }

    @Singleton
    @Provides
    public static ExecutorService provideExecutorService() {
        return Executors.newCachedThreadPool();
    }

    @Singleton
    @Provides
    public static Storage provideStorage(Injector injector, Config config) {
        if (config.getBoolean(Keys.DATABASE_MEMORY)) {
            return injector.getInstance(MemoryStorage.class);
        } else {
            return injector.getInstance(DatabaseStorage.class);
        }
    }

    @Singleton
    @Provides
    public static ObjectMapper provideObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSONPModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return objectMapper;
    }

    @Singleton
    @Provides
    public static Client provideClient(ObjectMapperContextResolver objectMapperContextResolver) {
        return ClientBuilder.newClient().register(objectMapperContextResolver);
    }

    @Singleton
    @Provides
    public static SmsManager provideSmsManager(Config config, Client client) {
        if (config.hasKey(Keys.SMS_HTTP_URL)) {
            return new HttpSmsClient(config, client);
        } else if (config.hasKey(Keys.SMS_AWS_REGION)) {
            return new SnsSmsClient(config);
        }
        return null;
    }

    @Singleton
    @Provides
    public static MailManager provideMailManager(Config config, StatisticsManager statisticsManager) {
        if (config.getBoolean(Keys.MAIL_DEBUG)) {
            return new LogMailManager();
        } else {
            return new SmtpMailManager(config, statisticsManager);
        }
    }

    @Singleton
    @Provides
    public static LdapProvider provideLdapProvider(Config config) {
        if (config.hasKey(Keys.LDAP_URL)) {
            return new LdapProvider(config);
        }
        return null;
    }

    @Singleton
    @Provides
    public static OpenIdProvider provideOpenIDProvider(
        Config config, LoginService loginService, ObjectMapper objectMapper
        ) throws InterruptedException, IOException, URISyntaxException {
        if (config.hasKey(Keys.OPENID_CLIENT_ID)) {
            return new OpenIdProvider(config, loginService, HttpClient.newHttpClient(), objectMapper);
        }
        return null;
    }

    @Provides
    public static WebServer provideWebServer(Injector injector, Config config) {
        if (config.getInteger(Keys.WEB_PORT) > 0) {
            return new WebServer(injector, config);
        }
        return null;
    }

    @Singleton
    @Provides
    public static Geocoder provideGeocoder(Config config, Client client, StatisticsManager statisticsManager) {
        if (config.getBoolean(Keys.GEOCODER_ENABLE)) {
            String type = config.getString(Keys.GEOCODER_TYPE);
            String url = config.getString(Keys.GEOCODER_URL);
            String key = config.getString(Keys.GEOCODER_KEY);
            String language = config.getString(Keys.GEOCODER_LANGUAGE);
            String formatString = config.getString(Keys.GEOCODER_FORMAT);
            AddressFormat addressFormat = formatString != null ? new AddressFormat(formatString) : new AddressFormat();

            int cacheSize = config.getInteger(Keys.GEOCODER_CACHE_SIZE);
            Geocoder geocoder = switch (type) {
                case "pluscodes" -> new PlusCodesGeocoder();
                case "nominatim" -> new NominatimGeocoder(client, url, key, language, cacheSize, addressFormat);
                case "locationiq" -> new LocationIqGeocoder(client, url, key, language, cacheSize, addressFormat);
                case "gisgraphy" -> new GisgraphyGeocoder(client, url, cacheSize, addressFormat);
                case "mapquest" -> new MapQuestGeocoder(client, url, key, cacheSize, addressFormat);
                case "opencage" -> new OpenCageGeocoder(client, url, key, language, cacheSize, addressFormat);
                case "bingmaps" -> new BingMapsGeocoder(client, url, key, cacheSize, addressFormat);
                case "factual" -> new FactualGeocoder(client, url, key, cacheSize, addressFormat);
                case "geocodefarm" -> new GeocodeFarmGeocoder(client, key, language, cacheSize, addressFormat);
                case "geocodexyz" -> new GeocodeXyzGeocoder(client, key, cacheSize, addressFormat);
                case "ban" -> new BanGeocoder(client, cacheSize, addressFormat);
                case "here" -> new HereGeocoder(client, url, key, language, cacheSize, addressFormat);
                case "mapmyindia" -> new MapmyIndiaGeocoder(client, url, key, cacheSize, addressFormat);
                case "tomtom" -> new TomTomGeocoder(client, url, key, cacheSize, addressFormat);
                case "positionstack" -> new PositionStackGeocoder(client, key, cacheSize, addressFormat);
                case "mapbox" -> new MapboxGeocoder(client, key, cacheSize, addressFormat);
                case "maptiler" -> new MapTilerGeocoder(client, key, cacheSize, addressFormat);
                case "geoapify" -> new GeoapifyGeocoder(client, key, language, cacheSize, addressFormat);
                case "geocodejson" -> new GeocodeJsonGeocoder(client, url, key, language, cacheSize, addressFormat);
                default -> new GoogleGeocoder(client, url, key, language, cacheSize, addressFormat);
            };
            geocoder.setStatisticsManager(statisticsManager);
            return geocoder;
        }
        return null;
    }

    @Singleton
    @Provides
    public static GeolocationProvider provideGeolocationProvider(Config config, Client client) {
        if (config.getBoolean(Keys.GEOLOCATION_ENABLE)) {
            String type = config.getString(Keys.GEOLOCATION_TYPE, "google");
            String url = config.getString(Keys.GEOLOCATION_URL);
            String key = config.getString(Keys.GEOLOCATION_KEY);
            return switch (type) {
                case "opencellid" -> new OpenCellIdGeolocationProvider(client, url, key);
                case "unwired" -> new UnwiredGeolocationProvider(client, url, key);
                default -> new GoogleGeolocationProvider(client, key);
            };
        }
        return null;
    }

    @Singleton
    @Provides
    public static SpeedLimitProvider provideSpeedLimitProvider(Config config, Client client) {
        if (config.getBoolean(Keys.SPEED_LIMIT_ENABLE)) {
            String type = config.getString(Keys.SPEED_LIMIT_TYPE, "overpass");
            String url = config.getString(Keys.SPEED_LIMIT_URL);
            return switch (type) {
                case "overpass" -> new OverpassSpeedLimitProvider(config, client, url);
                default -> throw new IllegalArgumentException("Unknown speed limit provider");
            };
        }
        return null;
    }

    @Singleton
    @Provides
    public static GeolocationHandler provideGeolocationHandler(
            Config config, @Nullable GeolocationProvider geolocationProvider, CacheManager cacheManager,
            StatisticsManager statisticsManager) {
        if (geolocationProvider != null) {
            return new GeolocationHandler(config, geolocationProvider, cacheManager, statisticsManager);
        }
        return null;
    }

    @Singleton
    @Provides
    public static GeocoderHandler provideGeocoderHandler(
            Config config, @Nullable Geocoder geocoder, CacheManager cacheManager) {
        if (geocoder != null) {
            return new GeocoderHandler(config, geocoder, cacheManager);
        }
        return null;
    }

    @Singleton
    @Provides
    public static SpeedLimitHandler provideSpeedLimitHandler(@Nullable SpeedLimitProvider speedLimitProvider) {
        if (speedLimitProvider != null) {
            return new SpeedLimitHandler(speedLimitProvider);
        }
        return null;
    }

    @Singleton
    @Provides
    public static CopyAttributesHandler provideCopyAttributesHandler(Config config, CacheManager cacheManager) {
        if (config.getBoolean(Keys.PROCESSING_COPY_ATTRIBUTES_ENABLE)) {
            return new CopyAttributesHandler(config, cacheManager);
        }
        return null;
    }

    @Singleton
    @Provides
    public static FilterHandler provideFilterHandler(
            Config config, CacheManager cacheManager, Storage storage, StatisticsManager statisticsManager) {
        if (config.getBoolean(Keys.FILTER_ENABLE)) {
            return new FilterHandler(config, cacheManager, storage, statisticsManager);
        }
        return null;
    }

    @Singleton
    @Provides
    public static TimeHandler provideTimeHandler(Config config) {
        if (config.hasKey(Keys.TIME_OVERRIDE)) {
            return new TimeHandler(config);
        }
        return null;
    }

    @Singleton
    @Provides
    public static BroadcastService provideBroadcastService(
            Config config, ExecutorService executorService, ObjectMapper objectMapper) throws IOException {
        if (config.hasKey(Keys.BROADCAST_TYPE)) {
            return switch (config.getString(Keys.BROADCAST_TYPE)) {
                case "multicast" -> new MulticastBroadcastService(config, executorService, objectMapper);
                case "redis" -> new RedisBroadcastService(config, executorService, objectMapper);
                default -> new NullBroadcastService();
            };
        }
        return new NullBroadcastService();
    }

    @Singleton
    @Provides
    public static EventForwarder provideEventForwarder(Config config, Client client, ObjectMapper objectMapper) {
        if (config.hasKey(Keys.EVENT_FORWARD_URL)) {
            String forwardType = config.getString(Keys.EVENT_FORWARD_TYPE);
            return switch (forwardType) {
                case "amqp" -> new EventForwarderAmqp(config, objectMapper);
                case "kafka" -> new EventForwarderKafka(config, objectMapper);
                case "mqtt" -> new EventForwarderMqtt(config, objectMapper);
                default -> new EventForwarderJson(config, client);
            };
        }
        return null;
    }

    @Singleton
    @Provides
    public static PositionForwarder providePositionForwarder(
            Config config, Client client, ExecutorService executorService,
            ObjectMapper objectMapper, CacheManager cacheManager) {
        if (config.hasKey(Keys.FORWARD_URL)) {
            return switch (config.getString(Keys.FORWARD_TYPE)) {
                case "json" -> new PositionForwarderJson(config, client, objectMapper, cacheManager);
                case "amqp" -> new PositionForwarderAmqp(config, objectMapper);
                case "kafka" -> new PositionForwarderKafka(config, objectMapper);
                case "mqtt" -> new PositionForwarderMqtt(config, objectMapper);
                case "redis" -> new PositionForwarderRedis(config, objectMapper);
                case "wialon" -> new PositionForwarderWialon(config, executorService, "1.0", false);
                default -> new PositionForwarderUrl(config, client, objectMapper);
            };
        }
        return null;
    }

    @Singleton
    @Provides
    public static VelocityEngine provideVelocityEngine(Config config) {
        Properties properties = new Properties();
        properties.setProperty("resource.loader.file.path", config.getString(Keys.TEMPLATES_ROOT) + "/");
        properties.setProperty("web.url", WebHelper.retrieveWebUrl(config));

        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init(properties);
        return velocityEngine;
    }

}
