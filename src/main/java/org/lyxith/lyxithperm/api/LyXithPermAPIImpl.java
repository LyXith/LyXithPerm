package org.lyxith.lyxithperm.api;

import org.lyxith.lyxithconfig.api.LyXithConfigAPI;

import static org.lyxith.lyxithperm.LyxithPerm.getConfigAPI;
import static org.lyxith.lyxithperm.LyxithPerm.modId;

public class LyXithPermAPIImpl implements LyXithPermAPI{
    LyXithConfigAPI configAPI = getConfigAPI();
    @Override
    public void createPermFile(String permFile) {
        configAPI.createModConfig(modId,permFile);
    }
}
