
package com.blacksnow1002.realmmod.spell;

import com.blacksnow1002.realmmod.spell.spells.FlySpell;
import com.blacksnow1002.realmmod.spell.spells.LingMuSpell;
import com.blacksnow1002.realmmod.spell.spells.LuminousSpell;
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
        // 🔮未來還能加：register(new FireballSpell()); register(new FreezeSpell()); ...
    }

    public static Map<String, BaseSpell> getAllSpells() {
        return SPELLS;
    }
}
