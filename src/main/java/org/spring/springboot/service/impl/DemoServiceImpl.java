/*
 * Copyright 2016-2018 shardingsphere.io.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package org.spring.springboot.service.impl;

import org.spring.springboot.dao.cluster.OrderItemRepository;
import org.spring.springboot.dao.cluster.OrderRepository;
import org.spring.springboot.domain.Order;
import org.spring.springboot.domain.OrderItem;
import org.spring.springboot.service.DemoService;
import org.spring.springboot.utils.UUIDGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
/**
 * 多数据源下，从数据源要指定事务
 */

@Service
@Transactional(transactionManager = "clusterTransactionManager",rollbackFor = Exception.class)
public class DemoServiceImpl implements DemoService {
    
    @Resource
    private OrderRepository orderRepository;
    
    @Resource
    private OrderItemRepository orderItemRepository;
    
    public void demo() {
        //DefaultKeyGeneratorTest defaultKeyGenerator =  new DefaultKeyGeneratorTest();
        //orderRepository.createIfNotExistsTable();
        //orderItemRepository.createIfNotExistsTable();


        //获取所有数据
        //System.out.println(orderItemRepository.selectAll());

        //分页测试
//        List<OrderItem> orderItems = orderItemRepository.pagingTest(0 ,20000);
//        for(OrderItem oi:orderItems){
//            System.out.println(oi.toString());
//        }

        //不参与分库分表的表测试
        //int allUserCount = orderItemRepository.selectAllUserCount();
        //System.out.println("不参与分库分表的库和表，统计用户数量："+allUserCount);

       /* System.out.println("2.Delete--------------");
        for (Long each : orderIds) {
            orderRepository.delete(each);
            orderItemRepository.delete(each);
        }
        System.out.println(orderItemRepository.selectAll());
        orderItemRepository.dropTable();
        orderRepository.dropTable();*/
    }

    /**
     * @Description //数据插入测试
     * @author weichao
     * @param: []
     * @return void
     * @date 2018/10/22 15:36
     */
    @Override
    public void insertData() {
        orderRepository.truncateTable();
        orderItemRepository.truncateTable();
        List<String> orderIds = new ArrayList<>(10);
        System.out.println("1.Insert--------------");
        long startTime=System.currentTimeMillis();   //获取开始时间
        for (int i = 0; i < 100; i++) {
            Order order = new Order();
            order.setOrderId(UUIDGenerator.getUUID());
            order.setUserId("123");
            order.setStatus("INSERT_TEST");
            orderRepository.insert(order);
            String orderId = order.getOrderId();
            orderIds.add(orderId);

            OrderItem item = new OrderItem();
            item.setOrderId(orderId);
            item.setUserId("123");
            item.setOrderItemId(UUIDGenerator.getUUID());
            orderItemRepository.insert(item);
            if(i == 99){
                throw new RuntimeException("事务测试");
            }
        }
        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
    }

    /**
     * @Description //分页测试
     * @author weichao
     * @param: [start, end]
     * @return java.util.List<org.spring.springboot.domain.OrderItem>
     * @date 2018/10/22 15:36
     */
    @Override
    public List<OrderItem> pageTest(int start ,int end){
        //分页测试
        List<OrderItem> orderItems = orderItemRepository.pagingTest(start ,end);
        return orderItems;
    }

}
