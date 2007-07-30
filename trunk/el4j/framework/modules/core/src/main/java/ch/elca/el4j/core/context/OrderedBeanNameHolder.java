package ch.elca.el4j.core.context;

import org.springframework.core.Ordered;

public class OrderedBeanNameHolder implements Ordered {
    
    protected int order;
    protected String beanName;
    
    public OrderedBeanNameHolder(int order, String beanName) {
        this.order = order;
        this.beanName = beanName;
    }
    
    public int getOrder() {
        return order;
    }
    
    public String getBeanName() {
        return beanName;
    }
    
}

