package com.examp.redislock.service;

import cn.hutool.core.util.IdUtil;
import com.examp.redislock.mylock.DistributedLockFactory;
import com.examp.redislock.mylock.RedisDistributedLock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * 库存
 *
 * @author liangchuanchuan
 */
@Service
@Slf4j
public class InventoryService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Value("${server.port}")
    private String port;

    Lock lock = new DistributedLockFactory().getDistributedLock("ZOOKEEPER");


    /**
     * V7
     *
     * @return
     */
    public String sale() {
        lock.lock();
        try {
            String retMessage = "";
            //1 查询库存信息
            String result = stringRedisTemplate.opsForValue().get("inventory001");
            //2 判断库存是否足够
            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
            //3 扣减库存
            if (inventoryNumber > 0) {
                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
                retMessage = "成功卖出一个商品，库存剩余: " + inventoryNumber;
                System.out.println(retMessage);
            } else {
                retMessage = "商品卖完了，o(╥﹏╥)o";
            }

            return retMessage + "\t" + "服务端口号：" + port;
        } finally {
            lock.unlock();
        }
    }


    /**
     * V6
     * <p>
     * 让删锁是一个原子操作
     *
     * @return
     */
//    public String sale() {
//        String key = "redisLock_01";
//        String uuid = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
//
//        // jvm 挂起
//        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, 30, TimeUnit.SECONDS)) {
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            String retMessage = "";
//            //1 查询库存信息
//            String result = stringRedisTemplate.opsForValue().get("inventory001");
//            //2 判断库存是否足够
//            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
//            //3 扣减库存
//            if (inventoryNumber > 0) {
//                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
//                retMessage = "成功卖出一个商品，库存剩余: " + inventoryNumber;
//                System.out.println(retMessage);
//            } else {
//                retMessage = "商品卖完了，o(╥﹏╥)o";
//            }
//            return retMessage + "\t" + "服务端口号：" + port;
//        } finally {
//            String script =
//                    "if redis.call('get',KEYS[1]) == ARGV[1] then " +
//                            "return redis.call('del',KEYS[1]) " +
//                            "else " +
//                            "return 0 " +
//                            "end";
//            stringRedisTemplate.execute(new DefaultRedisScript<>(script, Long.class)
//                    , Arrays.asList(key), uuid);
//        }
//    }


    /**
     * V5
     * 删了别人的锁，张冠李戴 没有根本解决
     *
     * @return
     */
//    public String sale() {
//        String key = "redisLock_01";
//        String uuid = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
//
//        // jvm 挂起
//        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, 30, TimeUnit.SECONDS)) {
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        try {
//            String retMessage = "";
//            //1 查询库存信息
//            String result = stringRedisTemplate.opsForValue().get("inventory001");
//            //2 判断库存是否足够
//            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
//            //3 扣减库存
//            if (inventoryNumber > 0) {
//                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
//                retMessage = "成功卖出一个商品，库存剩余: " + inventoryNumber;
//                System.out.println(retMessage);
//            } else {
//                retMessage = "商品卖完了，o(╥﹏╥)o";
//            }
//            return retMessage + "\t" + "服务端口号：" + port;
//        } finally {
//            if (stringRedisTemplate.opsForValue().get(key).equalsIgnoreCase(uuid)) {
//                // 这儿被挂起，或者有业务执行30分钟
//                stringRedisTemplate.delete(key);
//            }
//        }
//    }


    /**
     * V4
     *
     * 删了别人的锁，张冠李戴
     *
     * @return
     */
//    public String sale() {
//        String key = "redisLock_01";
//        String uuid = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
//
//        // jvm 挂起
//        while (!stringRedisTemplate.opsForValue().setIfAbsent(key, uuid, 30, TimeUnit.SECONDS)) {
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//
//        try {
//            String retMessage = "";
//            //1 查询库存信息
//            String result = stringRedisTemplate.opsForValue().get("inventory001");
//            //2 判断库存是否足够
//            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
//            //3 扣减库存
//            if (inventoryNumber > 0) {
//                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
//                retMessage = "成功卖出一个商品，库存剩余: " + inventoryNumber;
//                System.out.println(retMessage);
//            } else {
//                retMessage = "商品卖完了，o(╥﹏╥)o";
//            }
//            return retMessage + "\t" + "服务端口号：" + port;
//        } finally {
//            stringRedisTemplate.delete(key);
//        }
//    }

    /**
     * V3
     * 1、递归，高并发场景禁用。 栈会爆掉
     * 2、如果程序终结挂了，别人永远抢不到锁？
     * 3、虚假唤醒
     *
     * @return
     */
//    public String sale() {
//        String key = "redisLock_01";
//        String uuid = IdUtil.simpleUUID() + ":" + Thread.currentThread().getId();
//
//        Boolean isSuccess = stringRedisTemplate.opsForValue().setIfAbsent(key, uuid);
//        // jvm 挂起
//        if (!isSuccess) {
//            try {
//                Thread.sleep(50);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//            return sale();
//        } else {
//            try {
//                String retMessage = "";
//                //1 查询库存信息
//                String result = stringRedisTemplate.opsForValue().get("inventory001");
//                //2 判断库存是否足够
//                Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
//                //3 扣减库存
//                if (inventoryNumber > 0) {
//                    stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
//                    retMessage = "成功卖出一个商品，库存剩余: " + inventoryNumber;
//                    System.out.println(retMessage);
//                } else {
//                    retMessage = "商品卖完了，o(╥﹏╥)o";
//                }
//                return retMessage + "\t" + "服务端口号：" + port;
//            } finally {
//                stringRedisTemplate.delete(key);
//            }
//        }
//    }


    /**
     * V2 单机加锁
     *
     * @return
     */
//    ReentrantLock lock = new ReentrantLock();
//    public String sale() {
//        lock.lock();
//        try {
//            String retMessage = "";
//            //1 查询库存信息
//            String result = stringRedisTemplate.opsForValue().get("inventory001");
//            //2 判断库存是否足够
//            Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
//            //3 扣减库存
//            if (inventoryNumber > 0) {
//                stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
//                retMessage = "成功卖出一个商品，库存剩余: " + inventoryNumber;
//                System.out.println(retMessage);
//            } else {
//                retMessage = "商品卖完了，o(╥﹏╥)o";
//            }
//            return retMessage + "\t" + "服务端口号：" + port;
//        } finally {
//            lock.unlock();
//        }
//    }

    /**
     * V1
     * @return
     */
//    public String sale() {
//        String retMessage = "";
//        //1 查询库存信息
//        String result = stringRedisTemplate.opsForValue().get("inventory001");
//        //2 判断库存是否足够
//        Integer inventoryNumber = result == null ? 0 : Integer.parseInt(result);
//        //3 扣减库存
//        if (inventoryNumber > 0) {
//            stringRedisTemplate.opsForValue().set("inventory001", String.valueOf(--inventoryNumber));
//            retMessage = "成功卖出一个商品，库存剩余: " + inventoryNumber;
//            System.out.println(retMessage);
//        } else {
//            retMessage = "商品卖完了，o(╥﹏╥)o";
//        }
//        return retMessage + "\t" + "服务端口号：" + port;
//    }


}


