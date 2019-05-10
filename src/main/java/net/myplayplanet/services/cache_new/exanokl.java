package net.myplayplanet.services.cache_new;

import java.util.Arrays;
import java.util.List;

public class exanokl {
    public static void main(String[] args) {
        List<byte[]> br = Arrays.asList("1".getBytes(), "2".getBytes(), "3".getBytes());
        for (byte[] bytes : br) {
            System.out.println(bytes);
        }
        asdfasdf(br);
    }


    public static <K> byte[][] asdfasdf(List<byte[]> list) {
        byte[][] br = list.toArray(new byte[0][]);


        byte[][] r = new byte[list.size()][];
        for (int i = 0; i < list.size(); i++) {
            r[i] = list.get(i);
        }


        System.out.println("====");
        for (byte[] bytes : br) {
            System.out.println(bytes);
        }

        System.out.println("====");
        for (byte[] bytes : r) {
            System.out.println(bytes);
        }


        return r;
    }

}
