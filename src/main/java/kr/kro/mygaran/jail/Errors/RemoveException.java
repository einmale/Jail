package kr.kro.mygaran.jail.Errors;

import kr.kro.mygaran.jail.Util.ColorEnum;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;

public enum RemoveException {
    success (1,Component.text(" 감옥을 성공적으로 삭제하였습니다.").color(ColorEnum.success.getColor())),
    error (0, Component.text("감옥을을 삭제하는데 문제가 생겼습니다.").color(ColorEnum.fail.getColor())),
    duplication (-1, Component.text("해당 감옥을 찾을 수 없습니다.").color(ColorEnum.fail.getColor()));

    private final int error_code;
    private final TextComponent textComponent;

    RemoveException(int e, TextComponent c) {
        this.error_code = e;
        this.textComponent = c;
    }

    public static RemoveException getFromErrorCode(int error_code) {
        for (RemoveException exception : RemoveException.values()) {
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
