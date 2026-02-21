package com.inf1nlty.progresslimiter;

import fi.dy.masa.malilib.config.ConfigManager;
import net.fabricmc.api.ModInitializer;
import net.xiaoyu233.fml.ModResourceManager;

public class ProgressLimiterMod implements ModInitializer {

    public static final String NAMESPACE = "progresslimiter";

    public void onInitialize() {

        ModResourceManager.addResourcePackDomain(NAMESPACE);

        PLConfig.getInstance().load();
        ConfigManager.getInstance().registerConfig(PLConfig.getInstance());
    }
}