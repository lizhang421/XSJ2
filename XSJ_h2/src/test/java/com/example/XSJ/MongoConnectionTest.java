
package com.example.XSJ.Controller.Ai;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.bson.Document;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class MongoConnectionTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    public void testMongoOperation() {
        try {
            // 1. 打印连接的数据库名
            String dbName = mongoTemplate.getDb().getName();
            System.out.println("当前连接的 MongoDB 数据库：" + dbName);
            assertEquals("ceshi", dbName, "数据库名称应该是 ceshi");

            // 2. 插入一条测试数据
            Document testDoc = new Document("test_key", "test_value")
                    .append("timestamp", System.currentTimeMillis());
            mongoTemplate.insert(testDoc, "test_collection");
            System.out.println("测试数据插入成功！");

            // 3. 查询集合中的所有数据
            Iterable<Document> results = mongoTemplate.findAll(Document.class, "test_collection");
            int count = 0;
            for (Document doc : results) {
                count++;
                System.out.println("查询到的数据：" + doc.toJson());
            }

            assertTrue(count > 0, "应该至少有一条数据");
            System.out.println("✅ MongoDB 配置 + 连接 + 操作 全部成功！共查询到 " + count + " 条数据");
        } catch (Exception e) {
            e.printStackTrace();
            fail("❌ MongoDB 操作失败：" + e.getMessage());
        }
    }
}