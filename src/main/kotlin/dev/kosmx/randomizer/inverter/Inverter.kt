package dev.kosmx.randomizer.inverter

import dev.kosmx.randomizer.mixin.inverter.StructurePieceAccessor
import kotlinx.serialization.Serializable
import net.minecraft.block.Block
import net.minecraft.block.entity.BlockEntity
import net.minecraft.entity.Entity
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.ChunkPos
import net.minecraft.world.StructureWorldAccess
import net.minecraft.world.chunk.Chunk
import java.util.*
import kotlin.math.min

object Inverter {
    var config: InverterConfig = InverterConfig()
        private set

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
        if (shouldInvertChunk(chunk.pos)) {
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

                        val nbtA = world.getBlockEntity(a)?.createNbtWithId()
                        val nbtB = world.getBlockEntity(b)?.createNbtWithId()

                        world.setBlockState(a, blockB, Block.NOTIFY_LISTENERS)
                        if (StructurePieceAccessor.getBlocksNeedingPostProcessing().contains(blockB.block)) {
                            chunk.markBlockForPostProcessing(a)
                        }
                        nbtB?.let { nbt ->
                            chunk.setBlockEntity(BlockEntity.createFromNbt(a, blockB, nbt))
                        }

                        //this.setBlockEntity(tmp2);
                        world.setBlockState(b, blockA, Block.NOTIFY_LISTENERS)
                        if (StructurePieceAccessor.getBlocksNeedingPostProcessing().contains(blockA.block)) {
                            chunk.markBlockForPostProcessing(b)
                        }
                        nbtA?.let { nbt ->
                            chunk.setBlockEntity(BlockEntity.createFromNbt(b, blockA, nbt))
                        }
                    }
                }
            }
        }
    }

    /**
     * Tick in void function
     * @return true if needs cancelling
     */
    fun tickInVoid(entity: Entity): Boolean {
        return if (config.looper) {
            entity.velocity.let {
                entity.teleport(entity.x, entity.world.topY + 64.0, entity.z)
                entity.velocity = it // some stuff can't be fixed here
            }
            true
        } else false
    }

    /**
     *
     */
    fun computeFallDamage(entity: LivingEntity, fallDamage: Int): Int {
        if (config.maxFallDamage >= 0 && (!config.fallLimitOnlyPlayer || entity is PlayerEntity)) {
            return min(fallDamage, config.maxFallDamage)
        }
        return fallDamage
    }

}
@Serializable
data class InverterConfig(
    val enabled: Boolean = false,
    val modifySpawnMechanics: Boolean = false,
    val invertPercentage: Float = 1f,
    val looper: Boolean = false, // loop back entities fallen out of the world
    val fallLimitOnlyPlayer: Boolean = true,
    val maxFallDamage: Int = -1, // Max allowed fall damage, negative is disabled
)
