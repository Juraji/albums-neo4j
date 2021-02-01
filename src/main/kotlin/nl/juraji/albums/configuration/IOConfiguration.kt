package nl.juraji.albums.configuration

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import reactor.core.scheduler.Scheduler
import reactor.core.scheduler.Schedulers

@Configuration
class IOConfiguration {

    @Bean("nioFileOperationsScheduler")
    fun nioFileOperationsScheduler(): Scheduler = Schedulers
        .newBoundedElastic(
            Schedulers.DEFAULT_BOUNDED_ELASTIC_SIZE,
            Schedulers.DEFAULT_BOUNDED_ELASTIC_QUEUESIZE,
            "nio-file-operations",
            60,
            true
        )
}
