package com.blacksnow1002.realmmod.capability.realm_breakthrough;

import com.blacksnow1002.realmmod.capability.realm_breakthrough.IRealmBreakthroughData;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import java.util.*;

public class RealmBreakthroughData implements IRealmBreakthroughData {
    // 每個境界的突破條件
    private final Map<Integer, RealmBreakthroughConditions> realmConditions = new HashMap<>();

    public RealmBreakthroughData() {
        initializeRealmConditions();
    }

    // 初始化所有境界的條件
    private void initializeRealmConditions() {
        // 境界0: 凡夫俗子
        realmConditions.put(0, new RealmBreakthroughConditions(0)
                .addCondition(new BreakthroughCondition(0, "踏入修仙之途", false))
        );


        // 境界1: 練氣期
        realmConditions.put(1, new RealmBreakthroughConditions(1)
                .addCondition(new BreakthroughCondition(0, "氣感獲取",  false)) //第一次打坐，感受到氣感
                .addCondition(new BreakthroughCondition(1, "靈氣壓縮訓練", false))  // ??
                .addCondition(new BreakthroughCondition(2, "基礎功法圓滿", false))  // 功法系統
                .addCondition(new BreakthroughCondition(3, "服用築基丹", false))    // 獲得並服用築基丹
                .addCondition(new BreakthroughCondition(4, "打通任督二脈", false))  // 經脈系統
        );


        // 境界2: 築基期
        realmConditions.put(2, new RealmBreakthroughConditions(2)
                .addCondition(new BreakthroughCondition(0, "液態靈氣飽和", false))    // ??
                .addCondition(new BreakthroughCondition(1, "心魔試煉", false))        // 打贏自己的鏡像
                .addCondition(new BreakthroughCondition(2, "靈根純化", false))    // 靈根??
                .addCondition(new BreakthroughCondition(3, "凝丹法訣", false))        // 劇情 -> 學習凝丹法訣
                .addCondition(new BreakthroughCondition(4, "金丹雷劫", false))        // 給雷劈
        );


        // 境界3: 金丹期
        realmConditions.put(3, new RealmBreakthroughConditions(3)
                .addCondition(new BreakthroughCondition(0, "金丹九轉", false))        // 金丹經過九次精煉
                .addCondition(new BreakthroughCondition(1, "神識分化", false))        // 能夠分裂神識控制多物
                .addCondition(new BreakthroughCondition(2, "護魂法寶", false))        // 準備保護魂魄的法寶
                .addCondition(new BreakthroughCondition(3, "破丹秘法", false))        // 掌握破丹成嬰的技巧
                .addCondition(new BreakthroughCondition(4, "元嬰心魔劫", false))      // 更深層次的心魔考驗
        );


        // 境界4: 元嬰期
        realmConditions.put(4, new RealmBreakthroughConditions(4)
                .addCondition(new BreakthroughCondition(0, "元嬰出竅", false))        // 元嬰能夠離開肉身
                .addCondition(new BreakthroughCondition(1, "法則初悟", false))        // 初步理解天地法則
                .addCondition(new BreakthroughCondition(2, "領域雛形", false))        // 形成個人領域的基礎
                .addCondition(new BreakthroughCondition(3, "化神天劫", false))        // 威力更大的天劫
                .addCondition(new BreakthroughCondition(4, "神魂合一", false))        // 元嬰與魂魄完全融合
        );


        // 境界5: 化神期
        realmConditions.put(5, new RealmBreakthroughConditions(5)
                .addCondition(new BreakthroughCondition(0, "虛空感悟", false))        // 理解空間法則
                .addCondition(new BreakthroughCondition(1, "斬卻三屍", false))        // 斬去善、惡、執念三屍
                .addCondition(new BreakthroughCondition(2, "領域完善", false))        // 完成個人領域建設
                .addCondition(new BreakthroughCondition(3, "煉虛神雷", false))        // 針對神魂的特殊天劫
                .addCondition(new BreakthroughCondition(4, "虛實轉化", false))        // 能在虛實間自由轉換
        );


        // 境界6: 煉虛期
        realmConditions.put(6, new RealmBreakthroughConditions(6)
                .addCondition(new BreakthroughCondition(0, "天人感應", false))        // 與天地產生深度共鳴
                .addCondition(new BreakthroughCondition(1, "法身凝聚", false))        // 凝聚法則之身
                .addCondition(new BreakthroughCondition(2, "時空烙印", false))        // 在時空中留下個人印記
                .addCondition(new BreakthroughCondition(3, "合體之危", false))        // 肉身與法則融合的危險
                .addCondition(new BreakthroughCondition(4, "天地認可", false))        // 獲得天地意志的承認
        );


        // 境界7: 合體期
        realmConditions.put(7, new RealmBreakthroughConditions(7)
                .addCondition(new BreakthroughCondition(0, "道統傳承", false))        // 建立或繼承完整道統
                .addCondition(new BreakthroughCondition(1, "紅塵煉心", false))        // 在凡塵中完善心境
                .addCondition(new BreakthroughCondition(2, "法則圓滿", false))        // 掌握的法則達到圓滿
                .addCondition(new BreakthroughCondition(3, "教化功德", false))        // 積累足夠的教化功德
                .addCondition(new BreakthroughCondition(4, "大乘心劫", false))        // 最終的心境考驗
        );

        // 境界8: 大乘期
        realmConditions.put(8, new RealmBreakthroughConditions(8)
                .addCondition(new BreakthroughCondition(0, "道果圓滿", false))        // 將自身修煉的道果臻至圓滿無瑕
                .addCondition(new BreakthroughCondition(1, "創世感悟", false))        // 感悟世界生成與毀滅的法則真諦
                .addCondition(new BreakthroughCondition(2, "時光長河印記", false))    // 在時光長河中留下不朽印記
                .addCondition(new BreakthroughCondition(3, "教化萬靈功德", false))    // 教化生靈，積累無量功德
                .addCondition(new BreakthroughCondition(4, "斬斷命運枷鎖", false))    // 超脫命運束縛，掌握自身命數
        );


        // 境界9: 渡劫期
        realmConditions.put(9, new RealmBreakthroughConditions(9)
                .addCondition(new BreakthroughCondition(0, "仙基鑄造", false))        // 鑄就無上仙基，為轉化仙體做準備
                .addCondition(new BreakthroughCondition(1, "因果了斷", false))        // 了結所有塵世因果，無牽無掛
                .addCondition(new BreakthroughCondition(2, "煉製本命仙器", false))    // 煉製隨同飛升的本命仙器
                .addCondition(new BreakthroughCondition(3, "心魔終劫", false))        // 面對最深沉的心魔，完成最終超脫
                .addCondition(new BreakthroughCondition(4, "九重混沌劫", false))      // 經歷九重毀天滅地的混沌大劫
        );
    }


