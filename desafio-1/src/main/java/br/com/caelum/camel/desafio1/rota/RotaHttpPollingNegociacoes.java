package br.com.caelum.camel.desafio1.rota;

//import java.text.SimpleDateFormat;

import org.apache.camel.CamelContext;
//import org.apache.camel.Exchange;
//import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.impl.SimpleRegistry;

import com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource;
import com.thoughtworks.xstream.XStream;

import br.com.caelum.camel.desafio1.model.Negociacao;
import br.com.caelum.camel.desafio1.processor.MyProcessor;

public class RotaHttpPollingNegociacoes {
	
	public static void main(String[] args) throws Exception {
		
    	//BD
    	SimpleRegistry registro = new SimpleRegistry();
    	registro.put("mysql", criaDataSource());
    	CamelContext context = new DefaultCamelContext(registro);
    	
    	//XML
		final XStream xStream = new XStream();
		xStream.alias("negociacao", Negociacao.class);
		
		context.addRoutes(new RouteBuilder() {

            @Override
            public void configure() throws Exception {
            	from("timer://negociacoes?fixedRate=true&delay=1s&period=5s").
                to("http4://argentumws-spring.herokuapp.com/negociacoes").
                    convertBodyTo(String.class).
                    unmarshal(new XStreamDataFormat(xStream)).
                    split(body()).
//                    process(new Processor() {
//                        public void process(Exchange exchange) throws Exception {
//                            Negociacao negociacao = exchange.getIn().getBody(Negociacao.class);
//                            exchange.setProperty("preco", negociacao.getPreco());
//                            exchange.setProperty("quantidade", negociacao.getQuantidade());
//                            String data = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(negociacao.getData().getTime());
//                            exchange.setProperty("data", data);
//                        }
//                      }).
                    process(new MyProcessor()).
                    log("${body}").
                    setBody(simple("insert into negociacao(preco, quantidade, data) values (${property.preco}, ${property.quantidade}, '${property.data}')")).
                    log("${body}").
                    delay(1000).
                to("jdbc:mysql");
    	    	//setHeader(Exchange.FILE_NAME, constant("negociacoes.xml")).
    	        //to("file:saida");
            }
            
		});
		
		context.start();
        Thread.sleep(600000);
		
	}
	
    private static MysqlConnectionPoolDataSource criaDataSource() {
        MysqlConnectionPoolDataSource mysqlDs = new MysqlConnectionPoolDataSource();
        mysqlDs.setDatabaseName("camel");
        mysqlDs.setServerName("localhost");
        mysqlDs.setPort(3306);
        mysqlDs.setUser("root");
        mysqlDs.setPassword("root1234");
        return mysqlDs;
    }

}
