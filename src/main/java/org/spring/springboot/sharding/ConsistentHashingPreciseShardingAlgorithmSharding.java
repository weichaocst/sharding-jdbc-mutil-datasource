package org.spring.springboot.sharding;

import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import org.spring.springboot.hash.MurmurConsistHash;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;

/**
 * @author weichao
 * @Description 单分片健一致性哈希算法分片设置
 * @date 2018/10/18 9:39
 */
public class ConsistentHashingPreciseShardingAlgorithmSharding implements PreciseShardingAlgorithm<String>  {

    @Override
    public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<String> shardingValue) {
        MurmurConsistHash<String> consistentHash = new MurmurConsistHash<>(availableTargetNames);
        for (String each : availableTargetNames) {
            if (each.endsWith(consistentHash.get(shardingValue.getValue().toString()))) {
                //System.out.println("当前访问节点为："+consistentHash.get(shardingValue.getValue().toString())+"分片健值为："+shardingValue.getColumnName()+":"+shardingValue.getValue().toString());
                return each;
            }
        }
        throw new UnsupportedOperationException();
    }

}
