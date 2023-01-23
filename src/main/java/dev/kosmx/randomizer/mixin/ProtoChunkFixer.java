package dev.kosmx.randomizer.mixin;


import dev.kosmx.randomizer.inverter.Inverter;
import net.minecraft.block.BlockState;
import net.minecraft.registry.Registry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.chunk.BlendingData;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(ProtoChunk.class)
public abstract class ProtoChunkFixer extends Chunk {
    public ProtoChunkFixer(ChunkPos pos, UpgradeData upgradeData, HeightLimitView heightLimitView, Registry<Biome> biome, long inhabitedTime, @Nullable ChunkSection[] sectionArrayInitializer, @Nullable BlendingData blendingData) {
        super(pos, upgradeData, heightLimitView, biome, inhabitedTime, sectionArrayInitializer, blendingData);
    }

    @Shadow
    public abstract BlockState getBlockState(BlockPos pos);

    @Shadow @Final
    private List<BlockPos> lightSources;

    @Shadow private volatile ChunkStatus status;

    @Shadow public abstract HeightLimitView getHeightLimitView();

    @Unique private BlockPos invertBlockPos(BlockPos pos) {
        if(Inverter.INSTANCE.shouldInvertChunk(this.getPos()) && this.status.isAtLeast(ChunkStatus.FEATURES)){

            int bottomY = getBottomY();
            int topY = getTopY();


            return new BlockPos(pos.getX(), topY + bottomY - pos.getY() -1, pos.getZ());
        }
        return pos;
    }

    //@Shadow public abstract Biome getBiomeForNoiseGen(int biomeX, int biomeY, int biomeZ);

    @Inject(method = "setBlockState", at = @At("HEAD"))
    private void setBlockFix(BlockPos pos, BlockState state, boolean moved, CallbackInfoReturnable<BlockState> cir){
        if(getBlockState(pos).getLuminance() > 0){
            var old = new BlockPos((pos.getX() & 15) + this.getPos().getStartX(), pos.getY(), (pos.getZ() & 15) + this.getPos().getStartZ());
            this.lightSources.removeIf(blockPos -> blockPos.equals(old));
        }
    }

    @ModifyVariable(method = "setBlockState", at = @At("HEAD"), argsOnly = true)
    private BlockPos modifyPos(BlockPos pos){
        return invertBlockPos(pos);
    }


    /*
    @ModifyVariable(method = "setBlockEntity", at = @At("HEAD"), argsOnly = true)
    private BlockEntity setBlockEntityFix(BlockEntity blockEntity) {
        if (!Inverter.INSTANCE.shouldInvertChunk(getPos())) return blockEntity;
        final var nbt = blockEntity.createNbtWithId();
        final var pos = blockEntity.getPos();

        return BlockEntity.createFromNbt(invertBlockPos(pos), getBlockState(pos), nbt);
    }*/

    @ModifyVariable(method = "getBlockEntity", at = @At("HEAD"), argsOnly = true)
    private BlockPos getBlockEntityFix(BlockPos pos) {
        return invertBlockPos(pos);
    }

}
