package nl.juraji.albums.repositories

import nl.juraji.albums.model.Picture
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface PictureRepository: ReactiveNeo4jRepository<Picture, Long>
