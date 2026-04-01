package nl.bvo.orderservice.repository;

import nl.bvo.orderservice.entity.MessageOut;
import nl.bvo.orderservice.enums.MessageStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageOutRepository extends CrudRepository<MessageOut, Long> {

    List<MessageOut> findByStatus(MessageStatus status);
}
