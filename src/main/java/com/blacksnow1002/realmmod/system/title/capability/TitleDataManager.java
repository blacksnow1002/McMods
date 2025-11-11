package com.blacksnow1002.realmmod.system.title.capability;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.HashSet;
import java.util.Set;

public class TitleDataManager implements ITitleDataManager {

    private final Set<String> haveTitles = new HashSet<>();

    private String equippedTitle = "";

    @Override
    public Set<String> getHaveTitles() { return haveTitles; }

    @Override
    public boolean hasTitle(String titleId) { return haveTitles.contains(titleId); }

    @Override
    public String getEquipTitle() { return equippedTitle; }

    @Override
    public void unlockTitle(String titleId) {
        haveTitles.add(titleId);
    }

    @Override
    public void equipTitle(String titleId) {
        equippedTitle = titleId;
    }

    @Override
    public void unequipTitle() {
        equippedTitle = "";
    }

    @Override
    public CompoundTag saveNBTData() {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();
        for (String titleId : haveTitles) {
            CompoundTag t = new CompoundTag();
            t.putString("id", titleId);
            listTag.add(t);
        }
        tag.put("Titles", listTag);

        tag.putString("equippedTitle", equippedTitle);

        return tag;
    }

    @Override
    public void loadNBTData(CompoundTag tag) {
        haveTitles.clear();

        if (tag.contains("Titles", Tag.TAG_LIST)) {
            ListTag list = tag.getList(("Titles"),  Tag.TAG_COMPOUND);
            for (Tag t : list) {
                haveTitles.add(((CompoundTag)t).getString("id"));
            }
        }

        equippedTitle = tag.getString("equippedTitle");
    }
}
