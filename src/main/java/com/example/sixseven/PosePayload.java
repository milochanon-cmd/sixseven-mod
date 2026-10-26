package com.example.sixseven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record PosePayload(int entityId) implements CustomPacketPayload {
    public static final Type<PosePayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("sixseven", "pose"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PosePayload> CODEC =
            StreamCodec.composite(ByteBufCodecs.VAR_INT, PosePayload::entityId, PosePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
