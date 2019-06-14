package com.pchudzik.blog.example.proxyfactory;

import org.aopalliance.intercept.ConstructorInterceptor;
import org.aopalliance.intercept.ConstructorInvocation;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class ProxyFactoryApplication {

    public static void main(String[] args) throws Exception {
        ProxyFactory pf = new ProxyFactory();
        pf.addInterface(Worker.class);
        pf.addAdvice(new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                if (invocation.getMethod().getName().equals("doWork")) {
                    return 42;
                } else {
                    return invocation.proceed();
                }
            }
        });

        final Worker w = (Worker) pf.getProxy();
        System.out.println(w.doWork("aaa"));
    }

    interface Worker {
        int doWork(String input);
    }

    static class ConcreteWorker implements Worker {

        @Override
        public int doWork(String input) {
            return 23;
        }
    }
}
