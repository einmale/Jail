package kr.kro.mygaran.jail.Errors;

import kr.kro.mygaran.jail.Util.ColorEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public enum ReleaseException {
    success (1,Component.text(" 님을 성공적으로 석방했습니다.").color(ColorEnum.success.getColor())),
    error (0, Component.text("석방을 시키는데 문제가 생겼습니다.").color(ColorEnum.fail.getColor())),
    duplication (-1, Component.text("해당 수감자를 찾을 수 없습니다.").color(ColorEnum.fail.getColor()));

    private final int error_code;
    private final TextComponent textComponent;

    ReleaseException(int e, TextComponent c) {
        this.error_code = e;
        this.textComponent = c;
    }

    public static ReleaseException getFromErrorCode(int error_code) {
        for (ReleaseException exception : ReleaseException.values()) {
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
