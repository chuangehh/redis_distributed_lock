package com.examp.redislock.mylock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;

/**
 * 分布式锁简单工厂
 *
 * @author liangchuanchuan
 */
@Component
public class DistributedLockFactory {


    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    private String lockName;


    public Lock getDistributedLock(String lockType) {
        if (lockType == null) return null;

        if (lockType.equalsIgnoreCase("REDIS")) {
            lockName = "redisLock";
            return new RedisDistributedLock(stringRedisTemplate, lockName);
        } else if (lockType.equalsIgnoreCase("ZOOKEEPER")) {
            //TODO zookeeper版本的分布式锁实现
            return new ZookeeperDistributedLock();
        } else if (lockType.equalsIgnoreCase("MYSQL")) {
            //TODO mysql版本的分布式锁实现
            return null;
        }

        return null;
    }

}
