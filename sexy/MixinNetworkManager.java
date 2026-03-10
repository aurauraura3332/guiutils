package com.guiutils.mixin;

import com.guiutils.core.GUIUtilsMod;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts NetworkManager#sendPacket to support packet delay.
 * Requires Mixin library on the classpath (e.g. sponge mixin 0.7.x).
 */
@Mixin(NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V",
            at = @At("HEAD"),
            cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        if (GUIUtilsMod.getPacketManager() != null) {
            if (GUIUtilsMod.getPacketManager().interceptPacket(packet)) {
                ci.cancel(); // packet queued, do not send now
            }
        }
    }
}
