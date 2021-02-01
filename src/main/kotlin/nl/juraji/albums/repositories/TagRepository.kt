package nl.juraji.albums.repositories

import nl.juraji.albums.model.Tag
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface TagRepository: ReactiveNeo4jRepository<Tag, Long>
