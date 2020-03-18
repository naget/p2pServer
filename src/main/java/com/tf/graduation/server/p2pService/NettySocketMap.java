package com.tf.graduation.server.p2pService;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class NettySocketMap {
    private static final Map<String, InetSocketAddress> MAP = new ConcurrentHashMap<>(16);
    private static final Map<String,List<InetSocketAddress>> GROUP = new ConcurrentHashMap<>(16);

    public static void put(String id,String user, InetSocketAddress channel) {
        MAP.put(id, channel);
        if (GROUP.get("user")==null||!GROUP.get("user").contains(channel)){
            putGroup(user,channel);
        }
    }

    private static void putGroup(String user,InetSocketAddress channel){
        List<InetSocketAddress> channels;
        channels = GROUP.computeIfAbsent(user, k -> new ArrayList<>(10));
        channels.add(channel);
    }
    public static InetSocketAddress get(Long id) {
        return MAP.get(id);
    }

    public static List<InetSocketAddress> getGroup(String user){
        return GROUP.get(user);
    }

    public static Map<String, InetSocketAddress> getMAP() {
        return MAP;
    }

    public static void remove(InetSocketAddress address) {
        MAP.entrySet().stream().filter(entry -> entry.getValue() == address).forEach(entry -> MAP.remove(entry.getKey()));
        GROUP.forEach((key, value) -> value.removeIf(channel -> channel == address));
    }
}