package com.walkingcoding.framework.easy4j.core.config;

import com.alibaba.druid.support.http.StatViewServlet;
import com.alibaba.druid.support.http.WebStatFilter;
import com.walkingcoding.framework.easy4j.core.properties.DruidMonitorProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * druid配置
 *
 * @author songhuiqing
 */
@Configuration
public class DruidConfiguration {

    @Autowired
    private DruidMonitorProperties druidMonitorProperties;

    @Bean
    @ConditionalOnProperty(prefix = "easy4j.druid.monitor", name = "enabled", havingValue = "true")
    public ServletRegistrationBean statViewServlet() {
        assertDruidMonitorProperties();
        ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new StatViewServlet(), druidMonitorProperties.getUrlMapping());
        if (!StringUtils.isEmpty(druidMonitorProperties.getAllow())) {
            servletRegistrationBean.addInitParameter("allow", druidMonitorProperties.getAllow());
        }
        if (!StringUtils.isEmpty(druidMonitorProperties.getDeny())) {
            servletRegistrationBean.addInitParameter("deny", druidMonitorProperties.getDeny());
        }
        servletRegistrationBean.addInitParameter("loginUsername", druidMonitorProperties.getUsername());
        servletRegistrationBean.addInitParameter("loginPassword", druidMonitorProperties.getPassword());
        servletRegistrationBean.addInitParameter("resetEnable", String.valueOf(druidMonitorProperties.getResetEnabled()));
        return servletRegistrationBean;
    }

    /**
     * druid 过滤器
     *
     * @return
     */
    @Bean
    @ConditionalOnProperty(prefix = "easy4j.druid.monitor", name = "enabled", havingValue = "true")
    public FilterRegistrationBean druidFilter() {
        assertDruidMonitorProperties();
        FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean(new WebStatFilter());
        //设置过滤器过滤路径
        filterRegistrationBean.addUrlPatterns("/*");
        //忽略过滤的形式
        filterRegistrationBean.addInitParameter("exclusions", "*.js,*.gif,*.jpg,*.png,*.css,*.ico," + druidMonitorProperties.getUrlMapping());
        return filterRegistrationBean;

    }

    private void assertDruidMonitorProperties() {
        if (druidMonitorProperties.getEnabled()) {
            Assert.notNull(druidMonitorProperties.getUsername(), "enabled=true, username 不能为空");
            Assert.notNull(druidMonitorProperties.getPassword(), "enabled=true, password 不能为空");
            Assert.notNull(druidMonitorProperties.getResetEnabled(), "enabled=true, resetEnabled 不能为空");
            Assert.notNull(druidMonitorProperties.getUrlMapping(), "enabled=true, urlMapping 不能为空");
        }
    }
}
