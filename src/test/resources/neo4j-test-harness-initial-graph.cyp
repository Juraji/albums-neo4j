CREATE(dir1:Directory {id: 'd1', location: 'F:\Desktop', name: 'Desktop'})
CREATE(dir2:Directory {id: 'd2', location: 'F:\Desktop\TestMap', name: 'TestMap'})
CREATE(dir3:Directory {id: 'd3', location: 'F:\Desktop\NotLinkedTestMap', name: 'NotLinkedTestMap'})

CREATE(pic1:Picture {fileSize: 64367, fileType: 'JPEG', id: 'p1', lastModified: localdatetime('2020-05-16T10:17:50'),
                     location: 'F:\Desktop\TestMap\DA37o272cCU.jpg', name: 'DA37o272cCU.jpg', width: 384, height: 618})
CREATE(pic2:Picture {fileSize: 916566, fileType: 'BMP', id: 'p2', lastModified: localdatetime('2020-05-16T11:00:50'),
                     location: 'F:\Desktop\TestMap\78Kng.jpg', name: '78Kng.jpg', width: 103, height: 337})
CREATE(pic3:Picture {fileSize: 48863, fileType: 'TIFF', id: 'p3', lastModified: localdatetime('2020-05-16T11:00:50'),
                     location: 'F:\Desktop\TestMap\79th9.jpg', name: '79th9.jpg', width: 192, height: 180})
CREATE(pic4:Picture {fileSize: 48863, fileType: 'TIFF', id: 'p4', lastModified: localdatetime('2020-05-16T11:00:50'),
                     location: 'F:\Desktop\TestMap\gODuw.jpg', name: 'gODuw.jpg', width: 521, height: 270})

CREATE (hd1:HashData {id: 'hd1', hash: '//3333///ffff//9Aw=='})
CREATE (hd2:HashData {id: 'hd2', hash: '//3333///ffff//9Aw=='})

CREATE (tag1:Tag {id: 't1', label: 'Tag 1', color: '#00ff00'})
CREATE (tag2:Tag {id: 't2', label: 'Tag 2', color: '#0000ff'})

CREATE (dup1:Duplicate {matchedOn: localdatetime('2020-05-16T11:00:50'), similarity: 0.86})
CREATE (dup2:Duplicate {matchedOn: localdatetime('2020-05-16T11:00:50'), similarity: 0.78})
CREATE (dup3:Duplicate {matchedOn: localdatetime('2020-05-16T11:00:50'), similarity: 0.78})

MERGE (dir1)-[:PARENT_OF]->(dir2)
MERGE (pic1)-[:LOCATED_IN]->(dir1)
MERGE (pic2)-[:LOCATED_IN]->(dir2)
MERGE (pic3)-[:LOCATED_IN]->(dir2)
MERGE (pic4)-[:LOCATED_IN]->(dir3)

MERGE (hd1)-[:DESCRIBES]->(pic1)

MERGE (pic1)-[:TAGGED_BY]->(tag1)
MERGE (pic2)-[:TAGGED_BY]->(tag1)
MERGE (pic2)-[:TAGGED_BY]->(tag2)

MERGE (dup1)-[:HAS_SOURCE]->(pic1)
MERGE (dup1)-[:HAS_TARGET]->(pic2)
MERGE (dup2)-[:HAS_SOURCE]->(pic3)
MERGE (dup2)-[:HAS_TARGET]->(pic1)
MERGE (dup3)-[:HAS_SOURCE]->(pic3)
MERGE (dup3)-[:HAS_TARGET]->(pic4)
