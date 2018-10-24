package org.spring.springboot.hash;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Created by Intellij Idea 2017.3
 * Author : noven.zhen
 * Date : 2018-09-07
 * Time : 15:02
 */
public class MurmurConsistHash<T> {
    // 每个机器节点关联的虚拟节点个数
    private int numberOfReplicas;
    // 环形虚拟节点
    private final SortedMap<Long, T> circle = new TreeMap<Long, T>();
    private final String VERTUAL_PRFIX = "$";

    public MurmurConsistHash(Collection<T> nodes) {
        this(100, nodes);
    }

    /**
     * @param numberOfReplicas 每个机器节点关联的虚拟节点个数
     * @param nodes            真实机器节点
     */
    public MurmurConsistHash(int numberOfReplicas, Collection<T> nodes) {
        this.numberOfReplicas = numberOfReplicas;

        for (T node : nodes) {
            add(node);
        }
    }

    /**
     * 增加真实机器节点
     *
     * @param node
     */
    public void add(T node) {
        for (int i = 0; i < this.numberOfReplicas; i++) {
            circle.put(murMurHash(node.toString() + VERTUAL_PRFIX + i), node);
        }
    }

    /**
     * 删除真实机器节点
     *
     * @param node
     */
    public void remove(T node) {
        for (int i = 0; i < this.numberOfReplicas; i++) {
            circle.remove(murMurHash(node.toString() + VERTUAL_PRFIX + i));
        }
    }

    /**
     * 取得真实机器节点
     *
     * @param key
     * @return
     */
    public T get(String key) {
        if (circle.isEmpty()) {
            return null;
        }

        long hash = murMurHash(key);
        if (!circle.containsKey(hash)) {
            // 沿环的顺时针找到一个虚拟节点
            SortedMap<Long, T> tailMap = circle.tailMap(hash);
            hash = tailMap.isEmpty() ? circle.firstKey() : tailMap.firstKey();
        }
        // 返回该虚拟节点对应的真实机器节点的信息
        return circle.get(hash);
    }

    private static Long murMurHash(String key) {
        ByteBuffer buf = ByteBuffer.wrap(key.getBytes());
        int seed = 0x1234ABCD;

        ByteOrder byteOrder = buf.order();
        buf.order(ByteOrder.LITTLE_ENDIAN);

        long m = 0xc6a4a7935bd1e995L;
        int r = 47;

        long h = seed ^ (buf.remaining() * m);

        long k;
        while (buf.remaining() >= 8) {
            k = buf.getLong();

            k *= m;
            k ^= k >>> r;
            k *= m;

            h ^= k;
            h *= m;
        }

        if (buf.remaining() > 0) {
            ByteBuffer finish = ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN);
            finish.put(buf).rewind();
            h ^= finish.getLong();
            h *= m;
        }

        h ^= h >>> r;
        h *= m;
        h ^= h >>> r;

        buf.order(byteOrder);
        return h;
    }
}