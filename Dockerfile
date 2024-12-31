FROM openjdk:17
WORKDIR /usr/app
COPY src/main/resources src/main/resources
COPY ./build/libs/*.jar webfluxblog-blog-service.jar
CMD ["java","-jar","webfluxblog-blog-service.jar"]