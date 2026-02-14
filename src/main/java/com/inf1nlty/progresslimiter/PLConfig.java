package com.inf1nlty.progresslimiter;

import java.io.*;
import java.nio.file.Files;
import java.util.Properties;

public final class PLConfig {

    private static final String DEFAULT_FILENAME = "config/progresslimiter.cfg";

    // defaults
    public static int thresholdSeconds = 300;
    public static int effectDurationTicks = 9999999;
    public static double yReleaseThreshold = 144.0D;

    private static File configFile = new File(DEFAULT_FILENAME);
    private static long lastModified = 0L;

    private PLConfig() {}

    public static synchronized void load(File file) {
        if (file == null) file = new File(DEFAULT_FILENAME);
        configFile = file;

        if (configFile.getParentFile() != null && !configFile.getParentFile().exists()) {
            configFile.getParentFile().mkdirs();
        }

        Properties props = new Properties();
        if (configFile.exists()) {
            try (InputStream in = new FileInputStream(configFile)) {
                props.load(in);
            } catch (IOException e) {
                System.err.println("[ProgressLimiterConfig] Failed to read config, using defaults: " + e.getMessage());
            }
        }

        thresholdSeconds = parseInt(props.getProperty("thresholdSeconds"), thresholdSeconds);
        effectDurationTicks = parseInt(props.getProperty("effectDurationTicks"), effectDurationTicks);
        yReleaseThreshold = parseDouble(props.getProperty("yReleaseThreshold"), yReleaseThreshold);

        try {
            StringBuilder sb = new StringBuilder();
            sb.append("# thresholdSeconds: 达到此秒数（玩家在地下且无夜视）后触发 100%程度 的效果\n");
            sb.append("thresholdSeconds=").append(thresholdSeconds).append('\n');
            sb.append("\n");
            sb.append("# effectDurationTicks: DEBUFF效果持续时间（单位：tick）\n");
            sb.append("effectDurationTicks=").append(effectDurationTicks).append('\n');
            sb.append("\n");
            sb.append("# yReleaseThreshold: 当玩家 Y < 该值时，会移除效果并开始计时反向衰减\n");
            sb.append("yReleaseThreshold=").append(yReleaseThreshold);

            Files.writeString(configFile.toPath(), sb.toString());
            lastModified = configFile.lastModified();
        } catch (IOException e) {
            System.err.println("[ProgressLimiterConfig] Failed to write config: " + e.getMessage());
        }
    }

    private static int parseInt(String s, int fallback) {
        if (s == null) return fallback;
        try { return Integer.parseInt(s.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    private static double parseDouble(String s, double fallback) {
        if (s == null) return fallback;
        try { return Double.parseDouble(s.trim()); }
        catch (NumberFormatException e) { return fallback; }
    }

    public static int getThresholdTicks() {
        // auto-reload if file changed on disk
        if (configFile != null && configFile.exists()) {
            long m = configFile.lastModified();
            if (m != lastModified) {
                load(configFile);
            }
        }
        return Math.max(0, thresholdSeconds * 20);
    }

    public static int getEffectDurationTicks() {
        if (configFile != null && configFile.exists()) {
            long m = configFile.lastModified();
            if (m != lastModified) load(configFile);
        }
        return effectDurationTicks;
    }

    public static double getYReleaseThreshold() {
        if (configFile != null && configFile.exists()) {
            long m = configFile.lastModified();
            if (m != lastModified) load(configFile);
        }
        return yReleaseThreshold;
    }
}