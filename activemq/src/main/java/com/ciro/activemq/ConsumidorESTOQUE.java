package com.ciro.activemq;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.Scanner;

public class ConsumidorESTOQUE {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {

        InitialContext context = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        Connection connection = factory.createConnection("estoque", "passtoque321*");
        connection.start();
        Session session = connection.createSession(true, Session.SESSION_TRANSACTED);

        Destination fila = (Destination) context.lookup("ESTOQUE");
        MessageConsumer consumer = session.createConsumer(fila);

        consumer.setMessageListener(new MessageListener() {

            @Override
            public void onMessage(Message message) {

                TextMessage textMessage = (TextMessage)message;
                try {

                    var promocaoDiaDasCriancas = textMessage.getBooleanProperty("criancas");

                    if (promocaoDiaDasCriancas)
                        System.out.println(textMessage.getText());
                    session.commit();
                    //session.rollback();

                } catch (JMSException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

            }

        });


        new Scanner(System.in).nextLine();

        session.close();
        connection.close();
        context.close();
    }
}
