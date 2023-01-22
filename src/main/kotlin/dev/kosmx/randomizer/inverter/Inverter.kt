package dev.kosmx.randomizer.inverter

import dev.kosmx.randomizer.mixin.StructurePieceAccessor
import kotlinx.serialization.Serializable
import net.minecraft.block.Block
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.chunk.Chunk
import java.util.*

object Inverter {
    private var config: InverterConfig = InverterConfig()

    operator fun invoke(config: InverterConfig) {
        this.config = config
    }

    fun shouldInvertChunk(pos: ChunkPos): Boolean {
        if (!config.enabled) return false
        return if (config.invertPercentage == 1f) true else Random(
            pos.hashCode().toLong()
        ).nextFloat() < config.invertPercentage
    }


    fun invert(chunk: Chunk, world: StructureWorldAccess) {
        val pos = chunk.pos
        val bottomY = world.bottomY
        val topY: Int = world.dimension.logicalHeight + bottomY
        val middle = (topY - bottomY) / 2
        for (x in 0..15) {
            for (z in 0..15) {
                for (h in 0 until middle) {
                    val a = BlockPos(pos.x * 16 + x, bottomY + h, pos.z * 16 + z)
                    val b = BlockPos(pos.x * 16 + x, topY - 1 - h, pos.z * 16 + z)
                    val blockA = world.getBlockState(a)
                    val blockB = world.getBlockState(b)
                    world.setBlockState(a, blockB, Block.NOTIFY_LISTENERS)
                    if (StructurePieceAccessor.getBlocksNeedingPostProcessing().contains(blockB.block)) {
                        world.getChunk(a).markBlockForPostProcessing(a)
                    }
                    //this.setBlockEntity(tmp2);
                    world.setBlockState(b, blockA, Block.NOTIFY_LISTENERS)
                    if (StructurePieceAccessor.getBlocksNeedingPostProcessing().contains(blockA.block)) {
                        world.getChunk(b).markBlockForPostProcessing(b)
                    }
                }
            }
        }
    }

}
@Serializable
data class InverterConfig(
    val enabled: Boolean = false,
    val modifySpawnMechanics: Boolean = false,
    val invertPercentage: Float = 1f
)