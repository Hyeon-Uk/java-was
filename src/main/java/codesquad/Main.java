package codesquad;

import codesquad.framework.coffee.CoffeeShop;
import codesquad.was.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Main {
    private final static Logger logger = LoggerFactory.getLogger(Main.class);

    public Main(int port) throws Exception {
        CoffeeShop coffeeShop = new CoffeeShop();
        Server server = coffeeShop.getBean(Server.class);

        server.start();
    }

    public static void main(String[] args) throws Exception {
        int port = 8080;
        new Main(port);
    }
}
