package com.blacksnow1002.realmmod.system.battle.attribute;

import com.blacksnow1002.realmmod.core.capability.ModCapabilities;
import com.blacksnow1002.realmmod.system.battle.attribute.capability.IPlayerTotalAttributeData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class PlayerAttributeApplier {
    public static void applyAttributesToPlayer(ServerPlayer player, IPlayerTotalAttributeData data) {
        player.getCapability(ModCapabilities.PLAYER_TOTAL_ATTRIBUTE_CAP).ifPresent(cap -> {
            float customMaxHealth = cap.getPlayerTotalMaxHealth();

            player.getAttribute(Attributes.MAX_HEALTH)
                    .setBaseValue(customMaxHealth);

            if (player.getHealth() > customMaxHealth) player.setHealth(customMaxHealth);


            player.getCapability(ModCapabilities.MANA_CAP).ifPresent(mana -> {
                mana.getManaFromPlayerAttribute(player);
            });
        });
    }
}
