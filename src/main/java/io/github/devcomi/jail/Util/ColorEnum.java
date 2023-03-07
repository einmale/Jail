package io.github.devcomi.jail.Util;

import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.Nullable;

public enum ColorEnum {

    success (TextColor.color(132, 255, 104)),
    fail (TextColor.color(255, 150, 121)),
    blue (TextColor.color(136, 197, 255));

    private final TextColor color;

    ColorEnum(TextColor color) {
        this.color = color;
    }

    public TextColor getColor() {
        return this.color;
    }
}
