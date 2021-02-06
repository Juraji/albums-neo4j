package nl.juraji.albums.configurations

import org.neo4j.driver.AuthTokens
import org.neo4j.driver.Driver
import org.neo4j.driver.GraphDatabase
import org.neo4j.harness.Neo4j
import org.neo4j.harness.Neo4jBuilders
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@TestConfiguration
class TestNeo4jFixtureConfiguration {


    @Bean
    fun driver(): Driver = GraphDatabase.driver(
        neo4j().boltURI(),
        AuthTokens.basic("neo4j", "")
    )

    @Bean
    fun neo4j(): Neo4j = Neo4jBuilders.newInProcessBuilder()
        .withDisabledServer()
        .withFixture(
            // language=cypher
            """
                CREATE(:Directory {
                  id: 'd1',
                  location: 'F:\Desktop\TESTMAP'
                })
                
               CREATE (p1:Picture {
                 fileSize: 64367,
                 fileType: 'JPEG',
                 id: 'p1',
                 lastModified: '2020-05-16T10:17:50',
                 location: 'F:\Desktop\TESTMAP\DA37o272cCU.jpg',
                 name: 'DA37o272cCU.jpg'
               })

               CREATE (p2:Picture {
                 fileSize: 916566,
                 fileType: 'BMP',
                 id: 'p2',
                 lastModified: '2020-05-16T11:00:50',
                 location: 'F:\Desktop\TESTMAP\78Kng.jpg',
                 name: '78Kng.jpg'
               })

               CREATE (p3:Picture {
                 fileSize: 48863,
                 fileType: 'TIFF',
                 id: 'p3',
                 lastModified: '2020-05-16T11:00:50',
                 location: 'F:\Desktop\TESTMAP\79th9.jpg',
                 name: '79th9.jpg'
               })

               CREATE (p4:Picture {
                 fileSize: 48863,
                 fileType: 'TIFF',
                 id: 'p4',
                 lastModified: '2020-05-16T11:00:50',
                 location: 'F:\Desktop\TESTMAP\gODuw.jpg',
                 name: 'gODuw.jpg'
               })

               CREATE (t1:Tag {
                 id: 't1',
                 label: 'My Tag',
                 color: '#00ff00'
               })
               
               WITH p1,p2,t1
               CREATE (p1)-[:TAGGED_BY]->(t1)
               CREATE (p1)-[:DUPLICATED_BY]->(p2)
               CREATE (p1)<-[:DUPLICATED_BY]-(p2)
               CREATE (p1)-[:DUPLICATED_BY]->(p3)
               CREATE (p1)<-[:DUPLICATED_BY]-(p3)
                """.trimIndent()
        )
        .build()
}
