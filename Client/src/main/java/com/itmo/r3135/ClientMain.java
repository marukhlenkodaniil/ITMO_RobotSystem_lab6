package com.itmo.r3135;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.PortUnreachableException;
import java.net.SocketAddress;
import java.nio.channels.UnresolvedAddressException;
import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Stream;
//Обязанности клиентского приложения:
//есть-Чтение команд из консоли.
//Валидация вводимых данных.
//есть-Сериализация введённой команды и её аргументов.
//есть-Отправка полученной команды и её аргументов на сервер.
//Обработка ответа от сервера (вывод результата исполнения команды в консоль).

public class ClientMain {
    public static void main(String[] args) {



        Scanner input = new Scanner(System.in);
        while (true) {
            //     System.out.println("Внимание! В тестовых целях сервер может обрабатывает 1 сообщение в 3 секунды!!!");
            System.out.println("Для начала работы с коллекцией ведите адрес сервера в формате \"адрес:порт\" или 'exit' для завершенеия программы.");
            System.out.print("//: ");
            if (!input.hasNextLine()) {
                break;
            }
            String inputString = input.nextLine();
            if (inputString.equals("exit")) {
                break;
            } else {
                try {
                    String[] trimString = inputString.trim().split(":", 2);
                    String addres = trimString[0];
                    int port = Integer.valueOf(trimString[1]);
                    SocketAddress socketAddress = new InetSocketAddress(addres, port);
                    System.out.println("Запуск прошёл успешно, Потр: " + port + ". Адрес: " + socketAddress);
                    ClientWorker worker = new ClientWorker(socketAddress);
                    if (worker.connectionCheck()) {
                        worker.startWork();
                        break;
                    }
                    ;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка в записи номера порта.");
                } catch (IndexOutOfBoundsException | UnresolvedAddressException e) {
                    System.out.println("Адрес введён некорректно.");
                } catch (PortUnreachableException e) {
                    System.out.println("Похоже, сервер по этому адрусе недоступен");
                } catch (IOException | InterruptedException e) {
                    System.out.println(e);
                }
            }

        }
        System.out.println("Работа программы завершена.");
        System.exit(0);
    }


}
