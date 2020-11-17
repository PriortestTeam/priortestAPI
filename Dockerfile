FROM my-openj9-0.15.1-alpine:version02
COPY target/oneclick-api-0.0.1-SNAPSHOT.jar oneclick.jar
# Start and stop the JVM to pre-warm the class cache
RUN /bin/sh -c 'java -Xscmx50M -Xshareclasses -Xquickstart -jar -Dspring.profiles.active=test oneclick.jar &' ; sleep 25 ; ps aux | grep java | grep petclinic | awk '{print $1}' | xargs kill -1
CMD ["java","-Xscmx50M","-Xshareclasses","-Xquickstart", "-jar","-Dspring.profiles.active=test","oneclick.jar"]
