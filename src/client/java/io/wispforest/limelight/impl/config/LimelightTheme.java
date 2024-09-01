package io.wispforest.limelight.impl.config;

import io.wispforest.limelight.impl.Limelight;

public interface LimelightTheme {
    LimelightTheme LIGHT = new LimelightTheme() {
        @Override
        public int popupBackground() {
            return 0xAAFBFAF5;
        }

        @Override
        public int searchBoxColor() {
            return 0xFF000000;
        }

        @Override
        public int focusOutlineColor() {
            return 0xFFFFFFFF;
        }

        @Override
        public int sourceExtensionColor() {
            return 0x555555;
        }

        @Override
        public int resultEntryTextColor() {
            return 0x000000;
        }
    };

    LimelightTheme DARK = new LimelightTheme() {
        @Override
        public int popupBackground() {
            return 0xAA333333;
        }

        @Override
        public int searchBoxColor() {
            return 0xFFFFFFFF;
        }

        @Override
        public int focusOutlineColor() {
            return 0xFFFFFFFF;
        }

        @Override
        public int sourceExtensionColor() {
            return 0xFF999999;
        }

        @Override
        public int resultEntryTextColor() {
            return 0xFFFFFF;
        }
    };

    static LimelightTheme current() {
        return switch (Limelight.CONFIG.theme()) {
            case LIGHT -> LIGHT;
            case DARK -> DARK;
        };
    }

    int popupBackground();

    int searchBoxColor();

    int focusOutlineColor();

    int sourceExtensionColor();

    int resultEntryTextColor();
}
