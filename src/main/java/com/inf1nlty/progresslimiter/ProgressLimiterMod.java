package com.inf1nlty.progresslimiter;

import net.fabricmc.api.ModInitializer;

import java.io.File;

public class ProgressLimiterMod implements ModInitializer {

    public void onInitialize() {

        PLConfig.load(new File("config/progresslimiter.cfg"));
    }
}