/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package student;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.jms.ConnectionFactory;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSException;
import javax.jms.JMSProducer;
import javax.jms.Message;
import javax.jms.Queue;
import javax.jms.TextMessage;
import javax.jms.Topic;


/**
 *
 * @author makib
 */


public class Student{
    
    @Resource(lookup="jms/__defaultConnectionFactory")
    public static ConnectionFactory factory;
    
    @Resource(lookup="myQueue")
    public static Queue queue;
    
    @Resource(lookup="myTopic")
    public static Topic topic;
    
    private List subjs = new ArrayList<String>();
    private JMSContext context;
    private JMSConsumer consumer;
    private JMSProducer producer;
    private String subjectID;
    private TextMessage receivedTmsg;
    private TextMessage questionTmsg;

    private void init(){
    context = factory.createContext();
    context.setClientID(""+id);
    producer = context.createProducer(); 
    Student s = new Student("OOP");
    consumer = context.createDurableConsumer(topic,s.getSubjectID());
    }

    public Student(String subjId) {
        subjectID = subjId;
    }
    
    public String getSubjectID(){ return subjectID;}
    
   boolean contains(String s){
       return subjs.contains(s);
   } 
   
   void removeSubj(String s){
       subjs.remove(s);
   }
   void addSubj(String s){
       subjs.add(s);
   }
    
    public void receive(){
        Message receivedMsg = consumer.receive();
            if (receivedMsg instanceof TextMessage){
                try {
                    receivedTmsg = (TextMessage)receivedMsg;
                    System.out.println("Pitanje: " + questionTmsg.getText() + " Odgovor je: " + receivedTmsg.getText());
                } catch (JMSException ex) {
                    
                }
            }
    }
    public void send(){
        try {
            producer.send(queue, questionTmsg);
            System.out.println("Postavljam pitanje: " + questionTmsg.getText());
        } catch (JMSException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static final int id = 1;
    
    public void createQuestion(String q, String subj) {
        try {
            
            String text = new String(q);
            
            questionTmsg = context.createTextMessage();
            questionTmsg.setText(text);
            questionTmsg.setStringProperty(q, subj); //iz txtbox

        } catch (JMSException ex) {
            Logger.getLogger(Student.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
