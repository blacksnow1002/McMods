package com.blacksnow1002.realmmod.technique.techniques;

import com.blacksnow1002.realmmod.assignment.AssignmentRegistry;
import com.blacksnow1002.realmmod.capability.ModCapabilities;
import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.technique.BaseTechnique;
import com.blacksnow1002.realmmod.technique.TechniqueRegistry;
import net.minecraft.server.level.ServerPlayer;

import java.util.*;

public class QiCondensationTechnique extends BaseTechnique {

    public QiCondensationTechnique() {
        super(TechniqueRegistry.TechniqueIDs.QI_CONDENSATION, "引氣訣", 3);
    }

    // ==================== 解鎖條件 ====================

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.second;
    }

    @Override
    public Map<SpiritRootType, Integer> getSpiritRootRequirements() {
        Map<SpiritRootType, Integer> requirements = new HashMap<>();
        requirements.put(SpiritRootType.SUM, 1);
        return requirements;
    }

    // ==================== 進階條件 ====================

    @Override
    public String getAdvanceAssignmentId(int level) {
        return switch (level) {
            case 2 -> AssignmentRegistry.AssignmentIds.QI_CONDENSATION_LEVEL_2;
            case 3 -> AssignmentRegistry.AssignmentIds.QI_CONDENSATION_LEVEL_3;
            default -> null;
        };
    }

    @Override
    public void onLevelReached(ServerPlayer player, int level) {
        if (level == 3) {
            player.getCapability(ModCapabilities.BREAKTHROUGH_CAPABILITY_CAP).ifPresent(cap -> {
                cap.updateCondition(1, 2, true);
            });
        };
    }

    // ==================== 效果定義 ====================

    @Override
    public Map<String , Number> getGlobalAttributes(int level) {
        Map<String, Number> attributes = new HashMap<>();

        attributes.put("defense", 3);

        return attributes;
    }

    @Override
    public Map<String, Number> getEquippedAttributes(int level) {
        Map<String, Number> attributes = new HashMap<>();

        attributes.put("attack", 3);

        return attributes;
    }


}