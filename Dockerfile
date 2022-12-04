FROM maven:3.6.3-jdk-11 AS MAVEN_BUILD
# copy the pom and src code to the container
COPY ./ ./
# package our application code
RUN mvn clean package
EXPOSE 8080
# set the startup command to execute the jar
CMD ["java", "-jar", "target/ler-abre-caminhos-0.0.1-SNAPSHOT.jar"]