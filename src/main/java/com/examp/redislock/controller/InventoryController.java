package com.examp.redislock.controller;

import com.examp.redislock.service.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 超卖
 *
 * 比如只有100件衣服，我们要精准卖100件
 */
@RestController
@Api(tags = "redis分布式锁测试")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @ApiOperation("扣减库存，一次卖一个")
    @GetMapping(value = "/inventory/sale")
    public String sale() {
        return inventoryService.sale();
    }

}
