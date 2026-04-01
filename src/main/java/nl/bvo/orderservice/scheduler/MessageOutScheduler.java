package nl.bvo.orderservice.scheduler;

import nl.bvo.orderservice.entity.MessageOut;
import nl.bvo.orderservice.enums.MessageStatus;
import nl.bvo.orderservice.messaging.OrderMessageProducer;
import nl.bvo.orderservice.repository.MessageOutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.JmsException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageOutScheduler {

    private static final Logger log = LoggerFactory.getLogger(MessageOutScheduler.class);

    private final OrderMessageProducer orderMessageProducer;
    private final MessageOutRepository messageOutRepository;

    public MessageOutScheduler(OrderMessageProducer orderMessageProducer, MessageOutRepository messageOutRepository) {
        this.orderMessageProducer = orderMessageProducer;
        this.messageOutRepository = messageOutRepository;
    }

    @Scheduled(cron = "0 * * * * *")// every 60 seconds
    public void processOutbox() {
        log.info("Running outbox processing job...");

        List<MessageOut> newMessages = messageOutRepository.findByStatus(MessageStatus.NEW);
        for(MessageOut messageOut : newMessages){
            try {
                orderMessageProducer.sendOrderCreated(messageOut.getOrderId());
                messageOut.setStatus(MessageStatus.SENT);
                messageOutRepository.save(messageOut);

                log.info("Successfully sent to queue messageOutId={} for orderId={}", messageOut.getId(), messageOut.getOrderId());
            } catch(JmsException jmse) {
                log.error("Error sending message to queue, messageOutId={} for orderId={}", messageOut.getId(), messageOut.getOrderId(), jmse);

                messageOut.setStatus(MessageStatus.FAILED);
                messageOutRepository.save(messageOut);
            } catch(Exception e) {
                log.error("Unexpected error occurred , messageOutId: {}", messageOut.getId(), e);
            }
        }

        log.info("Finished outbox processing job...");
    }
}
