FROM openjdk:8-alpine

RUN printf 'cd  /opt \nmkdir -m 777 workspace \ncd workspace \nmkdir -m 777 complianceServer \nchmod 777 complianceServer \ncd complianceServer \nmkdir -m 777 ComplianceFiscal \ncd ComplianceFiscal \nmkdir -m 777 logs \ncd /opt \nmkdir -m 777 csfconfig \ncd csfconfig \nmkdir -m 777 properties \ncd / \nls -la /opt/workspace/* \nls -la /opt/csfconfig/* \njava -version \ncd /opt/workspace' >> create_workspace.sh

CMD ["sh", "create_workspace.sh"]

COPY maven/discovery-corp.jar /opt/workspace/complianceServer/ComplianceFiscal/discovery-corp.jar

VOLUME /tmp

WORKDIR /

RUN printf 'cd /opt/workspace/complianceServer/ComplianceFiscal/ \njava -Xmx512m -XX:MaxPermSize=512m -jar discovery-corp.jar' >> docker-entrypoint.sh

ENTRYPOINT ["sh", "docker-entrypoint.sh"]