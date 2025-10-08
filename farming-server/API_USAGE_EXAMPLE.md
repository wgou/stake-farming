# 阶梯式奖励活动 API 使用示例

## 1. 创建阶梯式奖励活动

### 请求
```http
POST /admin/activity/save
Content-Type: application/json

{
  "wallet": "0x1234567890abcdef1234567890abcdef12345678",
  "endDate": "2024-12-31 23:59:59",
  "levels": [
    {
      "levelOrder": 1,
      "targetAmount": 1000.00,
      "rewardEth": 0.1,
      "description": "达到1000 USDC奖励0.1 ETH"
    },
    {
      "levelOrder": 2,
      "targetAmount": 2000.00,
      "rewardEth": 0.2,
      "description": "达到2000 USDC奖励0.2 ETH"
    },
    {
      "levelOrder": 3,
      "targetAmount": 5000.00,
      "rewardEth": 0.5,
      "description": "达到5000 USDC奖励0.5 ETH"
    }
  ]
}
```

### 响应
```json
{
  "code": 0,
  "msg": "success"
}
```

## 2. 更新阶梯式奖励活动

### 请求
```http
POST /admin/activity/update
Content-Type: application/json

{
  "id": 1,
  "endDate": "2024-12-31 23:59:59",
  "status": 1,
  "levels": [
    {
      "id": 1,
      "levelOrder": 1,
      "targetAmount": 1500.00,
      "rewardEth": 0.15,
      "description": "达到1500 USDC奖励0.15 ETH"
    },
    {
      "levelOrder": 2,
      "targetAmount": 3000.00,
      "rewardEth": 0.3,
      "description": "达到3000 USDC奖励0.3 ETH"
    }
  ]
}
```

## 3. 获取活动详情（包含级别信息）

### 请求
```http
POST /admin/activity/get
Content-Type: application/json

{
  "id": 1
}
```

### 响应
```json
{
  "code": 0,
  "msg": "success",
  "data": {
    "id": 1,
    "wallet": "0x1234567890abcdef1234567890abcdef12345678",
    "standard": 1000.00,
    "eth": 0.5,
    "applyDate": null,
    "endDate": "2024-12-31 23:59:59",
    "rewardDate": null,
    "status": 0,
    "poolsId": 1,
    "levels": [
      {
        "id": 1,
        "activityId": 1,
        "levelOrder": 1,
        "targetAmount": 1000.00,
        "rewardEth": 0.1,
        "description": "达到1000 USDC奖励0.1 ETH",
        "completed": 0
      },
      {
        "id": 2,
        "activityId": 1,
        "levelOrder": 2,
        "targetAmount": 2000.00,
        "rewardEth": 0.2,
        "description": "达到2000 USDC奖励0.2 ETH",
        "completed": 0
      },
      {
        "id": 3,
        "activityId": 1,
        "levelOrder": 3,
        "targetAmount": 5000.00,
        "rewardEth": 0.5,
        "description": "达到5000 USDC奖励0.5 ETH",
        "completed": 0
      }
    ]
  }
}
```

## 4. 删除活动（会自动删除所有级别）

### 请求
```http
POST /admin/activity/delete
Content-Type: application/json

{
  "id": 1
}
```

## 数据库表结构

### s_activity 表（原有表结构保持不变，兼容性）
- 保留 `standard` 和 `eth` 字段作为兼容性字段
- `standard` 存储最低级别的目标金额
- `eth` 存储最高级别的奖励ETH

### s_activity_level 表（新增）
```sql
CREATE TABLE `s_activity_level` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `activity_id` bigint(20) NOT NULL COMMENT '活动ID',
  `level_order` int(11) NOT NULL COMMENT '级别序号（用于排序）',
  `target_amount` decimal(20,8) NOT NULL COMMENT '目标金额（USDC额度）',
  `reward_eth` decimal(20,8) NOT NULL COMMENT '奖励ETH数量',
  `description` varchar(255) DEFAULT NULL COMMENT '级别描述',
  `completed` tinyint(1) DEFAULT '0' COMMENT '是否已完成该级别 0：未完成 1：已完成',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `pools_id` bigint(20) DEFAULT NULL COMMENT '资金池ID',
  PRIMARY KEY (`id`),
  KEY `idx_activity_id` (`activity_id`),
  KEY `idx_level_order` (`level_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动级别表-阶梯式奖励';
```

## 主要特性

1. **阶梯式奖励**: 一个活动可以设置多个奖励级别
2. **灵活配置**: 每个级别可以设置不同的目标金额和奖励
3. **兼容性**: 保留原有API结构，新老版本可以并存
4. **级别管理**: 支持添加、修改、删除级别
5. **状态追踪**: 每个级别可以单独标记完成状态 