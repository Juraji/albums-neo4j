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
        
        MERGE (parent)-[:HAS_CHILD]->(child)
        RETURN child
    """
    )
    fun setParent(folderId: String, parentId: String): Mono<Folder>

    @Query(
        """
        MATCH (f:Folder)
          WHERE NOT (f)<-[:HAS_CHILD]-()
        RETURN f
    """
    )
    fun getRoots(): Flux<Folder>
}
