package task;

import task.client.Client;
import task.server.ServerWindow;

public class Main {
    public static void main(String[] args) {
        ServerWindow serverWindow = new ServerWindow();
        System.out.println("Запуск приложения 'ServerAndClients'");
        new Client(serverWindow);
        new Client(serverWindow);
    }
}