package org.lyxith.lyxithperm.api;

import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

public class LyXithPermEntryPoint implements PreLaunchEntrypoint {

    public static final LyXithPermAPI API = new LyXithPermAPIImpl();
    @Override
    public void onPreLaunch() {

    }
}
