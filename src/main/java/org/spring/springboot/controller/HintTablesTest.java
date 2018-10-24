package org.spring.springboot.controller;

import io.shardingsphere.api.HintManager;
import org.spring.springboot.domain.OrderItem;
import org.spring.springboot.domain.User;
import org.spring.springboot.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author weichao
 * @Description
 * @date 2018/10/23 16:00
 */
@RestController
public class HintTablesTest {

    @Autowired
    private DemoService demoService;

    @GetMapping("/hinttable")
    @ResponseBody
    public List<OrderItem> selectOrderItemByHint(){
        List<OrderItem> orderItems = demoService.selectOrderItemByHint();
        return orderItems;
    }

}
