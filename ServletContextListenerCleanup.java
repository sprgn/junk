import ch.qos.logback.classic.LoggerContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.cloud.commons.util.InetUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;
//import org.springframework.web.context.WebApplicationContext;
//import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.Closeable;
import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;

//@Component
@WebListener
@Slf4j
public class ServletContextListenerCleanup implements ServletContextListener, ApplicationContextAware {

    public static ApplicationContext ctx;

    public void contextDestroyed(ServletContextEvent sce)  {
//        clearInetThread();
        shutdownLogs();
    }

//        private void clearInetThread(){
//        //https://github.com/spring-cloud/spring-cloud-netflix/issues/491
//        //
//        try {
//            log.info("SHUTTING DOWN: InetUtils thread in contextDestroyed");
//            Field field = InetUtils.class.getDeclaredField("executorService");
//            field.setAccessible(true);  //get a hold of static
//            Object es = field.get(null);
//            ((ExecutorService)es).shutdown();
////            for (String beanName: ctx.getBeansOfType(Closeable.class).keySet()) {
////                try {
////                    Closeable c = (Closeable) ctx.getBean(beanName);
////                    log.info("SHUTTING DOWN: closing " + beanName + c.getClass());
////                    c.close();
////                } catch (Exception e){
////                    log.warn("Unable to close().  " + e.getMessage(), e);
////                }
////            }
//        } catch (Exception e) {
//            log.warn("Unable to clear InetUtils thread.  " + e.getMessage(), e);
//        }
//    }

    public void contextInitialized(ServletContextEvent sce) {
        log.info("Servlet Context initialized.");
    }

    private void shutdownLogs() {
        log.info("SHUTTING DOWN: loggerContext in contextDestroyed");
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        context.stop();
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }
}
