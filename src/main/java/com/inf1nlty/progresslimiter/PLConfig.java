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
            "效果持续时间(秒)",
            9999999,
            0,
            9999999,
            "DEBUFF效果持续时间"
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

    public static final ConfigInteger under50_digSlowdown_amp = new ConfigInteger(
            "50%阶段 挖掘疲劳",
            1,
            0,
            4,
            "达到50%阶段的挖掘疲劳效果的等级, 设置为 0 可禁用，1 表示挖掘疲劳 I"
    );

    public static final ConfigInteger under50_moveSlowdown_amp = new ConfigInteger(
            "50%阶段 缓慢",
            0,
            0,
            4,
            "达到50%阶段的缓慢效果的等级, 设置为 0 可禁用，1 表示缓慢 I"
    );

    public static final ConfigInteger under50_weakness_amp = new ConfigInteger(
            "50%阶段 虚弱",
            0,
            0,
            4,
            "达到50%阶段的虚弱效果的等级, 设置为 0 可禁用，1 表示虚弱 I"
    );

    public static final ConfigInteger under50_confusion_amp = new ConfigInteger(
            "50%阶段 反胃",
            0,
            0,
            4,
            "达到50%阶段的反胃效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger under50_blindness_amp = new ConfigInteger(
            "50%阶段 失明",
            0,
            0,
            4,
            "达到50%阶段的失明效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger under50_hunger_amp = new ConfigInteger(
            "50%阶段 饥饿",
            0,
            0,
            4,
            "达到50%阶段的饥饿效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger under50_poison_amp = new ConfigInteger(
            "50%阶段 中毒",
            0,
            0,
            4,
            "达到50%阶段的中毒效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger under50_wither_amp = new ConfigInteger(
            "50%阶段 凋零",
            0,
            0,
            4,
            "达到50%阶段的凋零效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger underFull_digSlowdown_amp = new ConfigInteger(
            "100%阶段 挖掘疲劳",
            2,
            0,
            4,
            "达到100%阶段的挖掘疲劳效果的等级, 设置为 0 可禁用，1 表示挖掘疲劳 I"
    );

    public static final ConfigInteger underFull_moveSlowdown_amp = new ConfigInteger(
            "100%阶段 缓慢",
            2,
            0,
            4,
            "达到100%阶段的缓慢效果的等级, 设置为 0 可禁用，1 表示缓慢 I"
    );

    public static final ConfigInteger underFull_weakness_amp = new ConfigInteger(
            "100%阶段 虚弱",
            2,
            0,
            4,
            "达到100%阶段的虚弱效果的等级, 设置为 0 可禁用，1 表示虚弱 I"
    );

    public static final ConfigInteger underFull_confusion_amp = new ConfigInteger(
            "100%阶段 反胃",
            0,
            0,
            4,
            "达到100%阶段的反胃效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger underFull_blindness_amp = new ConfigInteger(
            "100%阶段 失明",
            0,
            0,
            4,
            "达到100%阶段的失明效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger underFull_hunger_amp = new ConfigInteger(
            "100%阶段 饥饿",
            0,
            0,
            4,
            "达到100%阶段的饥饿效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger underFull_poison_amp = new ConfigInteger(
            "100%阶段 中毒",
            0,
            0,
            4,
            "达到100%阶段的中毒效果的等级, 设置为 0 可禁用"
    );

    public static final ConfigInteger underFull_wither_amp = new ConfigInteger(
            "100%阶段 凋零",
            0,
            0,
            4,
            "达到100%阶段的凋零效果的等级, 设置为 0 可禁用"
    );

    private static final PLConfig INSTANCE;

    public static final List<ConfigBase<?>> OverWorldSettings;
    public static final List<ConfigBase<?>> Under50Settings;
    public static final List<ConfigBase<?>> UnderFullSettings;
    public static final List<ConfigBase<?>> UnderCommonSettings;
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
        OverWorldSettings = List.of(dangerThresholdSeconds, dangerDamageAmount, dangerLightThreshold);

        UnderCommonSettings = List.of(thresholdSeconds, effectDurationSeconds, yReleaseThreshold);

        Under50Settings = List.of(
                under50_digSlowdown_amp, under50_moveSlowdown_amp, under50_weakness_amp,
                under50_confusion_amp, under50_blindness_amp, under50_hunger_amp,
                under50_poison_amp, under50_wither_amp);

        UnderFullSettings = List.of(
                underFull_digSlowdown_amp, underFull_moveSlowdown_amp, underFull_weakness_amp,
                underFull_confusion_amp, underFull_blindness_amp, underFull_hunger_amp,
                underFull_poison_amp, underFull_wither_amp);

        List<ConfigBase<?>> total = new ArrayList<>();
        total.addAll(OverWorldSettings);
        total.addAll(UnderCommonSettings);
        total.addAll(Under50Settings);
        total.addAll(UnderFullSettings);
        Total = Collections.unmodifiableList(total);

        List<ConfigTab> t = new ArrayList<>();
        t.add(new ConfigTab("主世界", OverWorldSettings));
        t.add(new ConfigTab("地下世界", UnderCommonSettings));
        t.add(new ConfigTab("地下世界 - 50%阶段", Under50Settings));
        t.add(new ConfigTab("地下世界 - 100%阶段", UnderFullSettings));
        tabs = Collections.unmodifiableList(t);

        INSTANCE = new PLConfig("Progress Limiter", Total);
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

    public static int getUnder50_digSlowdown_amp() {
        return Math.max(0, under50_digSlowdown_amp.getIntegerValue());
    }
    public static int getUnder50_moveSlowdown_amp() {
        return Math.max(0, under50_moveSlowdown_amp.getIntegerValue());
    }
    public static int getUnder50_weakness_amp() {
        return Math.max(0, under50_weakness_amp.getIntegerValue());
    }
    public static int getUnder50_confusion_amp() {
        return Math.max(0, under50_confusion_amp.getIntegerValue());
    }
    public static int getUnder50_blindness_amp() {
        return Math.max(0, under50_blindness_amp.getIntegerValue());
    }
    public static int getUnder50_hunger_amp() {
        return Math.max(0, under50_hunger_amp.getIntegerValue());
    }
    public static int getUnder50_poison_amp() {
        return Math.max(0, under50_poison_amp.getIntegerValue());
    }
    public static int getUnder50_wither_amp() {
        return Math.max(0, under50_wither_amp.getIntegerValue());
    }

    public static int getUnderFull_digSlowdown_amp() {
        return Math.max(0, underFull_digSlowdown_amp.getIntegerValue());
    }
    public static int getUnderFull_moveSlowdown_amp() {
        return Math.max(0, underFull_moveSlowdown_amp.getIntegerValue());
    }
    public static int getUnderFull_weakness_amp() {
        return Math.max(0, underFull_weakness_amp.getIntegerValue());
    }
    public static int getUnderFull_confusion_amp() {
        return Math.max(0, underFull_confusion_amp.getIntegerValue());
    }
    public static int getUnderFull_blindness_amp() {
        return Math.max(0, underFull_blindness_amp.getIntegerValue());
    }
    public static int getUnderFull_hunger_amp() {
        return Math.max(0, underFull_hunger_amp.getIntegerValue());
    }
    public static int getUnderFull_poison_amp() {
        return Math.max(0, underFull_poison_amp.getIntegerValue());
    }
    public static int getUnderFull_wither_amp() {
        return Math.max(0, underFull_wither_amp.getIntegerValue());
    }
}