package com.example.sixseven;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

public class SixSevenClient implements ClientModInitializer {
    public static final Map<Integer, Integer> ACTIVE = new HashMap<>(); // entityId -> ticks left
    public static final int DURATION = 40;

    private static KeyMapping key;

    @Override
    public void onInitializeClient() {
        key = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.sixseven.pose",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_G,
                KeyMapping.Category.register(Identifier.fromNamespaceAndPath("sixseven", "main"))
        ));

        ClientPlayNetworking.registerGlobalReceiver(PosePayload.TYPE,
                (payload, ctx) -> ACTIVE.put(payload.entityId(), DURATION));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (key.consumeClick()) {
                if (client.player != null) ClientPlayNetworking.send(new RequestPayload());
            }
            ACTIVE.replaceAll((id, t) -> t - 1);
            ACTIVE.values().removeIf(t -> t <= 0);
        });
    }
}
