package dev.kosmx.randomizer.mixin.inverter;

import dev.kosmx.randomizer.inverter.Inverter;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(method = "attemptTickInVoid", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/Entity;tickInVoid()V"), cancellable = true)
    private void attemptTickInVoidModified(CallbackInfo ci) {
        if (Inverter.INSTANCE.tickInVoid((Entity)(Object)this)) {
            ci.cancel();
        }
    }
}
