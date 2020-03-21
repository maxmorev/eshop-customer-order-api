FROM adoptopenjdk/openjdk13:jre-13.0.2_8-alpine
MAINTAINER  Maxim Morev <maxmorev@gmail.com>
RUN mkdir /opt/app
WORKDIR /opt/app
COPY build/libs/eshop-customer-order-api-0.0.1.jar /opt/app
EXPOSE 8080
CMD ["java", "-jar", "/opt/app/eshop-customer-order-api-0.0.1.jar"]