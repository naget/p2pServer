package com.tf.graduation.server.p2pService;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class NettySocketMap {
    private static final Map<String, InetSocketAddress> MAP = new ConcurrentHashMap<>(16);
    private static final Map<String,Set<String>> GROUP = new ConcurrentHashMap<>(16);

    public static void put(String id,String user, InetSocketAddress channel) {
        MAP.put(id, channel);
        if (GROUP.get("user")==null||!GROUP.get("user").contains(id)){
            putGroup(user,id);
        }
    }

    private static void putGroup(String user,String id){
        Set<String> ids;
        ids = GROUP.computeIfAbsent(user, k -> new HashSet<>(10));
        ids.add(id);
    }
    public static InetSocketAddress get(String userandmac) {
        return MAP.get(userandmac);
    }

    public static Set<String> getGroup(String user){
        return GROUP.get(user);
    }

    public static Map<String, InetSocketAddress> getMAP() {
        return MAP;
    }

    public static void remove(InetSocketAddress address) {
        MAP.entrySet().stream().filter(entry -> entry.getValue() == address).forEach(entry -> {MAP.remove(entry.getKey()); GROUP.forEach((key, value) -> value.removeIf(id -> id.equals(entry.getKey())));});
    }
}