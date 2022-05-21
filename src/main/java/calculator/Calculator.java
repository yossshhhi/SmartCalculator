package calculator;

import java.math.BigInteger;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Calculator {
    static HashMap<String, BigInteger> map = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean flag = true;
        do {
            String input = scanner.nextLine();
            String assignment = "\\s*[a-zA-Z]+\\s*=\\s*-?(\\d|[a-zA-Z])+\\s*";
            if (input.isEmpty()) {
                continue;
            } else if (input.equals("/exit")) {
                System.out.println("Bye!");
                flag = false;
            } else if (input.matches(assignment)) {
                input = input.trim();
                List<String> pair = new ArrayList<>(Arrays.asList(input.split("\\s*=\\s*|\\s+")));
                if (map.containsKey(pair.get(1))) {
                    pair.set(1, String.valueOf(map.get(pair.get(1))));
                    map.put(pair.get(0), new BigInteger(pair.get(1)));
                } else if (pair.get(1).matches("[-+]?\\d+?")) {
                    map.put(pair.get(0), new BigInteger(pair.get(1)));
                } else {
                    errors(input);
                }
            } else {
                try {
                    if (calculate(postfixStack(infixList(input))).equals("null")) {
                        errors(input);
                    } else {
                        System.out.println(calculate(postfixStack(infixList(input))));
                    }
                } catch (Exception e) {
                    errors(input);
                }
            }
        } while (flag);
    }

    public static List<String> infixList(String input) {
        if (!check(input)) {
            return null;
        }
        List<String> infixList = new ArrayList<>();
        Pattern pattern = Pattern.compile("-?(\\d+|[-+*/^]+|[a-zA-Z]+)|\\(|\\)");
        Matcher matcher = pattern.matcher(input);
        while(matcher.find()) {
            infixList.add(matcher.group());
        }
        return infixList;
    }

    public static boolean check(String input) {
        boolean check = true;
        Pattern pattern = Pattern.compile("=");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            check = false;
        }
        return check;
    }

    public static List<String> postfixStack(List<String> infix) {
        Stack<String> temp = new Stack<>();
        List<String> postfixStack = new ArrayList<>();
        for (String element : infix) {
            if (element.matches("-?\\d+?|[a-zA-Z]+?")) {
                postfixStack.add(element);
            } else if (element.matches("\\(|\\)")) {
                if (element.matches("\\(")) {
                    temp.push(element);
                } else {
                    while (!temp.lastElement().matches("\\(")) {
                        postfixStack.add(temp.pop());
                    }
                    temp.pop();
                }
            } else if (element.matches("[-+*/^]+?")) {
                if (temp.isEmpty() || temp.lastElement().matches("\\(")) {
                    temp.push(element);
                } else if (precedenceLevel(temp.lastElement().charAt(0)) < precedenceLevel(element.charAt(0))) {
                    temp.push(element);
                } else if (precedenceLevel(temp.lastElement().charAt(0)) >= precedenceLevel(element.charAt(0))) {
                    postfixStack.add(temp.pop());
                    while (!temp.isEmpty() && temp.lastElement().matches("[-+*/^]+?")) {
                        if (precedenceLevel(temp.lastElement().charAt(0)) >= precedenceLevel(element.charAt(0)))
                            postfixStack.add(temp.pop());
                    }
                    temp.push(element);
                }
            }
        }
        while (!temp.isEmpty())
            postfixStack.add(temp.pop());
        return postfixStack;
    }

    public static String calculate(List<String> postfixStack) {
        Stack<String> temp = new Stack<>();
        for (String element : postfixStack) {
            if (element.matches("-?\\d+?")) {
                temp.push(element);
            } else if (element.matches("-?[a-zA-Z]+?")) {
                temp.push(valueOfVariableInMap(element));
            } else {
                if (element.length() > 1)
                    element = sequenceOfOperator(element);
                temp.push(String.valueOf(operation(element.charAt(0), new BigInteger(temp.pop()), new BigInteger(temp.pop()))));
            }
        }
        return temp.lastElement();
    }

    public static String valueOfVariableInMap(String variable) {
        return String.valueOf(map.get(variable));
    }

    public static String sequenceOfOperator(String operator) {
        if (operator.matches("\\-+"))
            if (operator.length() % 2 == 0)
                operator = "+";
            else
                operator = "-";
        else if (operator.matches("\\*+|\\/+"))
            operator = null;
        return operator;
    }

    public static int precedenceLevel(char op) {
        switch (op) {
            case '+':
            case '-':
                return 0;
            case '*':
            case '/':
                return 1;
            case '^':
                return 2;
            default:
                throw new IllegalArgumentException("Operator unknown: " + op);
        }
    }

    public static BigInteger operation(char ch, BigInteger two, BigInteger one) {
        BigInteger result = BigInteger.ZERO;
        switch (ch) {
            case '+' : result = one.add(two); break;
            case '-' : result = one.subtract(two); break;
            case '*' : result = one.multiply(two); break;
            case '/' : result = one.divide(two); break;
            case '^' : result = one.pow(two.intValue()); break;
        }
        return result;
    }

    public static void errors(String input) {
        String unknownCommand = "\\/.+";
        String invalidIdentifier = "\\w+\\s*=\\s*\\d+";
        String invalidAssignment = "[a-zA-Z]+\\s*=\\s*\\w+\\s*=*?.*?";
        String unknownVariable = "[a-zA-Z]+\\s*=\\s*[a-zA-Z]+|[a-zA-Z]+";
        if (input.equals("/help")) {
            System.out.println("The program calculates the sum, difference, multiply and divide of numbers");
        } else if (map.containsKey(input)) {
            System.out.println(map.get(input));
        } else if (input.matches(unknownCommand)) {
            System.out.println("Unknown command");
        } else if (input.matches(unknownVariable)) {
            System.out.println("Unknown variable");
        } else if (input.matches(invalidIdentifier)) {
            System.out.println("Invalid identifier");
        } else if (input.matches(invalidAssignment)) {
            System.out.println("Invalid assignment");
        } else {
            System.out.println("Invalid expression");
        }
    }
}



