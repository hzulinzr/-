package app.service.impl;

import app.entity.User;
import app.entity.UserExample;
import app.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl{
    @Autowired
    private UserMapper userMapper;
    public int Login(String username, String password){
        UserExample example = new UserExample();
        UserExample.Criteria criteria = example.createCriteria();
        criteria.andUsernameEqualTo(username);
        List<User> users = userMapper.selectByExample(example);
        if(users.size() <= 0) {
            return -2;
        }
        criteria.andPasswordEqualTo(password);
        users = userMapper.selectByExample(example);
        if(users.size() <= 0){
            return -1;
        }else{
            return users.get(0).getLevel();
        }

    }

}
