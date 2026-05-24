import entity.User;
import services.UserService;
import util.HibernateUtil;

import java.util.Scanner;


private static final Scanner scanner = new Scanner(System.in);
private static final UserService userService = new UserService();

public static void main(String[] args) {
    while (true) {
        printMenu();
        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1":
                    createUser();
                    break;
                case "2":
                    findUser();
                    break;
                case "3":
                    updateUser();
                    break;
                case "4":
                    deleteUser();
                    break;
                case "0":
                    shutdown();
                    return;
                default:
                    break;
            }
        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
        }
    }
}

private static void shutdown() {
    System.out.println("Завершение работы");
    HibernateUtil.shutdown();
    scanner.close();
}

private static void printMenu() {
    System.out.println("\n1. Создать пользователя");
    System.out.println("2. Найти пользователя по ID");
    System.out.println("3. Обновить данные пользователя");
    System.out.println("4. Удалить пользователя");
    System.out.println("0. Выход");
    System.out.print("Выбор: ");
}

private static void createUser() {
    System.out.print("Введите имя: ");
    String name = scanner.nextLine();
    System.out.print("Введите email: ");
    String email = scanner.nextLine();
    System.out.print("Введите возраст: ");
    int age = Integer.parseInt(scanner.nextLine());

    Long userId = userService.createUser(name, email, age);
    System.out.println("Создан пользователь с ID: " + userId);
}

private static void findUser() {
    System.out.print("Введите ID: ");
    Long id = Long.parseLong(scanner.nextLine());

    userService.getUser(id).ifPresentOrElse(
            user -> System.out.println(user),
            () -> System.out.println("Пользователь не найден")
    );
}

private static void updateUser() {
    System.out.print("Введите ID: ");
    Long id = Long.parseLong(scanner.nextLine());

    Optional<User> optionalUser = userService.getUser(id);

    if (optionalUser.isPresent()) {
        User user = optionalUser.get();

        System.out.println("Текущее имя: " + user.getName());
        String name = scanner.nextLine();

        System.out.println("Текущий email: " + user.getEmail());
        String email = scanner.nextLine();

        System.out.println("Текущий возраст: " + user.getAge());
        String ageStr = scanner.nextLine();

        userService.updateUser(user, name, email, ageStr);
    } else {
        System.out.println("Пользователь не найден");
    }
}

private static void deleteUser() {
    System.out.print("Введите ID для удаления: ");
    Long id = Long.parseLong(scanner.nextLine());

    try {
        userService.deleteUser(id);
        System.out.println("Пользователь удалён");
    } catch (RuntimeException e) {
        System.err.println(e.getMessage());
    }
}



