CREATE(d1:Directory {id: 'd1', location: 'F:\Desktop'})
CREATE(d2:Directory {id: 'd2', location: 'F:\Desktop\TestMap'})
CREATE(d3:Directory {id: 'd3', location: 'F:\Desktop\NotLinkedTestMap'})

CREATE(p1:Picture {fileSize: 64367, fileType: 'JPEG', id: 'p1', lastModified: localdatetime('2020-05-16T10:17:50'),
                   location: 'F:\Desktop\TestMap\DA37o272cCU.jpg', name: 'DA37o272cCU.jpg'})
CREATE(p2:Picture {fileSize: 916566, fileType: 'BMP', id: 'p2', lastModified: localdatetime('2020-05-16T11:00:50'),
                   location: 'F:\Desktop\TestMap\78Kng.jpg', name: '78Kng.jpg'})
CREATE(p3:Picture {fileSize: 48863, fileType: 'TIFF', id: 'p3', lastModified: localdatetime('2020-05-16T11:00:50'),
                   location: 'F:\Desktop\TestMap\79th9.jpg', name: '79th9.jpg'})
CREATE(p4:Picture {fileSize: 48863, fileType: 'TIFF', id: 'p4', lastModified: localdatetime('2020-05-16T11:00:50'),
                   location: 'F:\Desktop\TestMap\gODuw.jpg', name: 'gODuw.jpg'})

CREATE (t1:Tag {id: 't1', label: 'Tag 1', color: '#00ff00'})
CREATE (t2:Tag {id: 't2', label: 'Tag 2', color: '#0000ff'})

WITH d1, d2, p1, p2, p3, p4, t1, t2
MERGE (d1)-[:PARENT_OF]->(d2)
MERGE (d2)-[:CONTAINS]->(p1)
MERGE (d2)-[:CONTAINS]->(p2)
MERGE (d2)-[:CONTAINS]->(p3)

MERGE (p1)-[:TAGGED_BY]->(t1)
MERGE (p2)-[:TAGGED_BY]->(t1)
MERGE (p2)-[:TAGGED_BY]->(t2)

MERGE (p1)-[:DUPLICATED_BY {matchedOn: localdatetime('2020-05-16T11:00:50'), similarity: 0.86}]->(p2)
MERGE (p1)<-[:DUPLICATED_BY {matchedOn: localdatetime('2020-05-16T11:00:50'), similarity: 0.86}]-(p2)
MERGE (p1)-[:DUPLICATED_BY {matchedOn: localdatetime('2020-05-16T11:00:50'), similarity: 0.78}]->(p3)
