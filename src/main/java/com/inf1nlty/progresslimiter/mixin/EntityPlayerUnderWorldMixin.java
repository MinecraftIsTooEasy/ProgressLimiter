package com.inf1nlty.progresslimiter.mixin;

import com.inf1nlty.progresslimiter.PLConfig;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerUnderWorldMixin {

    @Unique private int progLimiter_timerTicks = 0;
    @Unique private boolean progLimiter_warn50 = false;
    @Unique private boolean progLimiter_warn80 = false;

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void pl_onUpdate(CallbackInfo ci) {
        runTickLogic();
    }

    @Unique
    private void runTickLogic()
    {
        Entity selfEntity = (Entity) (Object) this;

        if (selfEntity == null) return;

        World world = selfEntity.worldObj;

        if (world == null) return;

        if (world.isRemote) return;

        boolean inUnderworld = selfEntity.isInUnderworld();

        double posY = selfEntity.posY;

        EntityLivingBase selfLiving = (EntityLivingBase) selfEntity;

        boolean hasNV = selfLiving.isPotionActive(Potion.nightVision.id);

        int thresholdTicks = PLConfig.getThresholdTicks();

        int effectDuration = PLConfig.getEffectDurationSeconds();

        double yRelease = PLConfig.getYReleaseThreshold();

        if (!inUnderworld)
        {
            if (this.progLimiter_timerTicks > 0) this.progLimiter_timerTicks = Math.max(0, this.progLimiter_timerTicks - 1);
            removeAllEffects(selfLiving);
            resetWarningsIfNeeded(thresholdTicks);
            return;
        }

        if (hasNV)
        {
            if (this.progLimiter_timerTicks > 0) this.progLimiter_timerTicks = Math.max(0, this.progLimiter_timerTicks - 1);
            removeAllEffects(selfLiving);
            resetWarningsIfNeeded(thresholdTicks);
            return;
        }

        if (posY < yRelease)
        {
            removeAllEffects(selfLiving);
            if (this.progLimiter_timerTicks > 0) this.progLimiter_timerTicks = Math.max(0, this.progLimiter_timerTicks - 1);
            resetWarningsIfNeeded(thresholdTicks);
            return;
        }

        if (thresholdTicks <= 0)
        {
            this.progLimiter_timerTicks = 0;
            this.progLimiter_warn50 = false;
            this.progLimiter_warn80 = false;
            removeAllEffects(selfLiving);
            return;
        }
        else
        {
            this.progLimiter_timerTicks = Math.min(this.progLimiter_timerTicks + 1, thresholdTicks);
        }

        if (this.progLimiter_timerTicks >= thresholdTicks)
        {
            this.progLimiter_warn50 = true;
            this.progLimiter_warn80 = true;

            int ampVal;
            int amp;

            ampVal = PLConfig.getUnderFull_digSlowdown_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.digSlowdown.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.digSlowdown.id);

            ampVal = PLConfig.getUnderFull_moveSlowdown_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.moveSlowdown.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.moveSlowdown.id);

            ampVal = PLConfig.getUnderFull_weakness_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.weakness.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.weakness.id);

            ampVal = PLConfig.getUnderFull_confusion_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.confusion.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.confusion.id);

            ampVal = PLConfig.getUnderFull_blindness_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.blindness.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.blindness.id);

            ampVal = PLConfig.getUnderFull_hunger_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.hunger.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.hunger.id);

            ampVal = PLConfig.getUnderFull_poison_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.poison.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.poison.id);

            ampVal = PLConfig.getUnderFull_wither_amp();

            if (ampVal > 0)
            {
                amp = ampVal - 1; applyEffectIfNeeded(selfLiving, Potion.wither.id, effectDuration, amp);
            }

            else removeEffectIfNeeded(selfLiving, Potion.wither.id);

            return;
        }

        double pct = (this.progLimiter_timerTicks / (double) thresholdTicks) * 100.0;

        if (pct >= 80.0)
        {
            if (!this.progLimiter_warn80)
            {
                this.progLimiter_warn80 = true;
                if (selfEntity instanceof EntityPlayer)
                {
                    sendPlayerMessage((EntityPlayer) selfEntity, "你快要坚持不住了！");
                }
            }
            return;
        }

        if (pct >= 50.0)
        {
            if (!this.progLimiter_warn50)
            {
                this.progLimiter_warn50 = true;
                if (selfEntity instanceof EntityPlayer)
                {
                    sendPlayerMessage((EntityPlayer) selfEntity, "由于对黑暗的恐惧，你感觉有一点疲劳");
                }
            }

            int ampVal50;
            int amp50;

            ampVal50 = PLConfig.getUnder50_digSlowdown_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.digSlowdown.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.digSlowdown.id);

            ampVal50 = PLConfig.getUnder50_moveSlowdown_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.moveSlowdown.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.moveSlowdown.id);

            ampVal50 = PLConfig.getUnder50_weakness_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.weakness.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.weakness.id);

            ampVal50 = PLConfig.getUnder50_confusion_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.confusion.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.confusion.id);

            ampVal50 = PLConfig.getUnder50_blindness_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.blindness.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.blindness.id);

            ampVal50 = PLConfig.getUnder50_hunger_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.hunger.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.hunger.id);

            ampVal50 = PLConfig.getUnder50_poison_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.poison.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.poison.id);

            ampVal50 = PLConfig.getUnder50_wither_amp();

            if (ampVal50 > 0)
            {
                amp50 = ampVal50 - 1; applyEffectIfNeeded(selfLiving, Potion.wither.id, effectDuration, amp50);
            }

            else removeEffectIfNeeded(selfLiving, Potion.wither.id);

            return;
        }

        removeAllEffects(selfLiving);
        resetWarningsIfNeeded(thresholdTicks);
    }

    @Unique
    private void applyEffectIfNeeded(EntityLivingBase entity, int potionId, int duration, int amplifier)
    {
        if (potionId < 0 || potionId >= Potion.potionTypes.length) return;

        Potion potion = Potion.potionTypes[potionId];

        if (potion == null) return;

        if (!entity.isPotionActive(potionId))
        {
            entity.addPotionEffect(new PotionEffect(potionId, duration, amplifier));
            return;
        }

        PotionEffect current = entity.getActivePotionEffect(potion);

        if (current == null) return;

        if (current.getAmplifier() != amplifier)
        {
            entity.removePotionEffect(potionId);
            entity.addPotionEffect(new PotionEffect(potionId, duration, amplifier));
        }
    }

    @Unique
    private void removeEffectIfNeeded(EntityLivingBase entity, int potionId)
    {
        if (potionId < 0 || potionId >= Potion.potionTypes.length) return;
        entity.removePotionEffect(potionId);
    }

    @Unique
    private void removeAllEffects(EntityLivingBase entity)
    {
        removeEffectIfNeeded(entity, Potion.digSlowdown.id);
        removeEffectIfNeeded(entity, Potion.moveSlowdown.id);
        removeEffectIfNeeded(entity, Potion.weakness.id);
        removeEffectIfNeeded(entity, Potion.confusion.id);
        removeEffectIfNeeded(entity, Potion.blindness.id);
        removeEffectIfNeeded(entity, Potion.hunger.id);
        removeEffectIfNeeded(entity, Potion.poison.id);
        removeEffectIfNeeded(entity, Potion.wither.id);
    }

    @Unique
    private void resetWarningsIfNeeded(int thresholdTicks)
    {
        int ticks50 = (int) Math.ceil(thresholdTicks * 0.50);
        int ticks80 = (int) Math.ceil(thresholdTicks * 0.80);

        if (this.progLimiter_timerTicks < ticks50 && this.progLimiter_warn50) this.progLimiter_warn50 = false;

        if (this.progLimiter_timerTicks < ticks80 && this.progLimiter_warn80) this.progLimiter_warn80 = false;

        if (this.progLimiter_timerTicks < ticks50)
        {
            Entity selfEntity = (Entity) (Object) this;
            if (selfEntity != null)
            {
                removeAllEffects((EntityLivingBase) selfEntity);
            }
        }
    }

    @Unique
    private void sendPlayerMessage(EntityPlayer player, String text)
    {
        player.addChatMessage(text);
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void pl_writeNBT(NBTTagCompound nbt, CallbackInfo ci)
    {
        nbt.setInteger("ProgressLimiter_TimerTicks", this.progLimiter_timerTicks);
        nbt.setBoolean("ProgressLimiter_Warn50", this.progLimiter_warn50);
        nbt.setBoolean("ProgressLimiter_Warn80", this.progLimiter_warn80);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void pl_readNBT(NBTTagCompound nbt, CallbackInfo ci)
    {
        if (nbt.hasKey("ProgressLimiter_TimerTicks")) this.progLimiter_timerTicks = nbt.getInteger("ProgressLimiter_TimerTicks");

        else this.progLimiter_timerTicks = 0;

        if (nbt.hasKey("ProgressLimiter_Warn50")) this.progLimiter_warn50 = nbt.getBoolean("ProgressLimiter_Warn50");

        else this.progLimiter_warn50 = false;

        if (nbt.hasKey("ProgressLimiter_Warn80")) this.progLimiter_warn80 = nbt.getBoolean("ProgressLimiter_Warn80");

        else this.progLimiter_warn80 = false;
    }
}