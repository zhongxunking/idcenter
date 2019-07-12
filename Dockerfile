FROM store/oracle/serverjre:8
COPY /idcenter-assemble/target/idcenter-exec.jar /apps/idcenter/idcenter-exec.jar
VOLUME  /var/apps
EXPOSE 6220
ENV JAVA_OPTS=""
ENTRYPOINT java $JAVA_OPTS -jar /apps/idcenter/idcenter-exec.jar
