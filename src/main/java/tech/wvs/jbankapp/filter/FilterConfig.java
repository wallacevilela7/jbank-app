package tech.wvs.jbankapp.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    private final IpFilter ipfilter;

    public FilterConfig(IpFilter ipfilter) {
        this.ipfilter = ipfilter;
    }

    @Bean
    public FilterRegistrationBean<IpFilter> filterRegistrationBean() {
        var filterRegistrationBean = new FilterRegistrationBean<IpFilter>();

        filterRegistrationBean.setFilter(ipfilter);
        filterRegistrationBean.setOrder(0);

        return filterRegistrationBean;
    }
}
