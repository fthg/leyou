
package com.leyou.filters;

import com.leyou.auth.entiy.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.FilterProperties;
/*import com.leyou.config.JwtProperties;*/
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;


/**
 * @author: HuYi.Zhang
 * @create: 2018-05-29 08:57
 **/

@Slf4j
@Component //放在spring容器中
@EnableConfigurationProperties(FilterProperties.class)
public class AuthFilter extends ZuulFilter {


   /* @Autowired
    private JwtProperties prop;*/


    @Autowired
    private  FilterProperties filterProp;

    //过滤类型，有前置 路由中 后置 出错时的过滤器
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;//过滤器类型，前置过滤
    }

    //这个值决定了多个同一种过滤器时的优先级，返回值越小优先级越高
    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER;
    }

    //是否执行过滤，true就执行run 方法
    @Override
    public boolean shouldFilter() {
        // 获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest req = ctx.getRequest();
        // 获取路径
        String requestURI = req.getRequestURI();
        // 判断白名单
        return !isAllowPath(requestURI);
    }
    private boolean isAllowPath(String requestURI) {
        // 定义一个标记
        boolean flag = false;
        // 遍历允许访问的路径
        for (String path : filterProp.getAllowPaths()) {
            // 然后判断是否是符合
            if(requestURI.startsWith(path)){
                flag = true;
                break;
            }
        }
        return flag;
    }
    //编写过滤器的业务逻辑
    @Override
    public Object run() throws ZuulException {

      /*    //        获取上下文
        RequestContext ctx = RequestContext.getCurrentContext();
        // 获取request
        HttpServletRequest request = ctx.getRequest();
        // 获取token
        String token = CookieUtils.getCookieValue(request, prop.getCookieName());

        try {
            //解析token
            UserInfo user = JwtUtils.getInfoFromToken(token, prop.getPublicKey());
            // 校验权限
        } catch (Exception e) {
            // 解析token 失败，未登录，拦截
            ctx.setSendZuulResponse(false);
            //返回状态码
            ctx.setResponseStatusCode(HttpStatus.SC_UNAUTHORIZED);
            ctx.setResponseBody("request error");
        }

        return null;*/

        return null;
    }
}
