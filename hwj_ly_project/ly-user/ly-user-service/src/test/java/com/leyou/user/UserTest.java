package com.leyou.user;

import com.leyou.LyUserApplication;
import com.leyou.user.mapper.UserMapper;
import com.leyou.user.pojo.User;
import com.leyou.user.utils.CodecUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = LyUserApplication.class)
public class UserTest {

    @Autowired
    private UserMapper userMapper;

    /**
     * 注册用户
     */
    @Test
    public void addUser(){
        User user = new User();

            user.setId(null);
            user.setCreated(new Date());
            user.setPhone("1873482"+String.format("%04d",2));//%04d表示四位数，前边补零
            user.setUsername("username2");
            user.setPassword("username2");
            String encodePassword = CodecUtils.passwordBcryptEncode(user,user.getPassword().trim());
            user.setPassword(encodePassword);
            this.userMapper.insertSelective(user);

    }

    /**
     * 添加后台管理人员
     */
    @Test
    public void addAdmin(){
        User user = new User();
        user.setCreated(new Date());
        user.setPhone("88888888");
        user.setUsername("admin1");
        user.setPassword("admin1");
        String encodePassword = CodecUtils.passwordBcryptEncode(user,user.getPassword().trim());
        user.setPassword(encodePassword);
        userMapper.insertSelective(user);
    }

}
