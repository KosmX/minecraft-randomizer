package dev.kosmx.randomizer.mixin.inverter;

import dev.kosmx.randomizer.inverter.Inverter;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {
    @Inject(method = "computeFallDamage", at = @At("RETURN"), cancellable = true)
    private void alterFallDamage(float fallDistance, float damageMultiplier, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(Inverter.INSTANCE.computeFallDamage((LivingEntity) (Object)this, cir.getReturnValue()));
    }
}
