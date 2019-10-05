package net.myplayplanet.services.checker;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.myplayplanet.services.ServiceCluster;
import net.myplayplanet.services.cache.AbstractSaveProvider;
import net.myplayplanet.services.cache.advanced.CacheCollectionSaveProvider;
import net.myplayplanet.services.cache.advanced.ListCache;
import net.myplayplanet.services.cache.advanced.ListCacheCollection;
import net.myplayplanet.services.checker.provider.ICheckProvider;
import net.myplayplanet.services.checker.provider.MockCheckProvider;
import net.myplayplanet.services.checker.provider.SqlCheckProvider;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
@Slf4j
public class CheckStringManager {

    @Getter
    private static CheckStringManager instance;

    private ListCache<String, String> wordCache;
    private ListCacheCollection<String, String, String> permutationCacheCollection;

    private ICheckProvider provider;

    public CheckStringManager() {
        instance = this;

        provider = (ServiceCluster.isDebug()
                ? new MockCheckProvider()
                : new SqlCheckProvider());

        wordCache = new ListCache<>("badword-cache",
                s -> s,
                new AbstractSaveProvider<String, String>() {
                    @Override
                    public boolean save(String key, String value) {
                        return provider.saveBadWord(value);
                    }

                    @Override
                    public HashMap<String, String> load() {
                        return provider.loadBadWords();
                    }

                    @Override
                    public List<String> saveAll(HashMap<String, String> values) {
                        return provider.saveAllBadWords(values);
                    }
                },
                s -> s
        );

        permutationCacheCollection = new ListCacheCollection<>("badword-permutation-cache",
                (integer, s) -> s,
                (integer, s) -> s,
                new CacheCollectionSaveProvider<String, String, String>() {
                    @Override
                    public boolean save(String masterKey, String key, String value) {
                        return provider.savePermutation(masterKey, value);
                    }

                    @Override
                    public HashMap<String, String> load(String masterKey) {
                        return provider.loadPermutations(masterKey);
                    }

                    @Override
                    public List<String> saveAll(String masterKey, HashMap<String, String> values) {
                        return provider.saveAllPermutations(masterKey, values);
                    }
                });
    }

    public int add(String word) {
        return this.add(word, true);
    }

    /**
     * Adds an String to the Bad String List in MySQL
     * Also the String to the Cache
     *
     * @param word         Which should be added
     * @param permutations No further information provided
     * @return No further information provided
     */
    public int add(String word, boolean permutations) {
        AtomicInteger integer = new AtomicInteger(0);
        ForkJoinPool.commonPool().execute(() -> {
            wordCache.addItem(word);

            if (permutations) {
                HashSet<String> badWords = new HashSet<>();

                new StringGenerator().generate(badWords, word);
                integer.set(badWords.size());
                badWords.add(word.toUpperCase());

                ListCache<String, String> cache = permutationCacheCollection.getCache(word);
                for (String badWord : badWords) {
                    cache.addItem(badWord);
                }

            }
        });
        return integer.get();
    }

    /**
     * Removes an String from the Bad String List
     *
     * @param string Which should be removed
     * @return {@link Boolean#TRUE} if successful
     */
    public boolean remove(String string) {
        if (!(check(string))) {
            return false;
        }

        wordCache.removeItem(string);
        permutationCacheCollection.getCache(string).clear();

        return provider.remove(string);
    }

    /**
     * Gets all Strings from the Cache and loads them from MySQL to the Cache if
     * the Cache is Empty
     *
     * @return {@link List} with all Strings from the List
     */
    public HashSet<String> getStrings() {
        final Collection<String> list = wordCache.getList();
        HashSet<String> result = new HashSet<>(list);
        for (String s : list) {
            result.addAll(permutationCacheCollection.getCache(s).getList());
        }
        return result;
    }

    /**
     * Checks if an String is on the Bad String List
     *
     * @param string Which should be Checked
     * @return {@link Boolean#TRUE} when the String is on the Bad String List
     */
    public boolean check(String string) {
        final HashSet<String> badWords = getBadWords(string);
        System.out.println("badwords found:");
        System.out.println(String.join(", ", badWords));
        System.out.println("====");
        return badWords.size() > 0;
    }

    public HashSet<String> getBadWords(String string) {
        HashSet<String> set = new HashSet<>();
        String message = this.removeDuplicatLetters(this.removeSpecialCharacters(string));
        for (String s : this.getStrings()) {
            if (message.contains(s.toUpperCase())) {
                set.add(s.toUpperCase());
            }
        }
        return set;
    }

    /**
     * Checks if an String is on the Bad String List
     *
     * @param string Which should be Checked
     * @return {@link Boolean#TRUE} when the String is on the Bad String List
     */
    public String removeDuplicatLetters(String string) {
        List<Character> chars = new ArrayList<>();

        String trimedString = string.toUpperCase().trim().replace(" ", "");

        char lastChar = 0;
        for (char c : trimedString.toCharArray()) {
            if (lastChar != c) {
                chars.add(c);
            }
            lastChar = c;
        }

        String shortedString = "";

        for (Character c : chars) {
            shortedString += c;
        }

        return shortedString.toUpperCase();
    }

    /**
     * Removes all Special Characters
     *
     * @param string Which should be Checked
     * @return The String without Special Characters
     */
    public String removeSpecialCharacters(String string) {
        String trimedString = string.toUpperCase().trim().replace(" ", "");

        StringBuilder stringBuilder = new StringBuilder();

        for (char c : trimedString.toCharArray()) {
            for (Letters value : Letters.values()) {
                char c1 = value.name().charAt(0);
                if (c1 == c || value.contains(value, c)) {
                    stringBuilder.append(c);
                }
            }
        }

        return stringBuilder.toString().toUpperCase();
    }

    /**
     * Normal String without LeetSpeak
     *
     * @param leetSpeakString The String in Leetspeak
     * @return Normal String
     */
    public String getNormalString(String leetSpeakString) {
        String message = "";

        String replaceString = leetSpeakString.replace(" ", "").toUpperCase();

        for (char c : replaceString.toCharArray()) {
            message += Letters.getOriginalChar(c);
        }
        return message.toUpperCase();
    }
}