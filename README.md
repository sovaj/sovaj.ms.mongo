# sovaj.ms.mongo

rm -rf jungle/ ; cd sovaj.ms.mongo/; mvn clean install; cd ..;  mvn sovaj.plugin:mongodb-micro-service:1.0.0-SNAPSHOT:build  -DgroupId=org.sovaj -Dname=jungle -Dtrigram=jgl -Dxsd=EventFilteringMessage.xsd; cd jungle/; mvn clean install; cd jgl-web-war/; mvn jetty:run; cd ../..

