package com.inf1nlty.progresslimiter.mixin;

import com.inf1nlty.progresslimiter.PLConfig;
import net.minecraft.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class EntityPlayerOverWorldMixin {

    @Unique private int danger_timerTicks = 0;
    @Unique private int danger_overTicks = 0;
    @Unique private boolean danger_warn50 = false;
    @Unique private boolean danger_warn80 = false;

    @Inject(method = "onUpdate", at = @At("TAIL"))
    private void pl_dangerOnUpdate(CallbackInfo ci) {
        processDanger();
    }

    @Unique
    private void processDanger() {

        Entity selfEntity = (Entity) (Object) this;

        if (selfEntity == null) return;

        World world = selfEntity.worldObj;

        if (world == null) return;

        if (world.isRemote) return;

        if (selfEntity.isInUnderworld() || selfEntity.isInNether())
        {
            this.danger_timerTicks = Math.max(0, this.danger_timerTicks - 1);

            if (this.danger_overTicks > 0) this.danger_overTicks = Math.max(0, this.danger_overTicks - 1);
            return;
        }

        EntityPlayer player = (EntityPlayer) selfEntity;

        EntityLivingBase selfLiving = (EntityLivingBase) selfEntity;

        if (player == null || selfLiving == null) return;

        boolean hasNV = selfLiving.isPotionActive(Potion.nightVision.id);

        double posY = selfEntity.posY;

        int x = MathHelper.floor_double(selfEntity.posX);
        int y = MathHelper.floor_double(selfEntity.posY);
        int z = MathHelper.floor_double(selfEntity.posZ);

        int light = world.getFullBlockLightValue(x, y, z);

        boolean darkBelow = light < PLConfig.getDangerLightThreshold();

        boolean lowYNoNV = (posY < 50.0) && (!hasNV);

        boolean danger = lowYNoNV && darkBelow;

        int thresholdTicks = PLConfig.getDangerThresholdSeconds();

        if (danger)
        {
            if (this.danger_timerTicks < thresholdTicks)
            {
                this.danger_timerTicks++;
            }
            else
            {
                this.danger_overTicks++;
            }
        }
        else
        {
            if (this.danger_overTicks > 0) this.danger_overTicks = Math.max(0, this.danger_overTicks - 1);
            this.danger_timerTicks = Math.max(0, this.danger_timerTicks - 1);
        }

        if (thresholdTicks > 0) {
            double pct = (this.danger_timerTicks / (double) thresholdTicks) * 100.0;

            if (pct >= 80.0)
            {
                if (!this.danger_warn80)
                {
                    this.danger_warn80 = true;
                    player.addChatMessage("你感觉有东西马上要跳出来吃了你");
                }
            }
            else if (pct >= 50.0)
            {
                if (!this.danger_warn50)
                {
                    this.danger_warn50 = true;
                    player.addChatMessage("你感觉有脏东西在盯着你");
                }
            }
            else
            {
                if (this.danger_warn50) this.danger_warn50 = false;
                if (this.danger_warn80) this.danger_warn80 = false;
            }
        }

        if (danger && this.danger_timerTicks >= thresholdTicks)
        {
            boolean firstHit = (this.danger_timerTicks == thresholdTicks && this.danger_overTicks == 0);
            boolean periodicHit = (this.danger_overTicks > 0 && (this.danger_overTicks % 100 == 0));

            if (firstHit || periodicHit)
            {
                {
                    float dmgAmount = (float) PLConfig.getDangerDamageAmount();

                    if (dmgAmount <= 0.0F) dmgAmount = 1.0F;

                    Damage damage = new Damage(DamageSource.generic, dmgAmount);

                    player.attackEntityFrom(damage);

                    float pitch = 0.85F + player.worldObj.rand.nextFloat() * 0.3F; // 0.85 - 1.15
                    world.playSoundAtEntity(player, "mob.wolf.growl", 1.0F, pitch);
                    this.danger_overTicks = 0;
                }
            }
        }
    }

    @Inject(method = "writeEntityToNBT", at = @At("TAIL"))
    private void pl_writeDangerNBT(NBTTagCompound nbt, CallbackInfo ci) {
        nbt.setInteger("Danger_TimerTicks", this.danger_timerTicks);
        nbt.setInteger("Danger_OverTicks", this.danger_overTicks);
        nbt.setBoolean("Danger_Warn50", this.danger_warn50);
        nbt.setBoolean("Danger_Warn80", this.danger_warn80);
    }

    @Inject(method = "readEntityFromNBT", at = @At("TAIL"))
    private void pl_readDangerNBT(NBTTagCompound nbt, CallbackInfo ci) {

        if (nbt.hasKey("Danger_TimerTicks")) this.danger_timerTicks = nbt.getInteger("Danger_TimerTicks");
        else this.danger_timerTicks = 0;

        if (nbt.hasKey("Danger_OverTicks")) this.danger_overTicks = nbt.getInteger("Danger_OverTicks");
        else this.danger_overTicks = 0;

        if (nbt.hasKey("Danger_Warn50")) this.danger_warn50 = nbt.getBoolean("Danger_Warn50");

        else this.danger_warn50 = false;

        if (nbt.hasKey("Danger_Warn80")) this.danger_warn80 = nbt.getBoolean("Danger_Warn80");

        else this.danger_warn80 = false;
    }
}