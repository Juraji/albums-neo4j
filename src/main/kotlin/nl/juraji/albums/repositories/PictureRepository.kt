package nl.juraji.albums.repositories

import nl.juraji.albums.model.Picture
import nl.juraji.albums.model.PictureDescription
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PictureRepository : ReactiveNeo4jRepository<Picture, String> {
    fun existsByLocation(location: String): Mono<Boolean>
    fun findDescriptionById(id: String): Mono<PictureDescription>
    @Query("MATCH (p:Picture) RETURN p")
    fun findAllDescriptions(): Flux<PictureDescription>
}
