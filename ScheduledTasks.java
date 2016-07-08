import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadFactory;

@Configuration
@EnableScheduling
@Slf4j
public class ScheduledTasks {
    @Autowired
    private Service1 service1;
    @Autowired
    private Service2 service2;
    @Autowired
    private Service3 service3;

    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("xxScheduler-");
        scheduler.setPoolSize(5);
		scheduler.setThreadFactory(threadFactory());
		scheduler.setThreadGroup(new ThreadGroup("xxThreadGroup-"));

        return scheduler;
    }


	@Bean
	public ThreadFactory threadFactory() {
		CustomizableThreadFactory factory = new CustomizableThreadFactory(){
            public Thread createThread(Runnable runnable) {
//                Runnable runnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            runnableOriginal.run();
//                        }
//                        catch(InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }
//                    }
//                };

                Thread thread = new Thread(this.getThreadGroup(), runnable, "xxThread-" + this.nextThreadName());
                thread.setPriority(this.getThreadPriority());
                thread.setDaemon(this.isDaemon());
                return thread;
            }
        };



        //new CustomizableThreadFactory("xxThreadFactory-");
//		factory.ge




		return factory;
	}

    @Scheduled(fixedDelayString = "${fds.delay.value}")
    public void testTask() {
        while(true) {
            String s = "Running testTask9." + Thread.currentThread().getName();
            log.info(s);
            System.out.println(s);
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.warn("Interrupted thread.");
//                throw new RuntimeException("Thread Closed");
            }
        }
    }

//	@Scheduled(fixedDelayString = "${delay.value}")
	public void service1Refresh() {
		service1.fetch();
	}

//	@Scheduled(fixedDelayString = "${delay.value}")
	public void service2Refresh() {
		service2.retrieveAll();
	}

//	@Scheduled(fixedDelayString = "${delay.value}")
	public void service3Refresh() {
		service3.fetch();
	}
}
