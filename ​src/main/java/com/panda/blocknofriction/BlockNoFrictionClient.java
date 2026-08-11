package com.panda.blocknofriction;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class BlockNoFrictionClient implements ClientModInitializer {
    
    @Override
    public void onInitializeClient() {
        // 监听服务端发来的 NoFrictionPayload 数据包
        ClientPlayNetworking.registerGlobalReceiver(NoFrictionPayload.ID, (payload, context) -> {
            // 确保在客户端主线程上修改变量
            context.client().execute(() -> {
                BlockNoFriction.isNoFrictionEnabled = payload.enabled();
            });
        });
    }
}
