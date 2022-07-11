package com.leyou.auth.config;

import com.leyou.auth.utils.RsaUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.security.PrivateKey;
import java.security.PublicKey;


/*@Component*/  /*声明这是一个bean*/
@ConfigurationProperties(prefix = "ly.jwt")  /*文件内容前缀为ly.jwt*/
public class JwtProperties {

    private String secret; // 密钥

    private String pubKeyPath;// 公钥

    private String priKeyPath;// 私钥

    private int expire;// token过期时间

    private PublicKey publicKey; // 公钥

    private PrivateKey privateKey; // 私钥

    private  String cookieName;//cookie名称
    //对象一旦实例化后，就应该读取公钥和私钥
    @PostConstruct
    public void init() throws Exception{
        //公钥私钥如果不存在，先生成
        File pubPath = new File(pubKeyPath);
        File priPath = new File(priKeyPath);

        if(!pubPath.exists() || !priPath.exists()){
            //生成公钥和私钥
            RsaUtils.generateKey(pubKeyPath,priKeyPath,secret);
        }
        //读取公钥和私钥
        this.publicKey = RsaUtils.getPublicKey(pubKeyPath);
        this.privateKey = RsaUtils.getPrivateKey(priKeyPath);

    }

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public String getPubKeyPath() {
        return pubKeyPath;
    }

    public void setPubKeyPath(String pubKeyPath) {
        this.pubKeyPath = pubKeyPath;
    }

    public String getPriKeyPath() {
        return priKeyPath;
    }

    public void setPriKeyPath(String priKeyPath) {
        this.priKeyPath = priKeyPath;
    }

    public int getExpire() {
        return expire;
    }

    public void setExpire(int expire) {
        this.expire = expire;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public String getCookieName() {
        return cookieName;
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }
}