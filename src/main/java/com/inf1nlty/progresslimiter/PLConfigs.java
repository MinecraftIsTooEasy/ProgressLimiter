package com.inf1nlty.progresslimiter;

import fi.dy.masa.malilib.config.ConfigTab;
import fi.dy.masa.malilib.config.SimpleConfigs;
import fi.dy.masa.malilib.config.options.ConfigBase;
import fi.dy.masa.malilib.config.options.ConfigInteger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PLConfigs extends SimpleConfigs {

    public static final ConfigInteger thresholdSeconds = new ConfigInteger(
            "触发阈值",
            300,
            0,
            72000,
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

    private static final PLConfigs INSTANCE;

    public static final List<ConfigBase<?>> UnderWorldSettings;
    public static final List<ConfigBase<?>> Total;
    public static final List<ConfigTab> tabs;

    private PLConfigs(String name, List<ConfigBase<?>> values) {
        super(name, null, values);
    }

    @Override
    public List<ConfigTab> getConfigTabs() {
        return tabs;
    }

    public static PLConfigs getInstance() {
        return INSTANCE;
    }

    static {
        UnderWorldSettings = List.of(thresholdSeconds, effectDurationSeconds, yReleaseThreshold);

        List<ConfigBase<?>> total = new ArrayList<>(UnderWorldSettings);
        Total = Collections.unmodifiableList(total);

        tabs = List.of(new ConfigTab("地下世界", UnderWorldSettings));

        INSTANCE = new PLConfigs("防内卷", Total);
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
}