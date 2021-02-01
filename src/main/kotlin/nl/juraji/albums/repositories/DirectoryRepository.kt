package nl.juraji.albums.repositories

import nl.juraji.albums.model.Directory
import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository
import org.springframework.stereotype.Repository

@Repository
interface DirectoryRepository: ReactiveNeo4jRepository<Directory, Long>
