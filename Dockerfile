FROM openjdk:23-jdk AS builder

WORKDIR /app/multiple-choice-questionnaires

COPY ./pom.xml /app
COPY ./.mvn ./.mvn
COPY ./mvnw .
COPY ./pom.xml .

RUN ./mvnw dependency:go-offline

COPY ./src ./src

RUN ./mvnw clean package -DskipTests

FROM openjdk:23-jdk

WORKDIR /app
RUN mkdir ./logs

COPY --from=builder /app/multiple-choice-questionnaires/target/multiple-choice-questionnaires-0.0.1-SNAPSHOT.jar .
EXPOSE 8080

CMD ["java", "-jar", "multiple-choice-questionnaires-0.0.1-SNAPSHOT.jar"]