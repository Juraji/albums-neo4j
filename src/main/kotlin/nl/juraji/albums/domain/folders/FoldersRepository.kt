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

    @Query(
        """
        MATCH (f:Folder)
          WHERE NOT (f)<-[:HAS_CHILD]-()
        RETURN f ORDER BY f.name
    """
    )
    fun findRoots(): Flux<Folder>

    @Query(
        """
        MATCH (f:Folder)<-[:HAS_CHILD]-(:Folder {id: $ folderId})
        RETURN f ORDER BY f. name
    """
    )
    fun findChildren(folderId: String): Flux<Folder>

    @Query(
        """
        OPTIONAL MATCH (f:Folder {id: $ folderId})-[r:HAS_CHILD|HAS_PICTURE]->()
        RETURN r IS NULL
    """
    )
    fun isEmptyById(folderId: String): Mono<Boolean>
}
