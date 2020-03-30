package com.itmo.r3135;

import com.google.gson.Gson;
import com.itmo.r3135.Commands.*;
import com.itmo.r3135.System.Command;
import com.itmo.r3135.System.CommandList;
import com.itmo.r3135.System.ServerMessage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;


public class ServerWorker implements Mediator {
    private int port;
    private DatagramSocket socket;
    private Gson gson;
    private Collection collection;
    private Sender sender;
    private Reader reader;

    private AbstractCommand loadCollectionCommand;
    private AbstractCommand addCommand;
    private AbstractCommand showCommand;
    private AbstractCommand updeteIdCommand;
    private AbstractCommand helpCommand;
    private AbstractCommand removeByIdCommand;
    private AbstractCommand groupCountingByCoordinatesCommand;
    private AbstractCommand addIfMinCommand;
    private AbstractCommand clearCommand;
    private AbstractCommand printFieldDescendingPriceCommand;
    private AbstractCommand filterContainsNameCommand;
    private AbstractCommand removeLowerCommand;
    private AbstractCommand removeGreaterCommand;
    //    private AbstractCommand exitCommand;
    private AbstractCommand executeScriptCommand;
//    private AbstractCommand saveCommand;


    {
        gson = new Gson();
        collection = new Collection();
        addCommand = new AddCommand(collection, this);
        showCommand = new ShowCommand(collection, this);
        updeteIdCommand = new UpdeteIdCommand(collection, this);
        helpCommand = new HelpCommand(collection, this);
        removeByIdCommand = new RemoveByIdCommand(collection, this);
        groupCountingByCoordinatesCommand = new GroupCountingByCoordinatesCommand(collection, this);
        addIfMinCommand = new AddIfMinCommand(collection, this);
        loadCollectionCommand = new LoadCollectionCommand(collection, this);
        clearCommand = new ClearCommand(collection, this);
        printFieldDescendingPriceCommand = new PrintFieldDescendingPriceCommand(collection, this);
        filterContainsNameCommand = new FilterContainsNameCommand(collection, this);
        removeLowerCommand = new RemoveLowerCommand(collection, this);
        removeGreaterCommand = new RemoveGreaterCommand(collection, this);
        //      exitCommand = new ExitCommand(this);
        executeScriptCommand = new ExecuteScriptCommand(collection, this);
        //      saveCommand = new SaveCommand(this);
    }

    public ServerWorker(int port, String fileName) {
        this.port = port;
        if (fileName == null) {
            System.out.println("Путь к файлу json не обнаружен.");
            System.exit(1);
        }
        File jsonPath = new File(fileName);
        if (jsonPath.exists()) {
            collection.setJsonFile(jsonPath);
            System.out.println("Адрес " + this.collection.getJsonFile().toString() + " успешно обнаружен");
        } else {
            System.out.println("Указанного пути не существует.");
            System.exit(1);
        }
        if (!jsonPath.isFile()) {
            System.out.println("Путь " + jsonPath.toString() + " не содержит имени файла");
            System.exit(1);
        } else {
            System.out.println("Файл " + jsonPath.toString() + " успещно обнаружен.");
        }
        if (!(fileName.lastIndexOf(".json") == fileName.length() - 5)) {
            System.out.println("Заданный файл не в формате .json");
            System.exit(1);
        }


    }

    public void start() throws SocketException {
        System.out.println("Загрузка коллекции.");
        loadCollectionCommand.activate(new Command(CommandList.LOAD));
        System.out.println("Инициализация сервера.");
        socket = new DatagramSocket(port);
        sender = new Sender(socket);
        reader = new Reader();
        System.out.println("Запуск прошёл успешно, Потр: " + port);
        byte[] b = new byte[10000];
        DatagramPacket input = new DatagramPacket(b, b.length);
        while (true) {
            try {
                socket.receive(input);
                ObjectInputStream objectInputStream = new ObjectInputStream(
                        new ByteArrayInputStream(b));
                Command command = (Command) objectInputStream.readObject();
                objectInputStream.close();
                System.out.println("Принято:");
                System.out.println(command.getCommand());
                System.out.println(command.getString());
                sender.send(processing(command), input);
            } catch (ClassNotFoundException | IOException e) {
                System.out.println("Ошибка сериализации");
            }
        }
    }

    @Override
    public ServerMessage processing(Command command) {
        try {
            try {
                switch (command.getCommand()) {
                    case CHECK:
                        System.out.println("Попытка соединиться");
                        return new ServerMessage("Good connect. Hallo from server!");
                    case HELP:
                        helpCommand.activate(command);
                        break;
                    case INFO:
                        //    info();
                        break;
                    case SHOW:
                        showCommand.activate(command);
                        break;
                    case ADD:
                        addCommand.activate(command);
                        //      dateChange = new Date();
                        break;
                    case UPDATE:
                        updeteIdCommand.activate(command);
                        //    dateChange = new Date();
                        break;
                    case REMOVE_BY_ID:
                        removeByIdCommand.activate(command);
                        //    dateChange = new Date();
                        break;
                    case CLEAR:
                        clearCommand.activate(command);
                        //    dateChange = new Date();
                        break;
//                    case "save":
//                        saveCommand.activate();
//                        break;
                    case EXECUTE_SCRIPT:
                        executeScriptCommand.activate(command);
                        break;
//                    case "exit":
//                        exitCommand.activate();
//                        break;
                    case ADD_IF_MIN:
                        addIfMinCommand.activate(command);
                        //      dateChange = new Date();
                        break;
                    case REMOVE_GREATER:
                        removeGreaterCommand.activate(command);
                        //     dateChange = new Date();
                        break;
                    case REMOVE_LOWER:
                        removeLowerCommand.activate(command);
                        //      dateChange = new Date();
                        break;
                    case GROUP_COUNTING_BY_COORDINATES:
                        groupCountingByCoordinatesCommand.activate(command);
                        break;
                    case FILTER_CONTAINS_NAME:
                        filterContainsNameCommand.activate(command);
                        break;
                    case PRINT_FIELD_DESCENDING_PRICE:
                        printFieldDescendingPriceCommand.activate(command);
                        break;
                    default:
                        System.out.println("Неопознанная команда. Наберите 'help' для получения доступных команд.");
                }
            } catch (NumberFormatException ex) {
                System.out.println("Где-то проблема с форматом записи числа.Команда не выполнена");
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            System.out.println("Отсутствует аргумент.");
        }
        return null;
    }
}
