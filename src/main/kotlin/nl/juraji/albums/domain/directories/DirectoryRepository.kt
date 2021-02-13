package nl.juraji.albums.domain.directories

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface DirectoryRepository : ReactiveNeo4jRepository<Directory, String> {

    fun existsByLocation(location: String): Mono<Boolean>

    fun findByLocation(location: String): Mono<Directory>

    fun findByLocationStartingWith(location: String): Flux<Directory>

    @Query("MATCH (d:Directory) WHERE NOT (d)<-[:PARENT_OF]-() RETURN d")
    fun findRoots(): Flux<Directory>

    @Query("MATCH (p:Directory {id: $ id}) MATCH (c:Directory {id: $ childId}) MERGE (p)-[:PARENT_OF]->(c)")
    fun addChild(id: String, childId: String): Mono<Void>

    @Query(
        """
        MATCH (root:Directory  {id: $ id})
        OPTIONAL MATCH t=(root)-[:PARENT_OF*0..]->()<-[:LOCATED_IN*0..]-()<-[:DESCRIBES|HAS_TARGET|HAS_SOURCE*0..]-()
        DETACH DELETE t,root
        """, delete = true
    )
    fun deleteTreeById(id: String): Mono<Void>
}
