package com.leyou.auth;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.auth.utils.RsaUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * @Author Hwj
 * @Date 2019/4/24 19:56
 * @Version 1.0.0
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = JwtTest.class) //测定测试的位置
public class JwtTest {
    private static final String pubKeyPath = "D:\\BaiduNetdiskDownload\\xx\\app\\rsa.pub";

    private static final String priKeyPath = "D:\\BaiduNetdiskDownload\\xx\\app\\rsa.pri";

    private PublicKey publicKey;

    private PrivateKey privateKey;

    @Test   /*生成公钥和私钥*/
    public void testRsa() throws Exception {
        RsaUtils.generateKey(pubKeyPath, priKeyPath, "234");
    }

    @Before   /*测试生成公钥和私钥时注释掉before,测试生成token时打开before*/
    public void testGetRsa() throws Exception {
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);
    }

    @Test
    public void testGenerateToken() throws Exception {
        // 生成token
        String token = JwtUtils.generateToken(new UserInfo(20L, "jack"), privateKey, 5);
        System.out.println("token = " + token);
    }

    @Test
    public void testParseToken() throws Exception {
        String token = "eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MjAsInVzZXJuYW1lIjoiamFjayIsImV4cCI6MTY1NjE0MTQwOX0.h8DA9A3nFPchKOLZnA7EgTBKarOk6XUYoeJcTTPk4CBKO6q7p-4MrygXpH4Xn8mEqf_CYOfOay2FNl8zNihapVgtCCCjEw7bnSE1pmI0TLvcAAarfmd_okqhxR9RA-wBpQfXKZtgqWDHnT34o-q9SplohrRnGwTFibMk5mY4T9Y";
        // 解析token
        UserInfo user = JwtUtils.getInfoFromToken(token, publicKey);
        System.out.println("id: " + user.getId());
        System.out.println("userName: " + user.getUsername());
    }
}
