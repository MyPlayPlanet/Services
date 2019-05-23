package net.myplayplanet.services.checker.provider;

import java.util.HashMap;
import java.util.List;

public interface ICheckProvider {
    boolean saveBadWord(String badWord);
    HashMap<String, String> loadBadWords();
    List<String> saveAllBadWords(HashMap<String, String> values);

    boolean savePermutation(String badWord, String permutation);
    HashMap<String, String> loadPermutations(String badWord);
    List<String> saveAllPermutations(String badWord, HashMap<String, String> values);
    boolean remove(String badWord);
}
