package net.myplayplanet.services.checker;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Letters {

    A(1, '4', '@'),
    B(2, '8', '&'),
    C(3, '('),
    D(4),
    E(5, '3', '€'),
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
    Q(17, 'O'),
    R(18),
    S(19, '5', '$'),
    T(20, '7'),
    U(21, 'V'),
    V(22),
    W(23),
    X(24),
    Y(25),
    Z(26, '2'),
    Ä(27, 'A'),
    Ö(28, 'O'),
    Ü(29, 'U');

    private int id;
    private char[] chars;

    Letters(int id, char... chars) {
        this.id = id;
        this.chars = chars;
    }

}
