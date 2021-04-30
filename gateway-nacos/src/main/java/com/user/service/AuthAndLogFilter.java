package com.user.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.common.utils.MD5Utils;
import com.alibaba.nacos.common.utils.MapUtils;
import com.user.bean.SerResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reactivestreams.Publisher;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Component
public class AuthAndLogFilter implements GlobalFilter, Ordered {

    static final Logger logger = LogManager.getLogger(AuthAndLogFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

            ServerHttpRequest serverHttpRequest = exchange.getRequest();
        ServerHttpResponse serverHttpResponse = exchange.getResponse();

        StringBuilder logBuilder = new StringBuilder();
        //获取所有的请求参数
        Map<String, String> params = parseRequest(exchange, logBuilder);
        boolean tokenFlag = false;
        if (MapUtils.isNotEmpty(params)) {
            //判断是否满足鉴权要求
            tokenFlag = checkSignature(params, serverHttpRequest);
        }
        //String redictUrl = "http://news.baidu.com/guonei";
        if (!tokenFlag) {
            Map map = new HashMap<>();
            SerResponse response = SerResponse.error("token验证失败");
            String resp = JSON.toJSONString(response);
            logBuilder.append(",resp=").append(resp);
            logger.info(logBuilder.toString());
            DataBuffer bodyDataBuffer = serverHttpResponse.bufferFactory().wrap(resp.getBytes());
            serverHttpResponse.getHeaders().add("Content-Type", "text/plain;charset=UTF-8");
            //serverHttpResponse.getHeaders().add(HttpHeaders.LOCATION,redictUrl);
            //return serverHttpResponse.setComplete();
            return serverHttpResponse.writeWith(Mono.just(bodyDataBuffer));
        }
        DataBufferFactory bufferFactory = serverHttpResponse.bufferFactory();
        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(serverHttpResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.map(dataBuffer -> {
                        byte[] content = new byte[dataBuffer.readableByteCount()];
                        dataBuffer.read(content);
                        DataBufferUtils.release(dataBuffer);
                        String resp = new String(content, Charset.forName("UTF-8"));
                        logBuilder.append(",resp=").append(resp);
                        logger.info(logBuilder.toString());
                        byte[] uppedContent = new String(content, Charset.forName("UTF-8")).getBytes();
                        return bufferFactory.wrap(uppedContent);
                    }));
                }
                return super.writeWith(body);
            }
        };
        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private Map<String, String> parseRequest(ServerWebExchange exchange, StringBuilder logBuilder) {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        String method = serverHttpRequest.getMethodValue().toUpperCase();
        logBuilder.append(method).append(",").append(serverHttpRequest.getURI());
        MultiValueMap<String, String> query = serverHttpRequest.getQueryParams();
        Map<String, String> params = new HashMap<>();
        query.forEach((k, v) -> {
            params.put(k, v.get(0));
        });
        if ("POST".equals(method)) {
            String body = exchange.getAttributeOrDefault("cachedRequestBody", "");
            if (StringUtils.isNotBlank(body)) {
                logBuilder.append(",body=").append(body);
                String[] kvArray = body.split("&");
                for (String kv : kvArray) {
                    if (kv.indexOf("=") >= 0) {
                        String k = kv.split("=")[0];
                        String v = kv.split("=")[1];
                        if (!params.containsKey(k)) {
                            try {
                                params.put(k, URLDecoder.decode(v, "UTF-8"));
                            } catch (UnsupportedEncodingException e) {
                            }
                        }
                    }
                }
            }
        }
        return params;
    }

    /**
     * 判断是否满足要求，token是否正确
     *
     * @param params
     * @param serverHttpRequest
     * @return
     */
    private boolean checkSignature(Map<String, String> params, ServerHttpRequest serverHttpRequest) {
        String token = params.get("token");
        String idno = params.get("idno");
        if (StringUtils.isBlank(token) || StringUtils.isBlank(idno)) {
            return false;
        }
        String tempToken = MD5Utils.md5Hex(idno, "UTF-8");
        if (!token.equals(tempToken)) {
            return false;
        }
        return true;
    }

    @Override
    public int getOrder() {
        return -20;
    }
}
