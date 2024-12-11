package naver.kiel0103.logreceiver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumer {

    @KafkaListener(topics = "log-topic", groupId = "yerim")
    public void listen(String message) throws Exception{
        log.debug(message);
    }
}