    @Override
    public boolean canBreakthrough(int realmIndex) {
        if (realmIndex < 0 || realmIndex >= 10) return false;
        RealmBreakthroughConditions conditions = realmConditions.get(realmIndex);
        return conditions != null && conditions.areAllConditionsMet();
    }

    @Override
    public boolean[] getCanBreakthrough() {
        boolean[] result = new boolean[10];
        for (int i = 0; i < 10; i++) {
            result[i] = canBreakthrough(i);
        }
        return result;
    }

    @Override
    public void setCanBreakthrough(int realmIndex, boolean value) {
        // 這個方法保留兼容性，但現在由條件系統控制
    }

    // 獲取指定境界的所有條件
    public RealmBreakthroughConditions getRealmConditions(int realmIndex) {
        return realmConditions.get(realmIndex);
    }

    // 獲取指定境界的條件列表（用於顯示）
    public List<BreakthroughCondition> getConditionList(int realmIndex) {
        RealmBreakthroughConditions conditions = realmConditions.get(realmIndex);
        return conditions != null ? conditions.getConditions() : new ArrayList<>();
    }

    // 更新某個條件的完成狀態
    @Override
    public void updateCondition(int realmIndex, int conditionId, boolean completed) {
        RealmBreakthroughConditions conditions = realmConditions.get(realmIndex);
        if (conditions != null) {
            conditions.updateCondition(conditionId, completed);
        }
    }

