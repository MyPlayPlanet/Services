package net.myplayplanet.services.checker;

import lombok.Getter;

@Getter
public enum Letters {

    A(1, '4', '@', 'À'),
    B(2, '8', '&'),
    C(3, '('),
    D(4),
    E(5, '3', '€', 'È'),
    F(6),
    G(7, '6'),
    H(8),
    I(9, '1', '|', '¥'),
    J(10),
    K(11),
    L(12, '1', '|'),
    M(13),
    N(14),
    O(15, '0', 'Ø'),
    P(16),
    Q(17),
    R(18),
    S(19, '5', '$'),
    T(20, '7'),
    U(21, 'V'),
    V(22),
    W(23),
    X(24),
    Y(25),
    Z(26, '2'),
    Ä(27),
    Ö(28),
    Ü(29);

    private int id;
    private char[] chars;

    Letters(int id, char... chars) {
        this.id = id;
        this.chars = chars;
    }

    public static char getOriginalChar(char c) {
        for (Letters value : Letters.values()) {
            if (value.name().charAt(0) == c) {
                return c;
            }
            if (value.contains(value, c)) {
                return value.name().charAt(0);
            }
        }

        return c;
    }

    boolean contains(Letters letters, char c) {
        for (char aChar : letters.getChars()) {
            if (aChar == c) {
                return true;
            }
        }
        return false;
    }
}
