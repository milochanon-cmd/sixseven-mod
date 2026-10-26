package com.example.sixseven;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.*;

public class SixSeven implements ModInitializer {
    private static final int COOLDOWN_TICKS = 40; // 2 seconds
    private final Map<UUID, Long> lastUse = new HashMap<>();

    @Override
    public void onInitialize() {
        PayloadTypeRegistry.playC2S().register(RequestPayload.TYPE, RequestPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(PosePayload.TYPE, PosePayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(RequestPayload.TYPE, (payload, ctx) -> {
            ServerPlayer player = ctx.player();
            long now = ctx.server().getTickCount();
            Long last = lastUse.get(player.getUUID());
            if (last != null && now - last < COOLDOWN_TICKS) return;
            lastUse.put(player.getUUID(), now);

            enchantEverything(player);

            PosePayload pose = new PosePayload(player.getId());
            for (ServerPlayer p : PlayerLookup.world(player.level())) {
                ServerPlayNetworking.send(p, pose);
            }
        });
    }

    private static void enchantEverything(ServerPlayer player) {
        Registry<Enchantment> reg = player.level().registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        List<Holder.Reference<Enchantment>> pool = reg.listElements()
                .filter(h -> !h.is(Enchantments.SILK_TOUCH))
                .filter(h -> !h.is(EnchantmentTags.CURSE))
                .toList();

        RandomSource rand = player.getRandom();
        var inv = player.getInventory();

        for (int i = 0; i < inv.getContainerSize(); i++) {
            ItemStack stack = inv.getItem(i);
            if (stack.isEmpty()) continue;

            List<Holder<Enchantment>> candidates = new ArrayList<>();
            for (Holder.Reference<Enchantment> h : pool) {
                if (h.value().isSupportedItem(stack)) candidates.add(h);
            }
            Collections.shuffle(candidates, new Random(rand.nextLong()));

            List<Holder<Enchantment>> chosen = new ArrayList<>();
            for (Holder<Enchantment> c : candidates) {
                boolean ok = true;
                for (Holder<Enchantment> o : chosen) {
                    if (!Enchantment.areCompatible(c, o)) { ok = false; break; }
                }
                if (ok) chosen.add(c);
                if (chosen.size() == 3) break;
            }
            if (chosen.isEmpty()) continue;

            EnchantmentHelper.updateEnchantments(stack, mutable -> {
                for (Holder<Enchantment> h : chosen) {
                    int max = h.value().getMaxLevel();
                    mutable.set(h, 1 + rand.nextInt(max));
                }
            });
        }
    }
}
