package com.example;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class PingHudManager {

    private static class PlayerHudState {
        boolean enabled = false;
        int intervalSeconds = 1; // default 1 second
        int tickCounter = 0;
        int lastPing = -1;
    }

    private static final Map<UUID, PlayerHudState> PLAYER_STATES = new ConcurrentHashMap<>();

    private PingHudManager() {}

    private static PlayerHudState getState(UUID uuid) {
        return PLAYER_STATES.computeIfAbsent(uuid, k -> new PlayerHudState());
    }

    public static boolean toggle(ServerPlayer player) {
        PlayerHudState state = getState(player.getUUID());
        state.enabled = !state.enabled;
        state.tickCounter = 0;
        if (!state.enabled) {
            player.sendOverlayMessage(Component.empty());
        } else {
            updatePlayerPing(player, state);
        }
        return state.enabled;
    }

    public static void setEnabled(ServerPlayer player, boolean enabled) {
        PlayerHudState state = getState(player.getUUID());
        state.enabled = enabled;
        state.tickCounter = 0;
        if (!enabled) {
            player.sendOverlayMessage(Component.empty());
        } else {
            updatePlayerPing(player, state);
        }
    }

    public static boolean isEnabled(ServerPlayer player) {
        return getState(player.getUUID()).enabled;
    }

    public static void setRefreshInterval(ServerPlayer player, int seconds) {
        PlayerHudState state = getState(player.getUUID());
        state.intervalSeconds = Math.max(1, Math.min(60, seconds));
        state.tickCounter = 0;
    }

    public static int getRefreshInterval(ServerPlayer player) {
        return getState(player.getUUID()).intervalSeconds;
    }

    public static void onServerTick(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerHudState state = PLAYER_STATES.get(player.getUUID());
            if (state == null || !state.enabled) {
                continue;
            }

            state.tickCounter++;
            int intervalTicks = state.intervalSeconds * 20;

            if (state.tickCounter >= intervalTicks) {
                state.tickCounter = 0;
                updatePlayerPing(player, state);
            } else if (state.lastPing >= 0 && state.tickCounter % 20 == 0) {
                // Keep the action bar text alive between refresh ticks
                player.sendOverlayMessage(createHudComponent(state.lastPing));
            }
        }
    }

    private static void updatePlayerPing(ServerPlayer player, PlayerHudState state) {
        PingManager.requestPing(player, ping -> {
            if (ping >= 0) {
                state.lastPing = ping;
                if (state.enabled && player.connection != null) {
                    player.sendOverlayMessage(createHudComponent(ping));
                }
            }
        });
    }

    public static Component createHudComponent(int ping) {
        return Component.literal("Ping: ")
            .withStyle(ChatFormatting.GRAY)
            .append(
                Component.literal(ping + "ms")
                    .withStyle(getPingColor(ping))
            );
    }

    public static ChatFormatting getPingColor(int ping) {
        if (ping < 80) {
            return ChatFormatting.GREEN;
        } else if (ping <= 150) {
            return ChatFormatting.YELLOW;
        } else {
            return ChatFormatting.RED;
        }
    }

    public static void onPlayerDisconnect(ServerPlayer player) {
        PLAYER_STATES.remove(player.getUUID());
    }
}
