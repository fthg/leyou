package com.leyou.auth.client;

import com.leyou.user.api.UserApi;
import com.leyou.user.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author Hwj
 * @Date 2019/4/25 10:14
 * @Version 1.0.0
 **/
@FeignClient("user-service")
public interface UserClient extends UserApi {
}
