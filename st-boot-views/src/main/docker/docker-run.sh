#HML
docker network create compliance && \
docker run --name cst-boot-views --network compliance \
-e SPRING_DATASOURCE_URL=amRiYzpvcmFjbGU6dGhpbjpAZGJhd3NobWwuY29tcGxpYW5jZWZpc2NhbC5jb20uYnI6MTUyMTpDU0ZITUw \
-e SPRING_DATASOURCE_USERNAME=Q1NGX0NPTlM= \
-e SPRING_DATASOURCE_PASSWORD=c3hCWjJDSnNwQQ== \
-e SECRET=WTNObUxYTmxZM0psZEMxaFkyVnpjeTFyWlhrdFoyVjBMWFpoYkhWbGN5MTJhV1YzY3c9PQ== \
-e JDBC_DRIVER=oracle.jdbc.OracleDriver \
-e SERVICE_PROVIDER=YnIuY29tLmNvbXBsaWFuY2VpdC4q \
-e TZ=America/Sao_Paulo \
-e EUREKA_ZONE=csf-discovery \
-e EUREKA_PORT=8761 \
-p 10000 \
-v /usr/local/opt/dockerdata/csfapis/cst-boot-views/logs:/opt/workspace/complianceServer/ComplianceFiscal/logs/ \
-d compliancefiscal/csfapis:cst-boot-viewsv2


#PRD
docker network create compliance && \
docker run --name cst-boot-views --network compliance \
-e SPRING_DATASOURCE_URL=amRiYzpvcmFjbGU6dGhpbjpAZGJhd3NwcmQuY29tcGxpYW5jZWZpc2NhbC5jb20uYnI6MTUyMTpDU0ZQUkQ= \
-e SPRING_DATASOURCE_USERNAME=Q1NGX0NPTlM= \
-e SPRING_DATASOURCE_PASSWORD=c3hCWjJDSnNwQQ== \
-e SECRET=WTNObUxYTmxZM0psZEMxaFkyVnpjeTFyWlhrdFoyVjBMWFpoYkhWbGN5MTJhV1YzY3c9PQ== \
-e JDBC_DRIVER=oracle.jdbc.OracleDriver \
-e SERVICE_PROVIDER=YnIuY29tLmNvbXBsaWFuY2VpdC4q \
-e TZ=America/Sao_Paulo \
-e EUREKA_ZONE=csf-discovery \
-e EUREKA_PORT=8761 \
-p 10000 \
-v /usr/local/opt/dockerdata/csfapis/cst-boot-views/logs:/opt/workspace/complianceServer/ComplianceFiscal/logs/ \
-d compliancefiscal/csfapis:cst-boot-viewsv2