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

    @Query("MATCH (p:Directory {id: $ id}) MATCH (c:Directory {id: $ childId}) MERGE (p)-[:PARENT_OF]->(c)")
    fun addChild(id: String, childId: String): Mono<Void>

    @Query(
        """
        MATCH (root:Directory  {id: $ id})
        
        OPTIONAL MATCH (root)-[:PARENT_OF*0..]->(child:Directory)
        OPTIONAL MATCH (rootPic:Picture)-[:LOCATED_IN]->(root)
        OPTIONAL MATCH (rootPic)<-[:DESCRIBES|HAS_SOURCE|HAS_TARGET]-(rootPicMeta)

        OPTIONAL MATCH (childPic:Picture)-[:LOCATED_IN]->(child)
        OPTIONAL MATCH (childPic)<-[:DESCRIBES|HAS_SOURCE|HAS_TARGET]-(childPicMeta)

        DETACH DELETE root, rootPic, rootPicMeta, child, childPic, childPicMeta
        """
    )
    fun deleteTreeById(id: String): Mono<Void>
}
