package net.myplayplanet.services.checker;

import java.util.HashSet;

public class StringGenerator {

    /**
     * Generate all possible permutations
     *
     * @param strings {@link java.util.List} all Permutations
     * @param string  {@link String} The Original Word
     */
    public void generate(HashSet<String> strings, String string) {
        for (char character : string.toUpperCase().toCharArray()) {
            if (!(String.valueOf(character).matches("[A-Z]"))) {
                continue;
            }
            for (char c : Letters.valueOf(String.valueOf(character)).getChars()) {
                String s = string.toUpperCase().replace(character, c);
                if (!(strings.contains(s))) {
                    strings.add(s);
                    generate(strings, s);
                }
            }
        }
        return;
    }

}
