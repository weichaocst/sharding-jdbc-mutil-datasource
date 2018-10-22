package org.spring.springboot.sharding;

import com.google.common.base.Preconditions;
import io.shardingsphere.api.algorithm.sharding.ListShardingValue;
import io.shardingsphere.api.algorithm.sharding.ShardingValue;
import io.shardingsphere.api.algorithm.sharding.complex.ComplexKeysShardingAlgorithm;
import org.spring.springboot.hash.MurmurConsistHash;


import java.util.*;

/**
 * @author weichao
 * @Description 多分片健一致性哈希算法分片设置
 * @date 2018/10/18 16:13
 */
public class ConsistentHashingComplexKeysShardingAlgorithmSharding implements ComplexKeysShardingAlgorithm {
    @Override
    public Collection<String> doSharding(Collection<String> availableTargetNames, Collection<ShardingValue> shardingValues) {

        ArrayList<ListShardingValue> listShardingValues = (ArrayList)shardingValues;

        StringBuilder splicingValues = new StringBuilder();
        for(ListShardingValue listShardingValue:listShardingValues){
            splicingValues.append(listShardingValue.getValues());
        }
        Collection<String> result = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        MurmurConsistHash<String> consistentHash = new MurmurConsistHash<>(availableTargetNames);
        for (String each : availableTargetNames) {
            if (each.endsWith(consistentHash.get(splicingValues.toString()))) {
                System.out.println("当前访问节点为："+consistentHash.get(splicingValues.toString())+"分片健值为："+splicingValues.toString());
                result.add(each);
                return result;
            }
        }
        throw new UnsupportedOperationException();
    }
}
