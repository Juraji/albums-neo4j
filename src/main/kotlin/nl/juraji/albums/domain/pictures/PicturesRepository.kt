package nl.juraji.albums.domain.pictures

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PicturesRepository : ReactiveNeo4jRepository<Picture, String> {

    @Query("MATCH (:Folder {id: $ folderId})-[:HAS_PICTURE]->(p:Picture) RETURN p")
    fun findAllByFolderId(folderId: String): Flux<Picture>

    @Query(
        """
            MATCH (f:Folder {id: $ folderId})
            MATCH (p:Picture {id: $ pictureId})
            
            OPTIONAL MATCH (:Folder)-[oldRel:HAS_PICTURE]->(p)
            DELETE oldRel
            
            MERGE (f)-[:HAS_PICTURE]->(p)
            RETURN p
        """
    )
    fun addPictureToFolder(folderId: String, pictureId: String): Mono<Picture>

    @Suppress("SpringDataRepositoryMethodReturnTypeInspection")
    @Query(
        "RETURN exists((:Folder {id: ${'$'} folderId})-[:HAS_PICTURE]->(:Picture {name: $ name}))",
        exists = true
    )
    fun existsByNameInFolder(folderId: String, name: String): Mono<Boolean>

    @Query("MATCH p=(:Picture {id: $ id})<-[:DESCRIBES]-(:PictureHash) DETACH DELETE p")
    fun deleteFully(id: String): Mono<Void>
}
