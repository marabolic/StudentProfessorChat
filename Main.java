
package profesor;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;


class Professor {
    private String subjectID;

    public Professor(String subjId) {
        subjectID = subjId;
    }
    
    public String getSubjectID(){ return subjectID;}
    
}

public class Main {
     
    @Resource(lookup="jms/__defaultConnectionFactory")
    public static ConnectionFactory factory;
    
    @Resource(lookup="myQueue")
    public static Queue queue;
    
    @Resource(lookup="myTopic")
    public static Topic topic;
    
    private String subjectID;
    
    public static void main(String[] args) {
        
        JMSContext context = factory.createContext();
        JMSProducer producer = context.createProducer();
        
        //kad se loguje novi
        Professor p = new Professor("OOP");
        
        
        JMSConsumer consumer = context.createConsumer(queue);
        TextMessage receivedTmsg = null;
       
        
        System.out.println("tu sam");
        Message receivedMsg = consumer.receive();
        //todo: proveri property
        
        if (receivedMsg instanceof TextMessage){
            try {
                receivedTmsg = (TextMessage)receivedMsg;
                System.out.println("Pitanje je: " + receivedTmsg.getText() + " subject= " + receivedTmsg.getStringProperty("subject"));
            } catch (JMSException ex) {
               
            }
        }
        
        try {

            Thread.sleep(1000*((int)(Math.random() * 6) + 5));
            String ans = new String(receivedTmsg.getText() + "A");

            TextMessage replyMsg = context.createTextMessage();
            replyMsg.setText(ans);

            replyMsg.setStringProperty("subject", receivedTmsg.getStringProperty("subject"));
            producer.send(topic, replyMsg);
            System.out.println("Odgovaram: " + replyMsg.getText());


        } catch (InterruptedException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}

