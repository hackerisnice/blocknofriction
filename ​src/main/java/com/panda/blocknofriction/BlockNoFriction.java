package com.panda.blocknofriction;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockNoFriction implements ModInitializer {
    public static final String MOD_ID = "blocknofriction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    // 全局状态开关，默认关闭
    public static boolean isNoFrictionEnabled = false;

    @Override
    public void onInitialize() {
        LOGGER.info("Zero Friction Physics Initialized! Waiting for command.");

        // 注册控制指令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            
            // 注册 /startnofriction 指令
            dispatcher.register(CommandManager.literal("startnofriction")
                    .requires(source -> source.hasPermissionLevel(2)) // 需要管理员(OP)权限
                    .executes(context -> {
                        isNoFrictionEnabled = true;
                        context.getSource().sendMessage(Text.literal("§a[BlockNoFriction] 零摩擦与反冲力模式 已开启！"));
                        return 1;
                    }));

            // 注册 /stopnofriction 指令 (方便你随时关闭)
            dispatcher.register(CommandManager.literal("stopnofriction")
                    .requires(source -> source.hasPermissionLevel(2))
                    .executes(context -> {
                        isNoFrictionEnabled = false;
                        context.getSource().sendMessage(Text.literal("§c[BlockNoFriction] 零摩擦与反冲力模式 已关闭。"));
                        return 1;
                    }));
        });
    }
}
