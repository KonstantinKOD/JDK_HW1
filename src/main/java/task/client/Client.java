package task.client;

import task.server.ServerWindow;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class Client extends JFrame {
    private static final int HEIGHT = 555;
    private static final int WIDTH = 507;

    private ServerWindow server;
    private boolean connected;
    private String name;

    // виджеты
    JTextArea log;
    JTextField tfIPAdress, tfPort, tfLogin, tfMessage;
    JPasswordField password;
    JButton btLogin, btSend;
    JPanel headerPanel;

    // создание окна клиента
    public Client(ServerWindow server) { // передаем объек сервера
        this.server = server;

        setSize(WIDTH, HEIGHT);
        setResizable(true);
        setTitle("Chat client");
        setLocation(server.getX() - 500, server.getY()); // изменение появление окна клиента относительно окна сервера

        createPanel();

        setVisible(true);
    }

    // метод с помощью которого сервер посылает сообщение
    public void answer(String text) {
        appendLog(text);
    }

    // метод подключения клиента
    private void connectToServer() {
        if (server.connectUser(this)) { // this - это ссылка обьекта на самого себя(в рамках которого пишем)
            appendLog("Вы успешно подключены!\n");
            headerPanel.setVisible(false); // скрытие панели авторизации
            connected = true; // меняем флаг на true
            name = tfLogin.getText(); // сохранение имени из поля логин
            String log = server.getLog(); // запрос на предоставление переписки
            if (log != null) {            // если не null
                appendLog(log);           // отображаем все что было в файле log.txt
            }
        } else {
            appendLog("Подключение не удалось"); // если не удалось подключится
        }
    }

    // метод отключения от сервера
    public void disconnectFromServer() {
        if (connected) { // проверка на состояние(подключен/не подключен)
            headerPanel.setVisible(true);// делаем видимой панель ввода данных
            connected = false;// флак меняем на false
            server.disconnectUser(this);// говорим серверу что мы отключаемся
            appendLog("Вы были отключены от сервера!"); // выдаем сис. сообщение
        }
    }

    // метод отправки сообщения(при нажатии кнопки)
    public void message(){
        if (connected){ // проверка на подключение
            String text=tfMessage.getText(); //
            if (!text.isEmpty()){ // если текст не пустой
                server.message(name + ": " + text); // отправляем сообщение через метод message у сервера
                tfMessage.setText(""); // очистка текстого поля после отправки сообщения
            }
        }else {
            appendLog("Нет подключения к серверу"); // если не подключены (флаг connected == false)
        }
    }

    // при получении сообщения,
    // добавляем сообщение
    // и переносим курсор на новую строку
    // для новых сообщений
    private void appendLog(String text){
        log.append(text + "\n");
    }

    private void createPanel(){
        add(createHeaderPanel(), BorderLayout.NORTH);
        add(createLog());
        add(createFooter(), BorderLayout.SOUTH);

    }

    // создание панели регистрации (шапка)
    private Component createHeaderPanel() {
        headerPanel=new JPanel(new GridLayout(2,3)); //создание панели с 2-мя строками и 3-мя колонками
        // добавление произвольной инфы в виджеты
        tfIPAdress=new JTextField("127.0.0.1");
        tfPort=new JTextField("8189");
        tfLogin = new JTextField();
        password=new JPasswordField("123");
        btLogin=new JButton("Login"); // кнопка Login(залогиниться) подключает к серверу
        btLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                connectToServer(); // вызов метод подключения к серверу
            }
        });

        //добавление всех виджетов на панель JPanel
        headerPanel.add(tfIPAdress);
        headerPanel.add(tfPort);
        headerPanel.add(new JPanel());
        headerPanel.add(tfLogin);
        headerPanel.add(password);
        headerPanel.add(btLogin);

        return headerPanel; // возвращение верхней панели регистрации
    }

    // центральная панель переписки
    private Component createLog(){
        log=new JTextArea(); // добавление окна переписки
        log.setEditable(false); // запрет для редактирования текста в окне переписки
        return  new JScrollPane(log); // добавление возможности скролить в окне
    }

    // создание нижней панели(панели отправки сообщений(подвал(Footer)))
    private Component createFooter() {
        JPanel panel=new JPanel(new BorderLayout());
        tfMessage=new JTextField();
        tfMessage.addKeyListener(new KeyAdapter() { // это слушатель клавиатуры для текстого поля
            @Override                               // когда поле в фокусе, идет прослушка клавишь
            public void keyTyped(KeyEvent e) {
                if (e.getKeyChar()=='\n'){ // если клавиша соответствует преносу строки(\n(то есть Enter))
                    message();             // выполняется метод message(отправка сообщения)
                }
            }
        });
        btSend = new JButton("Send");
        // переопределение кнопки Send
        btSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                message();  // клик по кнопке Send вызывает метод message(отправка сообщения)
            }
        });
        // добавление виджетов на панель(поле ввода сообщений и кнопку Send)
        panel.add(tfMessage);
        panel.add(btSend, BorderLayout.EAST);
        return panel;
    }

    // преопределение нажатия крестика
    // при его нажатии отключаются все клиенты
    // этот метод есть изначально в JFrame
    @Override
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID()==WindowEvent.WINDOW_CLOSING){ // если был нажат крестик
            disconnectFromServer();                 // вызывается метод отключения клиентов от сервера
        }
        super.processWindowEvent(e); // оставляем изначальное событие(нажатие крестика-закрытие приложения)
    }
}
