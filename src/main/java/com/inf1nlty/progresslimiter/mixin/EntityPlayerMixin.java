package com.inf1nlty.progresslimiter.mixin;

import com.inf1nlty.progresslimiter.PLConfig;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerMixin {

    @Unique private int progLimiter_timerTicks = 0;
    @Unique private boolean progLimiter_warn50 = false;
    @Unique private boolean progLimiter_warn80 = false;

    // NOTE: THRESHOLD_SECONDS removed; use ProgressLimiterConfig.getThresholdTicks()
    @Unique private static final int EFFECT_DURATION_FALLBACK = 9999999;

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void pl_onUpdate(CallbackInfo ci) {
        runTickLogic();
    }

    @Unique
    private void runTickLogic() {
        Entity selfEntity = (Entity) (Object) this;
        if (selfEntity == null) return;

        World world = selfEntity.worldObj;
        if (world == null) return;
        if (world.isRemote) return;

        boolean inUnderworld = selfEntity.isInUnderworld();
        double posY = selfEntity.posY;
        EntityLivingBase selfLiving = (EntityLivingBase) selfEntity;
        boolean hasNV = selfLiving.isPotionActive(Potion.nightVision.id);

        // current runtime-configured thresholds (use getters so methods are used)
        int thresholdTicks = PLConfig.getThresholdTicks();
        int effectDuration = PLConfig.getEffectDurationTicks() > 0 ? PLConfig.getEffectDurationTicks() : EFFECT_DURATION_FALLBACK;
        double yRelease = PLConfig.getYReleaseThreshold();

        if (!inUnderworld) {
            if (this.progLimiter_timerTicks > 0) this.progLimiter_timerTicks = Math.max(0, this.progLimiter_timerTicks - 1);
            removeAllEffects(selfLiving);
            resetWarningsIfNeeded(thresholdTicks);
            return;
        }

        if (hasNV) {
            if (this.progLimiter_timerTicks > 0) this.progLimiter_timerTicks = Math.max(0, this.progLimiter_timerTicks - 1);
            removeAllEffects(selfLiving);
            resetWarningsIfNeeded(thresholdTicks);
            return;
        }

        if (posY < yRelease) {
            removeAllEffects(selfLiving);
            if (this.progLimiter_timerTicks > 0) this.progLimiter_timerTicks = Math.max(0, this.progLimiter_timerTicks - 1);
            resetWarningsIfNeeded(thresholdTicks);
            return;
        }

        if (this.progLimiter_timerTicks < thresholdTicks) {
            this.progLimiter_timerTicks++;
        }

        if (this.progLimiter_timerTicks >= thresholdTicks) {
            this.progLimiter_warn50 = true;
            this.progLimiter_warn80 = true;
            applyEffectIfNeeded(selfLiving, Potion.digSlowdown.id, effectDuration, 1);
            applyEffectIfNeeded(selfLiving, Potion.moveSlowdown.id, effectDuration, 1);
            applyEffectIfNeeded(selfLiving, Potion.weakness.id, effectDuration, 1);
            return;
        }

        double pct = (this.progLimiter_timerTicks / (double) thresholdTicks) * 100.0;

        if (pct >= 80.0) {
            if (!this.progLimiter_warn80) {
                this.progLimiter_warn80 = true;
                if (selfEntity instanceof EntityPlayer) {
                    sendPlayerMessage((EntityPlayer) selfEntity, "你快要坚持不住了！");
                }
            }
            return;
        }

        if (pct >= 50.0) {
            if (!this.progLimiter_warn50) {
                this.progLimiter_warn50 = true;
                if (selfEntity instanceof EntityPlayer) {
                    sendPlayerMessage((EntityPlayer) selfEntity, "由于对黑暗的恐惧，你感觉有一点疲劳");
                }
            }
            applyEffectIfNeeded(selfLiving, Potion.digSlowdown.id, effectDuration, 0);
            removeEffectIfNeeded(selfLiving, Potion.moveSlowdown.id);
            removeEffectIfNeeded(selfLiving, Potion.weakness.id);
            return;
        }

        removeAllEffects(selfLiving);
        resetWarningsIfNeeded(thresholdTicks);
    }

    @Unique
    private void applyEffectIfNeeded(EntityLivingBase entity, int potionId, int duration, int amplifier) {
        if (potionId < 0 || potionId >= Potion.potionTypes.length) return;
        Potion p = Potion.potionTypes[potionId];
        if (p == null) return;

        if (!entity.isPotionActive(potionId)) {
            entity.addPotionEffect(new PotionEffect(potionId, duration, amplifier));
            return;
        }
        PotionEffect current = entity.getActivePotionEffect(p);
        if (current == null) return;
        if (current.getAmplifier() != amplifier) {
            entity.removePotionEffect(potionId);
            entity.addPotionEffect(new PotionEffect(potionId, duration, amplifier));
        }
    }

    @Unique
    private void removeEffectIfNeeded(EntityLivingBase entity, int potionId) {
        if (potionId < 0 || potionId >= Potion.potionTypes.length) return;
        entity.removePotionEffect(potionId);
    }

    @Unique
    private void removeAllEffects(EntityLivingBase entity) {
        removeEffectIfNeeded(entity, Potion.digSlowdown.id);
        removeEffectIfNeeded(entity, Potion.moveSlowdown.id);
        removeEffectIfNeeded(entity, Potion.weakness.id);
    }

    @Unique
    private void resetWarningsIfNeeded(int thresholdTicks) {
        int ticks50 = (int) Math.ceil(thresholdTicks * 0.50);
        int ticks80 = (int) Math.ceil(thresholdTicks * 0.80);
        if (this.progLimiter_timerTicks < ticks50 && this.progLimiter_warn50) this.progLimiter_warn50 = false;
        if (this.progLimiter_timerTicks < ticks80 && this.progLimiter_warn80) this.progLimiter_warn80 = false;
        if (this.progLimiter_timerTicks < ticks50) {
            Entity selfEntity = (Entity) (Object) this;
            if (selfEntity != null) {
                removeAllEffects((EntityLivingBase) selfEntity);
            }
        }
    }

    @Unique
    private void sendPlayerMessage(EntityPlayer player, String text) {
        player.addChatMessage(text);
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void pl_writeNBT(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setInteger("ProgressLimiter_TimerTicks", this.progLimiter_timerTicks);
        nbt.setBoolean("ProgressLimiter_Warn50", this.progLimiter_warn50);
        nbt.setBoolean("ProgressLimiter_Warn80", this.progLimiter_warn80);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void pl_readNBT(NBTTagCompound nbt, CallbackInfo ci) {
        if (nbt.hasKey("ProgressLimiter_TimerTicks")) this.progLimiter_timerTicks = nbt.getInteger("ProgressLimiter_TimerTicks");
        else this.progLimiter_timerTicks = 0;
        if (nbt.hasKey("ProgressLimiter_Warn50")) this.progLimiter_warn50 = nbt.getBoolean("ProgressLimiter_Warn50");
        else this.progLimiter_warn50 = false;
        if (nbt.hasKey("ProgressLimiter_Warn80")) this.progLimiter_warn80 = nbt.getBoolean("ProgressLimiter_Warn80");
        else this.progLimiter_warn80 = false;
    }
}