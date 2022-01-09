package crypto.lab1;

public class Task2 {
    public static void main(String[] args) {
        final String text = "Now the first one who will post the link to this document to our chat will receive +1 score. Simple.";
        final String key = "key";
        System.out.println(new String(Utils.encode(new String(Utils.encode(text, key)), key)));
    }
}
