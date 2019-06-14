package com.pchudzik.blog.example.proxyfactory;

import org.aopalliance.intercept.MethodInterceptor;
import org.junit.Test;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.AopUtils;

import static org.assertj.core.api.Assertions.assertThat;

public class ProxyFactoryLearningTest {
    @Test
    public void proxy_implements_interfaces() {
        ProxyFactory pf = new ProxyFactory();
        pf.setInterfaces(Interface.class, OtherInterface.class);
        pf.addAdvice((MethodInterceptor) invocation -> null);

        Object result = pf.getProxy();

        assertThat(result).isInstanceOf(Interface.class);
        assertThat(result).isInstanceOf(OtherInterface.class);
    }

    @Test
    public void handler_does_work() {
        ProxyFactory pf = new ProxyFactory();
        pf.setInterfaces(Interface.class, OtherInterface.class);
        pf.addAdvice((MethodInterceptor) invocation -> {
            switch (invocation.getMethod().getName()) {
                case "doWork":
                    return "processed " + invocation.getArguments()[0];
                case "process":
                    return (int) invocation.getArguments()[0] + 2;
                default:
                    return invocation.proceed();
            }
        });

        Object result = pf.getProxy();

        assertThat(((Interface) result).doWork(2)).isEqualTo("processed 2");
        assertThat(((OtherInterface) result).process(2)).isEqualTo(4);

        assertThat(result.hashCode()).isInstanceOf(Integer.class);
        assertThat(result.equals(null)).isInstanceOf(Boolean.class);
    }

    @Test
    public void proxy_of_concrete_class_is_created() {
        ProxyFactory pf = new ProxyFactory();
        pf.setTargetClass(ConcreteClass.class);
        pf.addAdvice((MethodInterceptor) invocation -> "concreteWork".equals(invocation.getMethod().getName())
                ? "magic"
                : invocation.proceed());

        Object result = pf.getProxy();

        assertThat(result).isInstanceOf(ConcreteClass.class);
        assertThat(((ConcreteClass) result).concreteWork(123)).isEqualTo("magic");
    }

    @Test
    public void detects_when_object_is_proxy() {
        ProxyFactory jdkProxyFactory = new ProxyFactory();
        jdkProxyFactory.setInterfaces(Interface.class, OtherInterface.class);
        jdkProxyFactory.addAdvice((MethodInterceptor) invocation -> null);

        ProxyFactory cglibProxyFactory = new ProxyFactory();
        cglibProxyFactory.setTargetClass(ConcreteClass.class);


        Object jdkProxy = jdkProxyFactory.getProxy();
        Object cglibProxy = cglibProxyFactory.getProxy();

        assertThat(AopUtils.isAopProxy(new Object())).isFalse();

        assertThat(AopUtils.isAopProxy(jdkProxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(jdkProxy)).isFalse();

        assertThat(AopUtils.isAopProxy(cglibProxy)).isTrue();
        assertThat(AopUtils.isCglibProxy(cglibProxy)).isTrue();
    }

    private interface Interface {
        String doWork(int param);
    }

    private interface OtherInterface {
        int process(int input);
    }

    static class ConcreteClass {
        public String concreteWork(int param) {
            return "asd";
        }
    }
}
