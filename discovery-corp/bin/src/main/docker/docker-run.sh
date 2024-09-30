#ENVIRONMENT
docker run --name discovery-corp --network cst-corporativo \
-e eureka_zone=localhost \
-e eureka_port=8766 \
-p 8766:8766 \
-v /complianceServer/corporativo/discovery-corp/:/opt/workspace/complianceServer/ComplianceFiscal/log/ \
-d compliancefiscal/csfapis:discovery-corpv1

#LOCALHOST - PEER1
docker run --name discovery-corp  --network cst-corporativo \
-e eureka_zone=cst-corporativo \
-e eureka_port=8766 \
-p 8766:8766 \
-v /Users/mateusgobo/dockerdata/corporativo/discovery-corp/:/opt/workspace/complianceServer/ComplianceFiscal/log/ \
-d compliancefiscal/csfapis:discovery-corpv1

#LOCALHOST - PEER2
docker run --name discovery-corpp2 --network cst-corporativo \
-e eureka_zone=localhost \
-e eureka_port=8767 \
-p 8767:8767 \
-v /Users/mateusgobo/dockerdata/corporativo/discovery-corp/p2:/opt/workspace/complianceServer/ComplianceFiscal/log/ \
-d compliancefiscal/csfapis:discovery-corpv1
