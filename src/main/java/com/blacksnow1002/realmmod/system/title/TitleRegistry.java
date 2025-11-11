package com.blacksnow1002.realmmod.system.title;

import com.blacksnow1002.realmmod.system.title.titles.TestTitle;

public class TitleRegistry {

    public static void registerAll() {
        TitleSystem system = TitleSystem.getInstance();

        system.registerTitle(new TestTitle());
    }

    public static BaseTitle getTitle(String titleId) {
        return TitleSystem.getInstance().getTitle(titleId);
    }

    public static final class TitleIds {
        public static final String TEST_TITLE = "test_title";
    }
}
