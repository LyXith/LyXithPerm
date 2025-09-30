package org.lyxith.lyxithperm;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.ServerCommandSource;
import org.lyxith.lyxithconfig.api.LyXithConfigAPI;
import org.lyxith.lyxithconfig.api.LyXithConfigNode;
import org.lyxith.lyxithconfig.api.LyXithConfigNodeImpl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lyxith.lyxithperm.command.lmainCommand.*;

public class LyxithPerm implements ModInitializer {
    public static LyXithConfigNodeImpl configNode = new LyXithConfigNodeImpl();
    private static final String defaultHelpInfo = "test help information";
    public static final String modId = "LyXithPerm";
    public static String configName = "Permission";
    private static LyXithConfigAPI configAPI;
    @Override
    public void onInitialize() {
        List<LyXithConfigAPI> apiInstances = FabricLoader.getInstance()
                .getEntrypoints("lyxithconfig-api", LyXithConfigAPI.class);

        if (apiInstances.isEmpty()) {
            System.err.println("LyXithConfig API 入口点未找到，可能是版本不兼容");
        } else if (apiInstances.size() == 1) {
            configAPI = apiInstances.getFirst();
        }
        initConfig();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            commandRegister(dispatcher,mainCommand);
            commandRegister(dispatcher,mainCommandAlias);
        });
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {

            var player = handler.player;

            String username = player.getName().getString();

            configAPI.loadConfig(modId,configName);
            configNode.addNode("players." + username + ".group", false);
            configNode.addNode("players." + username + ".perms", false);
            configNode.getNode("players." + username + ".group").get().setValue("default");
            configAPI.saveConfig(modId, configName, configNode);
        });
    }
    private void initConfig() {
        if (!configAPI.modConfigDirExist(modId)) {
            configAPI.createModConfigDir(modId);
        }
        if (!configAPI.modConfigExist(modId, configName)) {
            configAPI.createModConfig(modId, configName);
        }
        configAPI.loadConfig(modId, configName);
        configNode = configAPI.getConfigRootNode(modId,configName).getRoot();
        configNode.addNode("permLevel", false);
        configNode.addNode("players", false);
        configNode.initNode("groups.default", false, 0);
        configNode.initNode("helpInfo", false, defaultHelpInfo);
        configAPI.saveConfig(modId, configName, configNode);
    }
    public static LyXithConfigAPI getConfigAPI() {
        return configAPI;
    }
    public static  <T> T getPerm(PlayerEntity player, String perm, Class<T> type, T defaultValue) {
        String playerName = player.getName().getString();
        return configNode.getNode("players." + playerName + ".perms." + perm)
                .flatMap(node -> node.getValue(type))
                .orElse(defaultValue);
    }
    private static List<Object> getGroupList(String group) {
        LyXithConfigNode groupNode = configNode.getNode("groups."+group).get();
        int groupLength = groupNode.length();
        List<Object> list = new ArrayList<>();
        for (int x = 0; x < groupLength; x++) {
            list.add(groupNode.getElement(x));
        }
        return list;
    }

    public static boolean getGroupPerm(String group,String perm) {
        return getGroupList(group).contains(perm);
    }

    public static boolean isInGroup(PlayerEntity player, String group) {
        String playerName = player.getName().getString();
        LyXithConfigNode groupNode = configNode.getNode("players."+playerName+".group").get();
        boolean inGroup = false;
        for (int x = 0; x < groupNode.length(); x++) {
            if (Objects.equals(groupNode.getElement(x).toString(), group)) inGroup = true;
        }
        return inGroup;
    }

    private void commandRegister(CommandDispatcher<ServerCommandSource> dispatcher, LiteralCommandNode<ServerCommandSource> command) {
        command.addChild(help);
        dispatcher.getRoot().addChild(command);
    }

    public static void setPerm(PlayerEntity player, String perm, Object object) {
        String playerName = player.getName().getString();
        configNode.getNode("players." + playerName + ".perms." + perm).get().setValue(object);
    }
}
