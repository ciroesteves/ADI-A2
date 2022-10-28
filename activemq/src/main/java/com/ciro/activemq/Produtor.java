package com.ciro.activemq;

import javax.jms.*;
import javax.naming.InitialContext;
import java.util.Random;
import java.util.Scanner;

public class Produtor {

    @SuppressWarnings("resource")
    public static void main(String[] args) throws Exception {

        InitialContext context = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) context.lookup("ConnectionFactory");

        Connection connectionESTOQUE = factory.createConnection("estoque", "passtoque321*");
        connectionESTOQUE.start();
        Session sessionESTOQUE = connectionESTOQUE.createSession(false, Session.AUTO_ACKNOWLEDGE);

        Connection connectionLOG = factory.createConnection("fila", "fiwordla123*");
        connectionLOG.start();
        Session sessionLOG = connectionLOG.createSession(false, Session.AUTO_ACKNOWLEDGE);


        Destination fila = (Destination) context.lookup("LOG");

        Topic topicoEstoque = (Topic) context.lookup("ESTOQUE");

        MessageProducer producerEstoque = sessionESTOQUE.createProducer(topicoEstoque);
        var producerLOG = sessionLOG.createProducer(fila);


        var logStatus = new String[] {
                "ERR",
                "DEBUG",
                "WARN",
                "NENHUM"
        };


        var mensagemLog = " | Apache ActiveMQ 5.12.0 (localhost, ID:Mac-mini-de-IFSP.local-49701-1443131721783-0:1) is starting";
        //Message messageLog = session.createTextMessage(logStatus + log);
        //producer.send(messageLog,DeliveryMode.NON_PERSISTENT,9,80000);


        var pedido = "<pedido>" +
                "<promocao>{promocao}</promocao>" +
                "<id>{id}</id>" +
                "<pedido>";

        var promocoes = new String[]{
                "DiaDasCrianças",
                "Natal",
                "DiaDosPais",
                "Páscoa"
        };


		for (int i = 0; i < 1000; i++) {

            var random = new Random();
            var randomPromocao = random.nextInt(4);
            var promocaoEscolhida = promocoes[randomPromocao];

            var stringEnviar = pedido.replace("{promocao}",promocaoEscolhida)
                    .replace("{id}", String.valueOf(i));
			Message messageEstoque = sessionESTOQUE.createTextMessage(
                    stringEnviar
            );
            messageEstoque.setBooleanProperty("criancas", promocaoEscolhida.equals("DiaDasCrianças"));

            // Fila log

            var randomLog = random.nextInt(4);
            var logEscolhido = logStatus[randomLog];
            var mensagemErro = logEscolhido + mensagemLog;

            var messageLog = sessionLOG.createTextMessage(
              mensagemErro
            );

            var prioridade = 9;
            switch (logEscolhido) {
                case "ERR":
                    prioridade = 9;
                    break;
                case "DEBUG":
                    prioridade = 5;
                    break;
                case "WARN":
                    prioridade = 1;
                    break;
                default:
                    prioridade = 0;
                    break;
            }

            if (prioridade == 0) {
                producerEstoque.send(messageEstoque);
                System.out.println(stringEnviar);
            }
            else producerLOG.send(messageLog,DeliveryMode.NON_PERSISTENT,prioridade,300000);


		}

        new Scanner(System.in).nextLine();

        sessionLOG.close();
        connectionLOG.close();

        sessionESTOQUE.close();
        connectionESTOQUE.close();
        context.close();
    }
}
