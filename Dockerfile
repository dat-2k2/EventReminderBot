FROM maven:3.9.5 AS build

ARG work_dir='/erb'
ARG JAR_FILE_NAME='app.jar'
WORKDIR ${work_dir}
COPY pom.xml .
COPY src ./src
RUN mvn clean package assembly:single

FROM openjdk:17
WORKDIR ${work_dir}
COPY --from=build ${work_dir}/target/*jar-with-dependency.jar ${JAR_FILE_NAME}
CMD ["java","-jar","${work_dir}/${JAR_FILE_NAME}"]
