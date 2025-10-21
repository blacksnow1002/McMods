package com.blacksnow1002.realmmod.title;

public abstract class BaseTitle {
    
    private final String titleId;
    private final String displayName;

    public BaseTitle(String titleId, String displayName) {
        this.titleId = titleId;
        this.displayName = displayName;
    }

    public String getTitleId() {
        return titleId;
    }

    public String getDisplayName() {
        return displayName;
    }

}
