package nl.juraji.albums.domain.folders

import nl.juraji.albums.domain.pictures.Picture
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.data.neo4j.repository.query.Query
import reactor.core.publisher.Mono

interface FolderPicturesRepository: ReactiveNeo4jRepository<Picture, String> {

    @Query("""
        MATCH (f:Folder {id: $ folderId})
        MATCH (p:Picture {id: $ pictureId})
        
        MERGE (f)-[:HAS_PICTURE]->(p)
        RETURN p
    """)
    fun addPictureToFolder(folderId: String, pictureId:String): Mono<Picture>
}
