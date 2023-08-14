package com.help.hyozason_backend.security.redis;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;


import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;



@Component
@AllArgsConstructor
public class RedisUtil {

    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisTemplate<String, Object> redisBlackListTemplate;



    public void set(String key, Object o, int minutes){
       redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
        redisTemplate.opsForValue().set(key,o,minutes, TimeUnit.MINUTES);
    }

    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }

    public boolean delete(String key){
        return redisTemplate.delete(key);
    }

    public boolean hasKey(String key){
        System.out.println("이건가");
        return redisTemplate.hasKey(key);
    }

    public void setBlackList(String key,Object o, Long milliSeconds){
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer(o.getClass()));
        redisBlackListTemplate.opsForValue().set(key,o,milliSeconds,TimeUnit.MILLISECONDS);
    }

    public Object getBlackList(String key){
        return redisBlackListTemplate.opsForValue().get(key);
    }

    public boolean deleteBlackList(String key){
        return redisBlackListTemplate.delete(key);
    }

    public boolean hasKeyBlackList(String key){
        System.out.print(key);
        return redisBlackListTemplate.hasKey(key);
    }



}
