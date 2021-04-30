//package com.user.service;
//
//import com.alibaba.fastjson.JSON;
//import com.sun.deploy.association.utility.AppConstants;
//import com.user.bean.SerResponse;
//import com.user.common.UserAuth;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cloud.gateway.filter.GatewayFilterChain;
//import org.springframework.cloud.gateway.filter.GlobalFilter;
//import org.springframework.core.Ordered;
//import org.springframework.core.io.buffer.DataBuffer;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.server.reactive.ServerHttpRequest;
//import org.springframework.http.server.reactive.ServerHttpResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//
//import java.io.UnsupportedEncodingException;
//import java.util.Arrays;
//
///**
// * @author wzyang
// */
//@Component
//@Slf4j
//public class TestGlobalFilter implements GlobalFilter, Ordered {
//
//    //@Autowired
//    //private StringRedisTemplate stringRedisTemplate;
//
//    //指定无需拦截的请求 不需要验证的请求
//    private static final String[] WHITE_LIST = {"/auth/login", "/user/register"};
//
//
//    @Override
//    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
//        //获取请求信息
//        ServerHttpRequest serverHttpRequest = exchange.getRequest();
//        //获取请求URL
//        String url = serverHttpRequest.getURI().getPath();
//
//        log.info("url:{}", url);
//        //判断是否是需要拦截
//        if (Arrays.asList(WHITE_LIST).contains(url)) {
//            return chain.filter(exchange);
//        }
//        //获取token
//        String token = serverHttpRequest.getHeaders().getFirst("token");
//
//        if (StringUtils.isEmpty(token)) {
//            return setResponse(exchange, "token 不存在");
//        }
//
//        //String redisToken = stringRedisTemplate.opsForValue().get(UserAuth.AUTH_KEY);
//
//        //if (StringUtils.isBlank(redisToken)) {
//        //return setResponse(exchange, "token 鉴权失败！");
//        //}
//
//        log.info("鉴权完成！");
//
//        return chain.filter(exchange);
//    }
//
//
//    /**
//     * 设置 拦截返回信息
//     *
//     * @param exchange
//     * @param msg
//     * @return
//     */
//    private Mono<Void> setResponse(ServerWebExchange exchange, String msg) {
//        ServerHttpResponse originalResponse = exchange.getResponse();
//        originalResponse.setStatusCode(HttpStatus.UNAUTHORIZED);
//        originalResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
//        byte[] response = null;
//        try {
//            log.info("token已失效");
//            response = JSON.toJSONString(SerResponse.error(msg)).getBytes("");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        DataBuffer buffer = originalResponse.bufferFactory().wrap(response);
//        return originalResponse.writeWith(Flux.just(buffer));
//    }
//
//
//    @Override
//    public int getOrder() {
//        return -1;
//    }
//}