    // 獲取已完成和未完成的條件
    public Map<String, List<BreakthroughCondition>> getConditionStatus(int realmIndex) {
        Map<String, List<BreakthroughCondition>> status = new HashMap<>();
        List<BreakthroughCondition> completed = new ArrayList<>();
        List<BreakthroughCondition> incomplete = new ArrayList<>();

        RealmBreakthroughConditions conditions = realmConditions.get(realmIndex);
        if (conditions != null) {
            for (BreakthroughCondition condition : conditions.getConditions()) {
                if (condition.isCompleted()) {
                    completed.add(condition);
                } else {
                    incomplete.add(condition);
                }
            }
        }

        status.put("completed", completed);
        status.put("incomplete", incomplete);
        return status;
    }

    // 獲取突破進度 (完成數/總數)
    @Override
    public String getBreakthroughProgress(int realmIndex) {
        RealmBreakthroughConditions conditions = realmConditions.get(realmIndex);
        if (conditions == null) return "0/0";

        int total = conditions.getConditions().size();
        int completed = (int) conditions.getConditions().stream()
                .filter(BreakthroughCondition::isCompleted)
                .count();

        return completed + "/" + total;
    }

    public void saveNBTData(CompoundTag nbt) {
        ListTag realmList = new ListTag();

        for (Map.Entry<Integer, RealmBreakthroughConditions> entry : realmConditions.entrySet()) {
            CompoundTag realmTag = new CompoundTag();
            realmTag.putInt("realmIndex", entry.getKey());

            ListTag conditionList = new ListTag();
            for (BreakthroughCondition condition : entry.getValue().getConditions()) {
                CompoundTag conditionTag = new CompoundTag();
                conditionTag.putInt("id", condition.getId());
                conditionTag.putString("description", condition.getDescription());
                conditionTag.putBoolean("completed", condition.isCompleted());
                conditionList.add(conditionTag);
            }

            realmTag.put("conditions", conditionList);
            realmList.add(realmTag);
        }

        nbt.put("realmConditions", realmList);
    }

    public void loadNBTData(CompoundTag nbt) {
        if (nbt.contains("realmConditions")) {
            ListTag realmList = nbt.getList("realmConditions", Tag.TAG_COMPOUND);

            for (int i = 0; i < realmList.size(); i++) {
                CompoundTag realmTag = realmList.getCompound(i);
                int realmIndex = realmTag.getInt("realmIndex");

                RealmBreakthroughConditions conditions = realmConditions.get(realmIndex);
                if (conditions != null) {
                    ListTag conditionList = realmTag.getList("conditions", Tag.TAG_COMPOUND);

                    for (int j = 0; j < conditionList.size(); j++) {
                        CompoundTag conditionTag = conditionList.getCompound(j);
                        int id = conditionTag.getInt("id");
                        boolean completed = conditionTag.getBoolean("completed");

                        conditions.updateCondition(id, completed);
                    }
                }
            }
        }
    }

    // 內部類：單個突破條件
    public static class BreakthroughCondition {
        private final int id;
        private final String description;
        private boolean completed;

        public BreakthroughCondition(int id, String description, boolean completed) {
            this.id = id;
            this.description = description;
            this.completed = completed;
        }

        public int getId() { return id; }
        public String getDescription() { return description; }
        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    // 內部類：某個境界的所有條件
    public static class RealmBreakthroughConditions {
        private final int realmIndex;
        private final List<BreakthroughCondition> conditions;

        public RealmBreakthroughConditions(int realmIndex) {
            this.realmIndex = realmIndex;
            this.conditions = new ArrayList<>();
        }

        public RealmBreakthroughConditions addCondition(BreakthroughCondition condition) {
            conditions.add(condition);
            return this;
        }

        public List<BreakthroughCondition> getConditions() {
            return new ArrayList<>(conditions);
        }

        public boolean areAllConditionsMet() {
            return conditions.stream().allMatch(BreakthroughCondition::isCompleted);
        }

        public void updateCondition(int conditionId, boolean completed) {
            conditions.stream()
                    .filter(c -> c.getId() == conditionId)
                    .findFirst()
                    .ifPresent(c -> c.setCompleted(completed));
        }

        public int getRealmIndex() {
            return realmIndex;
        }
    }
}