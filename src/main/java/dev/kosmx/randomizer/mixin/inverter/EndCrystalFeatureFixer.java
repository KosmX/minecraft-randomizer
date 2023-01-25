package dev.kosmx.randomizer.mixin.inverter;

import dev.kosmx.randomizer.inverter.Inverter;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.EndSpikeFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndSpikeFeature.class)
public class EndCrystalFeatureFixer {
    @Redirect(method = "generateSpike", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/decoration/EndCrystalEntity;refreshPositionAndAngles(DDDFF)V"))
    private void updatePosition(EndCrystalEntity instance, double x, double y, double z, float yaw, float pitch) {
        var blockPos = new BlockPos(x, y, z);
        var pos = new ChunkPos(blockPos);
        if (Inverter.INSTANCE.shouldInvertChunk(pos)) {
            instance.refreshPositionAndAngles(x, 255 - y, z, yaw, pitch);
        } else {
            instance.refreshPositionAndAngles(x, y, z, yaw, pitch);
        }
    }
}
