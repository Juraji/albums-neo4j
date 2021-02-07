package nl.juraji.albums.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.ApplicationEventMulticaster
import org.springframework.context.event.SimpleApplicationEventMulticaster
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit

@Configuration
class SpringEventsConfiguration {

    @Bean("applicationEventMulticaster")
    fun simpleApplicationEventMulticaster(): ApplicationEventMulticaster {
        val taskExecutor = ThreadPoolExecutor(
            0, Runtime.getRuntime().availableProcessors() - 1,
            30L, TimeUnit.SECONDS,
            SynchronousQueue()
        )

        val eventMulticaster = SimpleApplicationEventMulticaster()
        eventMulticaster.setTaskExecutor(taskExecutor)
        return eventMulticaster
    }
}
