package com.example.command;

import com.example.PingHudManager;
import com.example.PingManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;

public final class PingCommand {

    private PingCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        register(dispatcher, null, null);
    }

    public static void register(
            CommandDispatcher<CommandSourceStack> dispatcher,
            CommandBuildContext registryAccess,
            Commands.CommandSelection environment
    ) {
        dispatcher.register(
            Commands.literal("ping")
                .executes(PingCommand::executeSelf)
                .then(
                    Commands.literal("refresh")
                        .then(
                            Commands.argument("seconds", IntegerArgumentType.integer(1, 60))
                                .executes(PingCommand::executeSetRefresh)
                        )
                )
                .then(
                    Commands.argument("player", EntityArgument.player())
                        .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                        .executes(PingCommand::executeOther)
                )
        );

        dispatcher.register(
            Commands.literal("hudping")
                .executes(PingCommand::executeToggleHud)
                .then(
                    Commands.literal("on")
                        .executes(context -> executeSetHud(context, true))
                )
                .then(
                    Commands.literal("off")
                        .executes(context -> executeSetHud(context, false))
                )
                .then(
                    Commands.literal("refresh")
                        .then(
                            Commands.argument("seconds", IntegerArgumentType.integer(1, 60))
                                .executes(PingCommand::executeSetRefresh)
                        )
                )
        );
    }

    private static int executeSelf(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(
                Component.literal("Only players can check their own ping!")
                    .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        ServerGamePacketListenerImpl connection = player.connection;
        if (connection == null) {
            context.getSource().sendFailure(
                Component.literal("Could not retrieve ping: network connection is unavailable.")
                    .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        CommandSourceStack source = context.getSource();
        PingManager.requestPing(player, ping -> {
            if (ping < 0) {
                source.sendFailure(
                    Component.literal("Could not retrieve ping: network connection is unavailable.")
                        .withStyle(ChatFormatting.RED)
                );
                return;
            }

            Component message = Component.literal("Your ping: ")
                .withStyle(ChatFormatting.GRAY)
                .append(
                    Component.literal(ping + "ms")
                        .withStyle(getPingColor(ping))
                );

            source.sendSuccess(() -> message, false);
        });

        return 1;
    }

    private static int executeOther(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer target = EntityArgument.getPlayer(context, "player");

        ServerGamePacketListenerImpl connection = target.connection;
        if (connection == null) {
            context.getSource().sendFailure(
                Component.literal("Could not retrieve ping: network connection is unavailable.")
                    .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        CommandSourceStack source = context.getSource();
        String targetName = target.getName().getString();

        PingManager.requestPing(target, ping -> {
            if (ping < 0) {
                source.sendFailure(
                    Component.literal("Could not retrieve ping: network connection is unavailable.")
                        .withStyle(ChatFormatting.RED)
                );
                return;
            }

            Component message = Component.literal(targetName + "'s ping: ")
                .withStyle(ChatFormatting.GRAY)
                .append(
                    Component.literal(ping + "ms")
                        .withStyle(getPingColor(ping))
                );

            source.sendSuccess(() -> message, false);
        });

        return 1;
    }

    private static int executeToggleHud(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(
                Component.literal("Only players can toggle the ping HUD!")
                    .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        boolean enabled = PingHudManager.toggle(player);
        Component message = Component.literal("Ping HUD: ")
            .withStyle(ChatFormatting.GRAY)
            .append(
                Component.literal(enabled ? "Enabled" : "Disabled")
                    .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)
            );

        context.getSource().sendSuccess(() -> message, false);
        return 1;
    }

    private static int executeSetHud(CommandContext<CommandSourceStack> context, boolean enabled) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(
                Component.literal("Only players can control the ping HUD!")
                    .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        PingHudManager.setEnabled(player, enabled);
        Component message = Component.literal("Ping HUD: ")
            .withStyle(ChatFormatting.GRAY)
            .append(
                Component.literal(enabled ? "Enabled" : "Disabled")
                    .withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.RED)
            );

        context.getSource().sendSuccess(() -> message, false);
        return 1;
    }

    private static int executeSetRefresh(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendFailure(
                Component.literal("Only players can set their ping refresh interval!")
                    .withStyle(ChatFormatting.RED)
            );
            return 0;
        }

        int seconds = IntegerArgumentType.getInteger(context, "seconds");
        PingHudManager.setRefreshInterval(player, seconds);

        Component message = Component.literal("Ping HUD refresh interval set to ")
            .withStyle(ChatFormatting.GRAY)
            .append(
                Component.literal(seconds + "s")
                    .withStyle(ChatFormatting.GREEN)
            );

        context.getSource().sendSuccess(() -> message, false);
        return 1;
    }

    private static ChatFormatting getPingColor(int ping) {
        if (ping < 80) {
            return ChatFormatting.GREEN;
        } else if (ping <= 150) {
            return ChatFormatting.YELLOW;
        } else {
            return ChatFormatting.RED;
        }
    }
}
