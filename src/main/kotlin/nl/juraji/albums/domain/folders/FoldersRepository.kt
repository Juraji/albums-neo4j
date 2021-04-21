package nl.juraji.albums.domain.folders

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface FoldersRepository : ReactiveNeo4jRepository<Folder, String> {

    @Query(
        """
        MATCH (parent:Folder {id: $ parentId})
        MATCH (child:Folder {id: $ folderId})

        OPTIONAL MATCH (child)<-[oldParent:HAS_CHILD]-()
        DELETE oldParent
        
        MERGE (parent)-[:HAS_CHILD]->(child)
        RETURN parent
    """
    )
    fun setParent(folderId: String, parentId: String): Mono<Folder>

    @Query("MATCH (f:Folder) WHERE NOT exists(()-[:HAS_CHILD]->(f)) RETURN f")
    fun findRoots(): Flux<Folder>

    @Query("MATCH (:Folder {id: $ folderId})-[:HAS_CHILD]->(f:Folder) RETURN f")
    fun findChildren(folderId: String): Flux<Folder>

    @Query("MATCH (f:Folder)-[:HAS_PICTURE]->(:Picture {id: $ pictureId}) RETURN f")
    fun findByPictureId(pictureId: String): Mono<Folder>

    @Query("RETURN NOT exists((:Folder {id: $ folderId})-[:HAS_CHILD|HAS_PICTURE]->())")
    fun isEmptyById(folderId: String): Mono<Boolean>

    @Query("MATCH p=(:Folder {id: $ folderId})-[:HAS_CHILD|HAS_PICTURE*0..]->() DETACH DELETE p")
    fun deleteRecursivelyById(folderId: String): Mono<Void>
}
