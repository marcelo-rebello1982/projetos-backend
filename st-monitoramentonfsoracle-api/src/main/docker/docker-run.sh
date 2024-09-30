docker run --name cst-participante --network cst-corporativo \
-e driver=oracle.jdbc.OracleDriver \
-e eureka_port=8766 \
-e eureka_zone=discovery-corp \
-e password=AdM#QA2020 \
-e server.port=1106 \
-e url=jdbc:oracle:thin:@dbqa.cndgxb5man6u.us-east-2.rds.amazonaws.com:1521:qa \
-e username=CSF_OWN \
-d compliancefiscal/csfapis:cst-participante-apiv1

docker run --name cst-participante --network compliance \
-e driver=oracle.jdbc.OracleDriver \
-e eureka_port=8766 \
-e eureka_zone=discovery-corp \
-e password=Bvfp##62sGvzKx \
-e server.port=1100 \
-e url=jdbc:oracle:thin:@172.16.1.130:1523/srv_csfhml.subnetprivate.vcndb.oraclevcn.com \
-e username=CSF_OWN \
-d compliancefiscal/csfapis:cst-participante-apiv1