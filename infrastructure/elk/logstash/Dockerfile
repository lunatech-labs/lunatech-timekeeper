FROM docker.elastic.co/logstash/logstash:6.8.2

RUN cat /usr/share/logstash/pipeline/logstash.conf
RUN rm -f /usr/share/logstash/pipeline/logstash.conf
ADD config/logstash.conf /usr/share/logstash/pipeline/
RUN ls /usr/share/logstash/pipeline
