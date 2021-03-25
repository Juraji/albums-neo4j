package nl.juraji.albums.domain.tags

import org.springframework.data.neo4j.repository.ReactiveNeo4jRepository

interface TagRepository : ReactiveNeo4jRepository<Tag, String>
