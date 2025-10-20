package com.blacksnow1002.realmmod.assignment;

public enum AssignmentCategory {
    MAIN("主線任務", "§6"),
    TECHNIQUE("功法任務", "§d"),
    SECT("宗門任務", "§b"),
    SIDE("支線任務", "§a"),
    ENCOUNTER("奇遇任務", "§5");

    private final String categoryName;
    private final String colorCode;

    AssignmentCategory(final String categoryName, final String colorCode) {
        this.categoryName = categoryName;
        this.colorCode = colorCode;
    }

    public String getCategoryName() { return this.categoryName; }

    public String getColorCode() { return this.colorCode; }

}
