package com.blacksnow1002.realmmod.technique.techniques;

import com.blacksnow1002.realmmod.capability.attribute.technique.TechniqueAttributeData;
import com.blacksnow1002.realmmod.capability.cultivation.CultivationRealm;
import com.blacksnow1002.realmmod.technique.BaseTechnique;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.*;

/**
 * 焚天訣 - 傳說級火系功法
 *
 * 解鎖條件：
 * - 境界：築基期
 * - 靈根：火靈根 5級、木靈根 3級
 * - 前置：需先修煉「烈焰心法」
 *
 * 效果：
 * - 全域：增加火焰抗性和攻擊力
 * - 裝備：大幅提升攻擊速度，獲得火焰神通
 */
public class FireSupremeTechnique extends BaseTechnique {

    public FireSupremeTechnique() {
        super("fire_supreme", "焚天訣", 9);
    }

    // ==================== 解鎖條件 ====================

    @Override
    public CultivationRealm getRequiredRealm() {
        return CultivationRealm.third; // 築基期
    }

    @Override
    public Map<SpiritRootType, Integer> getSpiritRootRequirements() {
        Map<SpiritRootType, Integer> requirements = new HashMap<>();
        requirements.put(SpiritRootType.FIRE, 5);  // 火靈根5級
        requirements.put(SpiritRootType.WOOD, 3);  // 木靈根3級（木生火）
        return requirements;
    }

    @Override
    public List<String> getPrerequisiteTechniques() {
        return Collections.emptyList();
    }

    @Override
    public String getUnlockAssignmentId() {
        return "assignment_ancient_fire_temple"; // 需要完成「上古火神殿」任務
    }

    // ==================== 進階條件 ====================

    @Override
    public CultivationRealm getRequiredRealmForLevel(int level) {
        // 每3層需要提升一個大境界
        return switch (level) {
            case 1, 2, 3 -> CultivationRealm.second; // 築基期
            case 4, 5, 6 -> CultivationRealm.third;             // 金丹期
            case 7, 8, 9 -> CultivationRealm.fourth;            // 元嬰期
            default -> CultivationRealm.second;
        };
    }

    @Override
    public String getAdvanceAssignmentId(int level) {
        // 只有突破大關卡時需要任務
        return switch (level) {
            case 4 -> "assignment_fire_trial_golden";   // 進入金丹期需要火焰試煉
            case 7 -> "assignment_fire_trial_nascent";  // 進入元嬰期需要更高級的試煉
            default -> null;
        };
    }

    // ==================== 效果定義 ====================

    @Override
    public Map<String , Number> getGlobalAttributes(int level) {
        Map<String, Number> attributes = new HashMap<>();

        attributes.put("defense", 5);

        return attributes;
    }

    @Override
    public Map<String, Number> getEquippedAttributes(int level) {
        Map<String, Number> attributes = new HashMap<>();

        attributes.put("attack", 5);

        return attributes;
    }

    @Override
    public List<String> getProvidedSpells(int level) {
        List<String> spells = new ArrayList<>();

        // 根據等級解鎖不同的神通
        if (level >= 1) {
            spells.add("spell_fire_ball");        // 1層：火球術
        }
        if (level >= 3) {
            spells.add("spell_fire_wall");        // 3層：火焰之牆
        }
        if (level >= 5) {
            spells.add("spell_fire_phoenix");     // 5層：火鳳凰召喚
        }
        if (level >= 7) {
            spells.add("spell_fire_dragon");      // 7層：火龍吐息
        }
        if (level >= 9) {
            spells.add("spell_burning_heaven");   // 9層：焚天滅地（大招）
        }

        return spells;
    }
}