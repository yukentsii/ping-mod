package com.example;

import net.minecraft.network.protocol.common.ClientboundPingPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public final class PingManager {
    private static final AtomicInteger COUNTER = new AtomicInteger(1000);
    private static final Map<Integer, PingRequest> PENDING_REQUESTS = new ConcurrentHashMap<>();

    private record PingRequest(long startTime, Consumer<Integer> callback, MinecraftServer server) {}

    private PingManager() {}

    public static void requestPing(ServerPlayer player, Consumer<Integer> callback) {
        if (player.connection == null) {
            callback.accept(-1);
            return;
        }

        // Clean up any stale requests older than 10 seconds
        long now = System.currentTimeMillis();
        PENDING_REQUESTS.entrySet().removeIf(entry -> now - entry.getValue().startTime() > 10000);

        int id = COUNTER.incrementAndGet();
        PENDING_REQUESTS.put(id, new PingRequest(now, callback, player.level().getServer()));

        try {
            player.connection.send(new ClientboundPingPacket(id));
        } catch (Exception e) {
            PENDING_REQUESTS.remove(id);
            int fallback = player.connection != null ? player.connection.latency() : -1;
            callback.accept(fallback);
        }
    }

    public static void handlePong(int id) {
        PingRequest request = PENDING_REQUESTS.remove(id);
        if (request != null) {
            long elapsed = System.currentTimeMillis() - request.startTime();
            int ping = (int) Math.max(0, elapsed);
            if (request.server() != null) {
                request.server().execute(() -> request.callback().accept(ping));
            } else {
                request.callback().accept(ping);
            }
        }
    }
}
