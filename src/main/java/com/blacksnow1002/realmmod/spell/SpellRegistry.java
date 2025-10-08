
package com.blacksnow1002.realmmod.spell;

import com.blacksnow1002.realmmod.spell.spells.*;

import java.util.HashMap;
import java.util.Map;

public class SpellRegistry {
    private static final Map<String, BaseSpell> SPELLS = new HashMap<>();

    public static void register(BaseSpell spell) {
        SPELLS.put(spell.getName(), spell);
    }

    public static BaseSpell get(String name) {
        return SPELLS.get(name);
    }

    public static void registerAll() {
        register(new LuminousSpell());
        register(new LingMuSpell());
        register(new FlySpell());
        register(new SpiritOutSpell());
        register(new SetMarkSpell());
        register(new MarkedTeleportSpell());
        register(new ShortTeleportSpell());
        register(new GiantSpell());
        register(new CloneSpell());
        register(new TransformSpell());
        // 🔮未來還能加：register(new FireballSpell()); register(new FreezeSpell()); ...
    }

    public static Map<String, BaseSpell> getAllSpells() {
        return SPELLS;
    }
}
