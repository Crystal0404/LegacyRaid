/*
 * This file is part of the LegacyRaid project, licensed under the
 * GNU Lesser General Public License v3.0
 *
 * Copyright (C) 2024  Crystal0404 and contributors
 *
 * LegacyRaid is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
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
import net.minecraft.entity.raid.RaiderEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RaiderEntity.class)
public abstract class RaiderEntityMixin {
    @ModifyExpressionValue(
            method = "onDeath",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/resource/featuretoggle/FeatureSet;" +
                            "contains(Lnet/minecraft/resource/featuretoggle/FeatureFlag;)Z"
            )
    )
    private boolean onDeathMixin(boolean original) {
        return false;
    }
}
