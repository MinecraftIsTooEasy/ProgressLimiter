package com.inf1nlty.progresslimiter;

import fi.dy.masa.malilib.config.ConfigTab;
import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.ConfigInteger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PLConfig extends SimpleConfigs {

    public static final ConfigInteger thresholdSeconds = new ConfigInteger(
            "触发阈值(秒)",
            300,
            0,
            43200,
            "当玩家在地下世界且无夜视效果持续超过此秒数后，触发完整效果"
    );

    public static final ConfigInteger effectDurationSeconds = new ConfigInteger(
            "效果持续时间",
            9999999,
            0,
            9999999,
            "DEBUFF效果持续时间 (单位：秒)"
    );

    public static final ConfigInteger yReleaseThreshold = new ConfigInteger(
            "Y 坐标阈值",
            144,
            0,
            512,
            "当玩家 Y 小于该值时，会移除效果并开始计时反向衰减"
    );

    public static final ConfigInteger dangerThresholdSeconds = new ConfigInteger(
            "恐惧触发阈值(秒)",
            60,
            0,
            43200,
            "当玩家在主世界满足恐惧条件并持续超过此秒数后，触发效果"
    );

    public static final ConfigInteger dangerDamageAmount = new ConfigInteger(
            "恐惧伤害量",
            1,
            0,
            20,
            "当恐惧阈值达到时每次造成的伤害"
    );

    public static final ConfigInteger dangerLightThreshold = new ConfigInteger(
            "恐惧光照阈值",
            1,
            1,
            15,
            "当玩家脚下方块的光照值低于此数值时视为危险的黑暗环境"
    );

    private static final PLConfig INSTANCE;

    public static final List<ConfigBase<?>> OverWorldSettings;
    public static final List<ConfigBase<?>> UnderWorldSettings;
    public static final List<ConfigBase<?>> Total;
    public static final List<ConfigTab> tabs;

    private PLConfig(String name, List<ConfigBase<?>> values) {
        super(name, null, values);
    }

    @Override
    public List<ConfigTab> getConfigTabs() {
        return tabs;
    }

    public static PLConfig getInstance() {
        return INSTANCE;
    }

    static {
        UnderWorldSettings = List.of(thresholdSeconds, effectDurationSeconds, yReleaseThreshold);

        OverWorldSettings = List.of(dangerThresholdSeconds, dangerDamageAmount, dangerLightThreshold);

        List<ConfigBase<?>> total = new ArrayList<>();
        total.addAll(UnderWorldSettings);
        total.addAll(OverWorldSettings);
        Total = Collections.unmodifiableList(total);

        tabs = List.of(new ConfigTab("主世界", OverWorldSettings), new ConfigTab("地下世界", UnderWorldSettings));

        INSTANCE = new PLConfig("防内卷", Total);
    }

    public static int getThresholdTicks() {
        return Math.max(0, thresholdSeconds.getIntegerValue() * 20);
    }

    public static int getEffectDurationSeconds() {
        return Math.max(0, effectDurationSeconds.getIntegerValue() * 20);
    }

    public static double getYReleaseThreshold() {
        return yReleaseThreshold.getIntegerValue();
    }

    public static int getDangerThresholdSeconds() {
        return Math.max(0, dangerThresholdSeconds.getIntegerValue() * 20);
    }

    public static int getDangerDamageAmount() {
        return Math.max(0, dangerDamageAmount.getIntegerValue());
    }

    public static int getDangerLightThreshold() {
        return Math.max(0, dangerLightThreshold.getIntegerValue());
    }
}