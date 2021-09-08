package com.tsinghua.kgeducator.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class WebService {
    private String id;
    @Resource
    public RestTemplate restTemplate;
    @PostConstruct
    public void initRestTemplate()
    {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
        MultiValueMap<String, Object> postParameters = new LinkedMultiValueMap<>();
        postParameters.add("password", "Syt20001121");
        postParameters.add("phone", "18611157300");
        HttpEntity<String> request = new HttpEntity(postParameters, headers);
        String url = "http://open.edukg.cn/opedukg/api/typeAuth/user/login";
        ResponseEntity<String> postForEntity = restTemplate.postForEntity(url, request, String.class);
        HashMap<String, String> body = JSON.parseObject(postForEntity.getBody(),new TypeReference<HashMap<String, String>>(){});
        id = body.get("id");
    }

    public String getId() {
        return id;
    }
}
