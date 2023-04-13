package net.mingsoft.config;

import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @className: MybatisPlusConfig
 * @description: mybatis-plus配置
 * @author: 赖军宝
 * @create: 2018-08-10 10:25
 **/
@Configuration
public class MybatisPlusConfig {

    private final Logger logger = LoggerFactory.getLogger(MybatisPlusConfig.class);

    /**
     * @Description:  mybatis-plus分页插件，使用物理分页（默认内存分页）
     * @Param:
     * @return:
     * @Author: 赖军宝
     * @Date: 2018-08-10 10:35
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        PaginationInterceptor page = new PaginationInterceptor();
        page.setDialectType("mysql");
        logger.info("已配置mybatis-plus分页插件，使用物理分页");
        return page;
    }
}
