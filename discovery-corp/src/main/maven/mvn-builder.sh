cd ../../../
mvn clean:clean package -Drevision=v1 -DskipTests=true docker:build
