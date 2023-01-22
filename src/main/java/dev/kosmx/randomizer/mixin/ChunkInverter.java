package dev.kosmx.randomizer.mixin;

import dev.kosmx.randomizer.inverter.Inverter;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkGenerator.class)
public class ChunkInverter {

    @Inject(method = "generateFeatures", at = @At(value = "TAIL"))
    private void test1(StructureWorldAccess world, Chunk chunk, StructureAccessor structureAccessor, CallbackInfo ci){
        if(Inverter.INSTANCE.shouldInvertChunk(chunk.getPos())) {
            Inverter.INSTANCE.invert(chunk, world);
        }

    }


}
