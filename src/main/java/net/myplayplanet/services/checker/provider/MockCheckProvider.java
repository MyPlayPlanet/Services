package net.myplayplanet.services.checker.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MockCheckProvider implements ICheckProvider {
    @Override
    public boolean saveBadWord(String badWord) {
        return false;
    }

    @Override
    public HashMap<String, String> loadBadWords() {
        return new HashMap<>();
    }

    @Override
    public List<String> saveAllBadWords(HashMap<String, String> values) {
        return new ArrayList<>();
    }

    @Override
    public boolean savePermutation(String badWord, String permutation) {
        return true;
    }

    @Override
    public HashMap<String, String> loadPermutations(String badWord) {
        return new HashMap<>();
    }

    @Override
    public List<String> saveAllPermutations(String badWord, HashMap<String, String> values) {
        return new ArrayList<>();
    }

    @Override
    public boolean remove(String badWord) {
        return true;
    }
}
