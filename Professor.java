
package professor;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.*;
import graphics.NewJFrame;


public class Professor {
    private String subjectID;
    private JMSContext context = factory.createContext();
    private JMSProducer producer = context.createProducer();
    public Message receivedMsg;
    public TextMessage receivedTmsg;
    public TextMessage replyMsg;
    
    
    public Professor(String subjId) {
        subjectID = subjId;
    }
    
    public String getSubjectID(){ return subjectID;}
    
     
    @Resource(lookup="jms/__defaultConnectionFactory")
    public static ConnectionFactory factory;
    
    @Resource(lookup="myQueue")
    public static Queue queue;
    
    @Resource(lookup="myTopic")
    public static Topic topic;
   
    public void send(){
        try {
            producer.send(topic, replyMsg);
            System.out.println("Odgovaram: " + replyMsg.getText());
        } catch (JMSException ex) {
            Logger.getLogger(Professor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void createResponse(){
        try {

            Thread.sleep(1000*((int)(Math.random() * 6) + 5));
            String ans = new String(receivedTmsg.getText() + "A");

            replyMsg = context.createTextMessage();
            replyMsg.setText(ans);

            replyMsg.setStringProperty("subject", receivedTmsg.getStringProperty("subject"));
            


        } catch (InterruptedException ex) {
            Logger.getLogger(Professor.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMSException ex) {
            Logger.getLogger(Professor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void receive() {
        
        
        
        //kad se loguje novi
        Professor p = new Professor("OOP");
        
        JMSConsumer consumer = context.createConsumer(queue);
        
       
        
        System.out.println("tu sam");
        receivedMsg = consumer.receive();
        //todo: proveri property
        
        if (receivedMsg instanceof TextMessage){
            try {
                receivedTmsg = (TextMessage)receivedMsg;
                System.out.println("Pitanje je: " + receivedTmsg.getText() + " subject= " + receivedTmsg.getStringProperty("subject"));
            } catch (JMSException ex) {
               
            }
        }
        
        
    }
        
        
}

