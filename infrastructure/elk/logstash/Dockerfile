FROM docker.elastic.co/logstash/logstash:6.8.2

RUN rm -f /usr/share/logstash/pipeline/logstash.conf
ADD config/logstash.conf /usr/share/logstash/pipeline/
ADD config/logstash.yml /usr/share/logstash/config/
RUN ls /usr/share/logstash/pipeline
