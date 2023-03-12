package kr.kro.mygaran.jail.Errors;

import kr.kro.mygaran.jail.Util.ColorEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public enum SendException {
    success (1, Component.text(" 수감자를 성공적으로 수감시켰습니다.").color(ColorEnum.success.getColor())),
    error (0, Component.text("수감을 시키는데 문제가 생겼습니다.").color(ColorEnum.fail.getColor())),
    duplication (-1, Component.text("해당 수감자는 이미 수감 중입니다.").color(ColorEnum.fail.getColor()));

    private final int error_code;
    private final TextComponent textComponent;

    SendException(int e, TextComponent c) {
        this.error_code = e;
        this.textComponent = c;
    }

    public static SendException getFromErrorCode(int error_code) {
        for (SendException exception : SendException.values()) {
            if (exception.error_code != error_code) {
                continue;
            }
            return exception;
        }
        return null;
    }

    public TextComponent getTextComponent() {
        return this.textComponent;
    }

    @Override
    public String toString() {
        return String.valueOf(this.error_code);
    }
}
