package com.panda.blocknofriction;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BlockNoFriction implements ModInitializer {
    public static final String MOD_ID = "blocknofriction";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static boolean isNoFrictionEnabled = false;

    @Override
    public void onInitialize() {
        // 1. 注册网络数据包类型
        PayloadTypeRegistry.playS2C().register(NoFrictionPayload.ID, NoFrictionPayload.CODEC);

        // 2. 玩家进入存档/服务器时，自动同步当前状态
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayNetworking.send(handler.getPlayer(), new NoFrictionPayload(isNoFrictionEnabled));
        });

        // 3. 注册指令
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            
            dispatcher.register(CommandManager.literal("startnofriction")
                    .executes(context -> {
                        isNoFrictionEnabled = true;
                        context.getSource().sendMessage(Text.literal("§a[BlockNoFriction] 零摩擦与反冲力模式 已开启！"));
                        
                        // 【核心】将开启状态广播给所有在线客户端
                        for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                            ServerPlayNetworking.send(player, new NoFrictionPayload(true));
                        }
                        return 1;
                    }));

            dispatcher.register(CommandManager.literal("stopnofriction")
                    .executes(context -> {
                        isNoFrictionEnabled = false;
                        context.getSource().sendMessage(Text.literal("§c[BlockNoFriction] 零摩擦与反冲力模式 已关闭。"));
                        
                        // 【核心】将关闭状态广播给所有在线客户端
                        for (ServerPlayerEntity player : context.getSource().getServer().getPlayerManager().getPlayerList()) {
                            ServerPlayNetworking.send(player, new NoFrictionPayload(false));
                        }
                        return 1;
                    }));
        });
    }
}
