package org.spring.springboot.service.impl;

import org.spring.springboot.dao.cluster.CityDao;
import org.spring.springboot.dao.master.UserDao;
import org.spring.springboot.domain.City;
import org.spring.springboot.domain.User;
import org.spring.springboot.service.UserService;
import org.spring.springboot.utils.UUIDGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户业务实现层
 *
 * Created by bysocket on 07/02/2017.
 */
@Service
@Transactional(transactionManager = "masterTransactionManager",rollbackFor = Exception.class)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao; // 主数据源

    @Autowired
    private CityDao cityDao; // 从数据源

    @Override
    public User findByName(String userName) {
        User user = userDao.findByName(userName);
        City city = cityDao.findByName("温岭市");
        user.setCity(city);
        return user;
    }

    @Override
    public void addUser() {
        for(int i=0; i<10; i++){
            User user = new User();
            user.setId(UUIDGenerator.getUUID());
            user.setUserName("aaa");
            user.setDescription("bbb");
            userDao.addUser(user);
            if(i == 9){
                throw new RuntimeException("事务测试");
            }
        }

    }
}
