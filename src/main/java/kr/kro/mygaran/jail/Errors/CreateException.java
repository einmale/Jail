package kr.kro.mygaran.jail.Errors;

import kr.kro.mygaran.jail.Util.ColorEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public enum CreateException {
    success (1, Component.text(" 로 감옥을 성공적으로 생성하였습니다.").color(ColorEnum.success.getColor())),
    error (0, Component.text("감옥을 생성하는데 문제가 생겼습니다!").color(ColorEnum.fail.getColor())),
    duplication (-1, Component.text("해당 아이디로 된 감옥이 이미 존재합니다.").color(ColorEnum.fail.getColor()));

    private final int error_code;
    private final TextComponent textComponent;

    CreateException(int e, TextComponent c) {
        this.error_code = e;
        this.textComponent = c;
    }

    public static CreateException getFromErrorCode(int error_code) {
        for (CreateException exception : CreateException.values()) {
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
