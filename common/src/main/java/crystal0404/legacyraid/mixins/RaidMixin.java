package crystal0404.legacyraid.mixins;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.village.raid.Raid;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Raid.class)
public abstract class RaidMixin {
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
}
