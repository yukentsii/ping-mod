package com.example;

import com.example.command.PingCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PingMod implements ModInitializer {
    public static final String MOD_ID = "ping-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing {}", MOD_ID);

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PingCommand.register(dispatcher, registryAccess, environment);
        });

        ServerTickEvents.END_SERVER_TICK.register(PingHudManager::onServerTick);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            if (handler.getPlayer() != null) {
                PingHudManager.onPlayerDisconnect(handler.getPlayer());
            }
        });
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
