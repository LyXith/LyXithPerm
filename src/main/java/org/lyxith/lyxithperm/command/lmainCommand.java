package org.lyxith.lyxithperm.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.lyxith.lyxithconfig.api.LyXithConfigAPI;

import java.util.Objects;
import java.util.Optional;

import static org.lyxith.lyxithperm.LyxithPerm.*;

public class lmainCommand {
    public static LyXithConfigAPI configAPI = getConfigAPI();
    public static LiteralCommandNode<ServerCommandSource> mainCommand = CommandManager.literal("lyxithperm")
            .executes(context -> {
                configAPI.loadConfig(modId, configName);
                Optional<Integer> permLevel = configAPI.getValue(modId, configName, "permLevel", Integer.class);
                boolean hasPerm = context.getSource().hasPermissionLevel(permLevel.orElse(2));
                if (hasPerm) {
                    context.getSource().sendFeedback(() -> Text.literal("Called /lyxithperm."), false);
                    return 1;
                } else {
                    context.getSource().sendFeedback(()-> Text.literal("You don't have permission"), false);
                    return 0;
                }
            }).build();
    public static LiteralCommandNode<ServerCommandSource> help = LiteralArgumentBuilder.<ServerCommandSource>literal("help")
            .executes(context -> {
                ServerCommandSource executor = context.getSource();
                boolean hasPerm = getPerm(Objects.requireNonNull(executor.getPlayer()), "lyxithperm.help", Boolean.class,false);
                if (hasPerm) {
                    context.getSource().sendFeedback(()-> Text.literal(String.valueOf(configNode.getNode("helpInfo").get().getString())),false);
                    return 1;
                } else {
                    context.getSource().sendFeedback(()-> Text.literal("You don't have permission"), false);
                    return 0;
                }
            }).build();
}
