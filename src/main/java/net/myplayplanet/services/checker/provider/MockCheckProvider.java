package net.myplayplanet.services.checker.provider;

import java.util.HashMap;
import java.util.List;

public class MockCheckProvider implements ICheckProvider {
    @Override
    public boolean saveBadWord(String badWord) {
        return false;
    }

    @Override
    public HashMap<String, String> loadBadWords() {
        return null;
    }

    @Override
    public List<String> saveAllBadWords(HashMap<String, String> values) {
        return null;
    }

    @Override
    public boolean savePermutation(String badWord, String permutation) {
        return false;
    }

    @Override
    public HashMap<String, String> loadPermutations(String badWord) {
        return null;
    }

    @Override
    public List<String> saveAllPermutations(String badWord, HashMap<String, String> values) {
        return null;
    }

    @Override
    public boolean remove(String badWord) {
        return false;
    }
}
