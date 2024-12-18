/*
 * This file is part of the LegacyRaid project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Crystal0404 and contributors
 *
 * LegacyRaid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License.
 *
 * LegacyRaid is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with LegacyRaid.  If not, see <https://www.gnu.org/licenses/>.
 */

package crystal0404.legacyraid.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.raid.Raid;
import net.minecraft.world.Heightmap;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.util.Optional;
import java.util.function.Supplier;

@Mixin(Raid.class)
public abstract class RaidMixin {
    @Shadow
    @Final
    private ServerWorld world;

    @Shadow
    private BlockPos center;

    @Shadow
    private int preRaidTicks;

    @ModifyArg(
            method = "start",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/network/ServerPlayerEntity;" +
                            "getStatusEffect(Lnet/minecraft/registry/entry/RegistryEntry;)" +
                            "Lnet/minecraft/entity/effect/StatusEffectInstance;"
            ),
            index = 0
    )
    private RegistryEntry<StatusEffect> startMixin(RegistryEntry<StatusEffect> original) {
        return StatusEffects.BAD_OMEN;
    }

    @ModifyArg(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Optional;orElseGet(Ljava/util/function/Supplier;)Ljava/lang/Object;"
            )
    )
    private Supplier<? extends BlockPos> tickMixin_findRandomRaidersSpawnLocation(
            Supplier<? extends BlockPos> original,
            @Local(ordinal = 1) int j
    ) {
        return () -> this.legacyraid$getRavagerSpawnLocation(j, 20);
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "CONSTANT",
                    args = "intValue=5",
                    ordinal = 1
            )
    )
    private int tickMixin_modifyNumberOfAttempts(int original) {
        return 3;
    }

    @WrapOperation(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/raid/Raid;getRaidersSpawnLocation()Ljava/util/Optional;"
            )
    )
    private Optional<BlockPos> tickMixin_getRaidersSpawnLocation(
            Raid instance,
            Operation<Optional<BlockPos>> original
    ) {
        return this.legacyraid$preCalculateRavagerSpawnLocation(this.preRaidTicks < 100 ? 1 : 0);
    }

    // from Minecraft-1.21.1
    @Unique
    @Nullable
    @SuppressWarnings("deprecation")
    private BlockPos legacyraid$getRavagerSpawnLocation(int proximity, int tries) {
        int i = proximity == 0 ? 2 : 2 - proximity;
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        SpawnLocation spawnLocation = SpawnRestriction.getLocation(EntityType.RAVAGER);

        for (int j = 0; j < tries; j++) {
            float f = this.world.random.nextFloat() * (float) (Math.PI * 2);
            int k = this.center.getX() + MathHelper.floor(
                    MathHelper.cos(f) * 32.0F * (float) i + this.world.random.nextInt(5)
            );
            int l = this.center.getZ() + MathHelper.floor(
                    MathHelper.sin(f) * 32.0F * (float) i + this.world.random.nextInt(5)
            );
            int m = this.world.getTopY(Heightmap.Type.WORLD_SURFACE, k, l);
            mutable.set(k, m, l);

            if (!this.world.isNearOccupiedPointOfInterest(mutable) || proximity >= 2) {
                if (
                        this.world.isRegionLoaded(
                                mutable.getX() - 10,
                                mutable.getZ() - 10,
                                mutable.getX() + 10,
                                mutable.getZ() + 10
                        )
                                && this.world.shouldTickEntity(mutable)
                                && (
                                spawnLocation.isSpawnPositionOk(this.world, mutable, EntityType.RAVAGER)
                                        || this.world.getBlockState(mutable.down()).isOf(Blocks.SNOW)
                                        && this.world.getBlockState(mutable).isAir()
                        )
                ) {
                    return mutable;
                }
            }
        }
        return null;
    }

    // from Minecraft-1.21.1
    @Unique
    private Optional<BlockPos> legacyraid$preCalculateRavagerSpawnLocation(int proximity) {
        for (int i = 0; i < 3; i++) {
            BlockPos blockPos = this.legacyraid$getRavagerSpawnLocation(proximity, 1);
            if (blockPos != null) {
                return Optional.of(blockPos);
            }
        }
        return Optional.empty();
    }
}
