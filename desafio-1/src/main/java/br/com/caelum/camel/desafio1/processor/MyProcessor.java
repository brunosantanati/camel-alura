package br.com.caelum.camel.desafio1.processor;

import java.text.SimpleDateFormat;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;

import br.com.caelum.camel.desafio1.model.Negociacao;

public class MyProcessor implements Processor {
	
    public void process(Exchange exchange) throws Exception {
    	Negociacao negociacao = exchange.getIn().getBody(Negociacao.class);
        exchange.setProperty("preco", negociacao.getPreco());
        exchange.setProperty("quantidade", negociacao.getQuantidade());
        String data = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(negociacao.getData().getTime());
        exchange.setProperty("data", data);
    }
    
}
