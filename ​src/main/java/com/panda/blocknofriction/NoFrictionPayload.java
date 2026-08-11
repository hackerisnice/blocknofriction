package com.panda.blocknofriction;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record NoFrictionPayload(boolean enabled) implements CustomPayload {
    // 1.21 的标准网络包注册方式
    public static final CustomPayload.Id<NoFrictionPayload> ID = new CustomPayload.Id<>(Identifier.of("blocknofriction", "sync"));
    
    public static final PacketCodec<RegistryByteBuf, NoFrictionPayload> CODEC = PacketCodec.of(
        (value, buf) -> buf.writeBoolean(value.enabled()),
        buf -> new NoFrictionPayload(buf.readBoolean())
    );

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
