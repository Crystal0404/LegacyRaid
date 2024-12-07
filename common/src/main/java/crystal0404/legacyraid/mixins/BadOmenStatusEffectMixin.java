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
