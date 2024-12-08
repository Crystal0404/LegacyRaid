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

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.entity.effect.BadOmenStatusEffect")
public abstract class BadOmenStatusEffectMixin {
    @Inject(
            method = "applyUpdateEffect",
            at = @At("HEAD"),
            cancellable = true
    )
    private void applyUpdateEffectMixin(
            ServerWorld serverWorld,
            LivingEntity entity,
            int amplifier,
            CallbackInfoReturnable<Boolean> cir
    ) {
        if (entity instanceof ServerPlayerEntity serverPlayerEntity && !serverPlayerEntity.isSpectator()) {
            cir.setReturnValue(this.legacyraid$tryStartRaid(serverPlayerEntity, serverPlayerEntity.getServerWorld()));
        } else {
            cir.setReturnValue(true);
        }
    }

    @Unique
    private boolean legacyraid$tryStartRaid(ServerPlayerEntity player, ServerWorld world) {
        BlockPos pos = player.getBlockPos();
        if (world.getDifficulty() != Difficulty.PEACEFUL && world.isNearOccupiedPointOfInterest(pos)) {
            return world.getRaidManager().startRaid(player, pos) == null;
        }
        return true;
    }
}
