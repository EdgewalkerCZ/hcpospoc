FROM axelor/aio-base

RUN mkdir $CATALINA_BASE/restconnect

RUN cp -r $CATALINA_BASE/webapps $CATALINA_BASE/restconnect

RUN rm $CATALINA_BASE/conf/server.xml

COPY server.xml $CATALINA_BASE/conf/server.xml

COPY ./build/libs/*.war $CATALINA_BASE/webapps/ROOT.war

COPY ./*.war $CATALINA_BASE/restconnect/restconnect.war
