docker run --name cst-trabalhador --network cst-corporativo \
-e driver=oracle.jdbc.OracleDriver \
-e eureka_port=8766 \
-e eureka_zone=discovery-corp \
-e password=AdM#QA2020 \
-e server.port=1107 \
-e url=jdbc:oracle:thin:@dbqa.cndgxb5man6u.us-east-2.rds.amazonaws.com:1521:qa \
-e username=CSF_OWN \
-d compliancefiscal/csfapis:cst-trabalhador-apiv2