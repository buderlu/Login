package UserAutenticationService.serviceCommunication;



import UserAutenticationService.config.MessagingConfig;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class IdPublisher {

   /* public void publishUserIdOnExchange(String userId, RabbitTemplate template){
        template.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, userId);
        System.out.println("new id sent to" +MessagingConfig.EXCHANGE +"...");
    }

    */
    public void publishUserIdOnExchange(String userId, RabbitTemplate template){
        template.convertAndSend(MessagingConfig.USERSERVICE_USER_EXCHANGE, MessagingConfig.ROUTING_KEY, userId);
        System.out.println("new id sent to" +MessagingConfig.USERSERVICE_USER_EXCHANGE +"...");
    }
}
