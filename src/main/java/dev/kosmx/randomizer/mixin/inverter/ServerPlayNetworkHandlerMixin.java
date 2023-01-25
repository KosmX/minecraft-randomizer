package dev.kosmx.randomizer.mixin.inverter;

import dev.kosmx.randomizer.inverter.Inverter;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerPlayNetworkHandler.class)
public abstract class ServerPlayNetworkHandlerMixin {
    @Shadow public ServerPlayerEntity player;

    @Shadow private double lastTickY;

    @Redirect(method = "onPlayerMove", at = @At(value = "FIELD", target = "Lnet/minecraft/server/network/ServerPlayNetworkHandler;lastTickY:D"))
    private double fixSpeedCapping(ServerPlayNetworkHandler instance) {
        if (Inverter.INSTANCE.getConfig().getLooper() && player.getY() - lastTickY > 512) {
            return lastTickY + 512;
        }
        return lastTickY;
    }
}
