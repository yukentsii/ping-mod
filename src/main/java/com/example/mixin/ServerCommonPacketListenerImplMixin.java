package com.example.mixin;

import com.example.PingManager;
import net.minecraft.network.protocol.common.ServerboundPongPacket;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonPacketListenerImpl.class)
public class ServerCommonPacketListenerImplMixin {

    @Inject(method = "handlePong", at = @At("HEAD"))
    private void onHandlePong(ServerboundPongPacket packet, CallbackInfo ci) {
        PingManager.handlePong(packet.getId());
    }
}
