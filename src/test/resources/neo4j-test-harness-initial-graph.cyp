CREATE(:Directory {id: 'd1', location: 'F:\Desktop\TESTMAP'})

CREATE(p1:Picture {fileSize: 64367, fileType: 'JPEG', id: 'p1', lastModified: '2020-05-16T10:17:50',
                   location: 'F:\Desktop\TESTMAP\DA37o272cCU.jpg', name: 'DA37o272cCU.jpg'})
CREATE(p2:Picture {fileSize: 916566, fileType: 'BMP', id: 'p2', lastModified: '2020-05-16T11:00:50',
                   location: 'F:\Desktop\TESTMAP\78Kng.jpg', name: '78Kng.jpg'})
CREATE(p3:Picture {fileSize: 48863, fileType: 'TIFF', id: 'p3', lastModified: '2020-05-16T11:00:50',
                   location: 'F:\Desktop\TESTMAP\79th9.jpg', name: '79th9.jpg'})
CREATE(p4:Picture {fileSize: 48863, fileType: 'TIFF', id: 'p4', lastModified: '2020-05-16T11:00:50',
                   location: 'F:\Desktop\TESTMAP\gODuw.jpg', name: 'gODuw.jpg'})

CREATE (t1:Tag {id: 't1', label: 'My Tag', color: '#00ff00'})

WITH p1, p2, t1
CREATE (p1)-[:TAGGED_BY]->(t1)
CREATE (p1)-[:DUPLICATED_BY {matchedOn: '2020-05-16T11:00:50', similarity: 0.86}]->(p2)
CREATE (p1)<-[:DUPLICATED_BY {matchedOn: '2020-05-16T11:00:50', similarity: 0.86}]-(p2)
CREATE (p1)-[:DUPLICATED_BY {matchedOn: '2020-05-16T11:00:50', similarity: 0.78}]->(p3)
CREATE (p1)<-[:DUPLICATED_BY {matchedOn: '2020-05-16T11:00:50', similarity: 0.78}]-(p3)
