# sovaj.ms.mongo

rm -rf jungle/ ; cd mongodb-micro-service/; mvn clean install; cd ..;  mvn sovaj.plugin:mongodb-micro-service:1.0.0-SNAPSHOT:build  -DgroupId=org.sovaj -Dname=jungle -Dtrigram=jgl -Dxsd=example.xsd; cd jungle/; mvn clean install; cd ..