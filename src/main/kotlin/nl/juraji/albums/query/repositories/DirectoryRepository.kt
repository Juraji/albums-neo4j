package nl.juraji.albums.query.repositories

import nl.juraji.albums.query.dto.Directory
import nl.juraji.albums.util.Neo4jDtoMapper
import org.neo4j.driver.Driver
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class DirectoryRepository(driver: Driver) : Repository(driver) {
    private val directoryMapper = Neo4jDtoMapper(Directory::class)

    fun findAll(): Flux<Directory> = readMultiple(
        """
            MATCH (n:Directory)
            RETURN n
            ORDER BY n.location
        """
    ).map(directoryMapper::recordToDto)
}
