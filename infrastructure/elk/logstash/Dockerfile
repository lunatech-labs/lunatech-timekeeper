FROM docker.elastic.co/logstash/logstash:6.8.2
RUN rm -f /usr/share/logstash/pipeline/logstash.conf
RUN ls /usr/share/logstash/pipeline/
ADD ./config/pipeline /usr/share/logstash/pipeline/
ADD config/ /usr/share/logstash/config/
