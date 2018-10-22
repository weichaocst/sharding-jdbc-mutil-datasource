package org.spring.springboot.controller;

import org.spring.springboot.domain.OrderItem;
import org.spring.springboot.service.DemoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author weichao
 * @Description
 * @date 2018/10/22 15:32
 */
@RestController
public class DemoRestController {

    @Autowired
    private DemoService demoService;

    @RequestMapping(value = "/demo/insert", method = RequestMethod.GET)
    public void insertData() {
        demoService.insertData();
    }

    @RequestMapping(value = "/demo/page", method = RequestMethod.GET)
    public List<OrderItem> pageTest() {
        return demoService.pageTest(19000,100);
    }
}
