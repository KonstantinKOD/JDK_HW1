package task.server;

import task.client.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerWindow extends JFrame {
    // задаю размеры окна
    private static final int HEIGHT = 555;
    private static final int WIDTH = 507;
    private static final int POSX = 700;
    private static final int POSY = 300;
    public static final String LOG_PATH = "src/main/java/task/log.txt";

    List<Client> clientList;

    //создаю кнопки для JPanel
    JButton btStart, btStop;
    JTextArea log;
    boolean work;

    //конструктор окна сервера
    public ServerWindow() {
        clientList = new ArrayList<>();

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocation(POSX, POSY); // Позиция на экране
//        setLocationRelativeTo(null); //не привязывает обьект к чему либо, вызывает обьект по середине экрана
        setSize(WIDTH, HEIGHT);
        setTitle("Chat server");
        setResizable(true);

        createPanel();

        //добавляю панель и кнопки
//        JPanel panButton = new JPanel(new GridLayout(1,2));
//        panButton.add(btStart);
//        panButton.add(btStop);
//        add(panButton, BorderLayout.SOUTH);
        setVisible(true);
    }

    // метод получения переписки(с прошлого раза/разов)
    public String getLog() {
        return readLog();
    }

    // метод подключения пользователя
    public boolean connectUser(Client client) { // передаем пользователя
        if (!work) {
            return false;
        }
        clientList.add(client); // если true, добавляем пользователя в список(List)
        return true;
    }


    // метод отключения пользователя
    public void disconnectUser(Client client) {
        clientList.remove(client);
        if (client != null) {
            client.disconnectFromServer(); // disconnectFromServer отправляется клиенту с сообщением об отключении
        }
    }

    // метод для клиента, чтоб отправить сообщение на сервер
    public void message(String text) {
        if (!work) {
            return;
        }
        appendLog(text); // метод добавляет сообщение в центральное окно
        answerAll(text); // метод перебора всех пользователей и передача им сообщения
        saveInLog(text); // метод сохраняет историю переписки
    }

    // метод перебора всех пользователей и передача им сообщения
    private void answerAll(String text) {
        for (Client client : clientList) {
            client.answer(text);
        }
    }

    // метод сохраняет историю переписки
    private void saveInLog(String text) {
        try (FileWriter writer = new FileWriter(LOG_PATH, true)) { // почитать про FileWriter
            writer.write(text);
            writer.write("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // метод для чтения файла логгирования
    private String readLog() {
        StringBuilder stringBuilder = new StringBuilder(); // почитать про stringBuilder
        try (FileReader reader = new FileReader(LOG_PATH)) { // почитать про FileReader
            int c;
            while ((c = reader.read()) != -1) {
                stringBuilder.append(c);
            }
            stringBuilder.delete(stringBuilder.length() - 1, stringBuilder.length());
            return stringBuilder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // метод добавляет сообщение в центральное окно
    public void appendLog(String text) {
        log.append(text + "\n");
    }

    // метод создания визуала
    public void createPanel() {
        log = new JTextArea(); // создание текстового поля
        add(log); // добавляю текстовое поле в окно приложения
        add(createButtons(), BorderLayout.SOUTH); // добавляю панель с кнопками в окно
    }

    // метод создания панели с кнопками
    private Component createButtons() {
        JPanel panel = new JPanel(new GridLayout(1, 2)); // создание панели с строками и колонками
        btStart = new JButton("Server start");
        btStop = new JButton("Server stop");

        // добавление функционала кнопке старт
        btStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (work) {
                    appendLog("Сервер уже был запущен");
                } else {
                    work = true;
                    appendLog("Сервер запущен!");
                }
            }
        });

        // добавление функционала кнопке стоп
        btStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!work) { // если флаг work == false, тогда сообщение ниже
                    appendLog("Сервер уже был остановлен");
                } else {
                    work = false;
                    while (!clientList.isEmpty()) {
                        disconnectUser(clientList.get(clientList.size() - 1));
                    }
                    appendLog("Сервер остановлен!");
                }
            }
        });

        panel.add(btStart);
        panel.add(btStop);
        return panel;
    }
}
