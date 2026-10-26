package com.example.sixseven;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record RequestPayload() implements CustomPacketPayload {
    public static final Type<RequestPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("sixseven", "request"));
    public static final StreamCodec<RegistryFriendlyByteBuf, RequestPayload> CODEC =
            StreamCodec.unit(new RequestPayload());

    @Override
    public Type<? extends CustomPacketPayload> type() { return TYPE; }
}
