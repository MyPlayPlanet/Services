package net.myplayplanet.services.cache;

import java.util.UUID;

public class exanokl {
    public static void main(String[] args) {
        ListCache<UUID, String> cache = new ListCache<>("some-list-cache", uuid -> {
            //here get a single object for the list
            return null;
        });


    }

}
