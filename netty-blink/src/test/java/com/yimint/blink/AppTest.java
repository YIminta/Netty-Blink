package com.yimint.blink;

import com.yimint.blink.refletc.ClassScanner;
import com.yimint.blink.route.RouterScanner;
import org.junit.Test;

import java.lang.reflect.Method;
import java.util.Set;

/**
 * Unit test for simple App.
 */
public class AppTest {

    @Test
    public void testApp() {
        Set<Class<?>> classes = ClassScanner.getClasses("com.yimint.blink");
        System.out.println(classes);
    }

    @Test
    public void testRoute() throws Exception {
       //Method routeMethod = RouterScanner.getInstance().getRouteMethod("/home?name=1");

    }
}
