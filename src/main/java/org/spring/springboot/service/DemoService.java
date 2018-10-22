package org.spring.springboot.service;

import org.spring.springboot.controller.DemoRestController;
import org.spring.springboot.domain.OrderItem;
import org.spring.springboot.service.impl.DemoServiceImpl;

import java.util.List;

/**
 * @author weichao
 * @Description
 * @date 2018/10/22 15:33
 */
public interface DemoService{

    void insertData();

    List<OrderItem> pageTest(int start , int end);

}
