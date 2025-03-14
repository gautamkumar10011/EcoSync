package org.ecosync;

import io.netty.channel.Channel;
import org.ecosync.config.Config;
import org.ecosync.database.CommandsManager;
import org.ecosync.database.MediaManager;
import org.ecosync.database.StatisticsManager;
import org.ecosync.model.Device;
import org.ecosync.session.ConnectionManager;
import org.ecosync.session.DeviceSession;
import org.ecosync.session.cache.CacheManager;

import java.net.SocketAddress;
import java.util.HashSet;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BaseTest {

    protected <T extends BaseProtocolDecoder> T inject(T decoder) throws Exception {
        var config = new Config();
        decoder.setConfig(config);
        var device = mock(Device.class);
        when(device.getId()).thenReturn(1L);
        var cacheManager = mock(CacheManager.class);
        when(cacheManager.getConfig()).thenReturn(config);
        when(cacheManager.getObject(eq(Device.class), anyLong())).thenReturn(device);
        decoder.setCacheManager(cacheManager);
        var connectionManager = mock(ConnectionManager.class);
        var uniqueIdsProvided = new HashSet<Boolean>();
        when(connectionManager.getDeviceSession(any(), any(), any(), any(String[].class))).thenAnswer(invocation -> {
            var mock = new DeviceSession(
                    1L, "", null, mock(Protocol.class), mock(Channel.class), mock(SocketAddress.class));
            if (uniqueIdsProvided.isEmpty()) {
                if (invocation.getArguments().length > 3) {
                    uniqueIdsProvided.add(true);
                    return mock;
                }
                return null;
            } else {
                return mock;
            }
        });
        decoder.setConnectionManager(connectionManager);
        decoder.setStatisticsManager(mock(StatisticsManager.class));
        decoder.setMediaManager(mock(MediaManager.class));
        decoder.setCommandsManager(mock(CommandsManager.class));
        return decoder;
    }

    protected <T extends BaseFrameDecoder> T inject(T decoder) throws Exception {
        return decoder;
    }

    protected <T extends BaseProtocolEncoder> T inject(T encoder) throws Exception {
        var device = mock(Device.class);
        when(device.getId()).thenReturn(1L);
        when(device.getUniqueId()).thenReturn("123456789012345");
        var cacheManager = mock(CacheManager.class);
        when(cacheManager.getConfig()).thenReturn(mock(Config.class));
        when(cacheManager.getObject(eq(Device.class), anyLong())).thenReturn(device);
        encoder.setCacheManager(cacheManager);
        return encoder;
    }

}
