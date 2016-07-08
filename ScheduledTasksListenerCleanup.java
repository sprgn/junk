import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ScheduledTasksListenerCleanup implements ApplicationListener<ContextClosedEvent>, ApplicationContextAware {

    private ApplicationContext ctx;

//    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.ctx = applicationContext;
    }

//    @Override
    public void onApplicationEvent(ContextClosedEvent contextClosedEvent) {
        shutdownScheduledThreads();
    }

	private void shutdownScheduledThreads(){

		try {
			log.info("Shutting down scheduler.");
            ThreadPoolTaskScheduler scheduler = ctx.getBean(ThreadPoolTaskScheduler.class);
            if(scheduler != null) {
                log.info("Shutting down scheduler's running threads via threadGroup BEGIN.  activeCount(): " + scheduler.getThreadGroup().activeCount() + " activeGroupCount(): " + scheduler.getThreadGroup().activeGroupCount());
                scheduler.getThreadGroup().interrupt();
                scheduler.getThreadGroup().destroy();
                log.info("Shutting down scheduler's running threads via threadGroup END.  activeCount(): " + scheduler.getThreadGroup().activeCount() + " activeGroupCount(): " + scheduler.getThreadGroup().activeGroupCount());

                log.info("Shutting down scheduler's executor.");
//                scheduler.getScheduledExecutor().shutdown();
                scheduler.getScheduledExecutor().shutdownNow();
                scheduler.shutdown();

                log.info("done.  Active count: " + scheduler.getActiveCount());
            }
        } catch (NoSuchBeanDefinitionException e){
            log.warn("Bean ThreadPoolTaskScheduler not found.");
		} catch (Exception e){
			log.error(e.getMessage(), e);
		}


		try {
            log.info("Shutting down executor if one is still available.");
            ThreadPoolTaskExecutor executor = ctx.getBean(ThreadPoolTaskExecutor.class);
			if(executor != null) {
                executor.shutdown();
                executor.getThreadPoolExecutor().shutdownNow();
                log.info("done.");
            }
        } catch (NoSuchBeanDefinitionException e){
            log.warn("Bean ThreadPoolTaskExecutor not found.");
		} catch (Exception e){
			log.error(e.getMessage(), e);
		}
	}

}
