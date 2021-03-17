import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class changeIP {

    public static void main(String[] args) {
        System.out.println("-----------------------------------------------------------");
        System.out.println("Place changeIP.jar into root directory of Tomcat and run it");
        System.out.println("-----------------------------------------------------------");
        System.out.println();

        String setenvW = currentDir + "/bin/setenv.bat";
        String setenvL = currentDir + "/bin/setenv.sh";
        String ip = null;
        String jre = null;
        Scanner in = new Scanner(System.in);

        File[] files = {activemq, server, file1, file2, file3, file4, file5, file6};

        if (System.getProperty("os.name").contains("indow")) {
            ip = getIP();
            System.out.println("Input JAVA_HOME (ex. D:/jre) or press Enter");
            jre = in.nextLine();

            if (jre.isEmpty()) jre = getJRE();
            System.out.println("Java home is: " + System.getProperty("java.home"));
            changeLine(setenvW, "set JRE_HOME=", jre);

        } else if (System.getProperty("os.name").contains("nix")) {
            System.out.println("Input your IP:");
            ip = in.nextLine();
            System.out.println("Input JAVA_HOME");
            jre = in.nextLine();
            changeLine(setenvL, "export JRE_HOME=", jre);
        }

        System.out.println(ip);
        System.out.println(jre);

        for (File f : files) {
            changeIPByRegExp(f, ip);
        }
    }

    /**
     * Change IP inside the document by regular expression.
     * Save new file.
     *
     * @param filePath set File that it is needed to change.
     */
    public static void changeIPByRegExp(File filePath, String myIP) {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        byte[] buffer = new byte[0];

        try {
            fin = new FileInputStream(filePath);
        } catch (FileNotFoundException e) {
            System.out.println(filePath + " not found!");
        }
        try {
            assert fin != null;
            buffer = new byte[fin.available()];

        } catch (IOException e) {
            e.printStackTrace();
        }
        // https://mkyong.com/regular-expressions/how-to-validate-ip-address-with-regular-expression/

        String data = new String(buffer, Charset.forName("Windows-1251"));

        String pattern = "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])" + "\\." + "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])" +
                "\\." + "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])" + "\\." + "([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])";

        Pattern p = Pattern.compile(pattern);
        data = p.matcher(data).replaceAll(myIP);

        try {
            fout = new FileOutputStream(filePath.getPath());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            assert fout != null;
            fout.write(data.getBytes());
            fout.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return jre home. if Unix get path from command line.
     */
    public static String getJRE() {

        if (System.getProperty("os.name").contains("indow")) {
            return System.getProperty("java.home");

        } else {
            Scanner in = new Scanner(System.in);
            System.out.println("Insert path to your Java. Or set '/usr/lib/jvm/java-8-oracle'");
            String jre = in.nextLine();
            in.close();
            return jre;
        }
    }

    /**
     * @return current system charset. UTF_8 \ windows-1251
     */
    public static Charset charSet() {

        if (System.getProperty("os.name").contains("indow") && System.getProperty("user.language").contains("ru")) {
            return Charset.forName("Windows-1251");
        } else if (System.getProperty("os.name").contains("nix")) {
            return StandardCharsets.UTF_8;
        }
        return Charset.defaultCharset();
    }

    /**
     * @return String IP. If Unix, get ip from command line.
     */
    public static String getIP() {

        if (System.getProperty("os.name").contains("indow")) {
            InetAddress inetAddress = null;

            try {
                inetAddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
            assert inetAddress != null;
            return inetAddress.getHostAddress();

        } else {
            Scanner in = new Scanner(System.in);
            System.out.println("Insert your IP");
            String ip = in.nextLine();
            in.close();
            return ip;
        }
    }

    /**
     * Find file setenv.bat
     * Find lines JAVA_HOME, IBANK_HOME
     * Change values
     * Save the file
     * read all lines from the file. Change necessary lines.
     * It works with small files and with line = null
     *
     * @param filePath  set file path for a document
     * @param findThis set value that must found
     * @param putThis  set value that must put
     */
    public static void changeLine(String filePath, String findThis, String putThis) {
        File file = new File(filePath);
        List<String> list = new ArrayList<>();

        try {
            int size = Files.readAllLines(file.toPath(), charSet()).size();
            assert size != 0;

            for (int i = 0; i < size; i++) {

                if (Files.readAllLines(file.toPath(), charSet()).get(i).startsWith(findThis)) {
                    String put = findThis + putThis;
                    list.add(put);
                    continue;
                }
                list.add(Files.readAllLines(file.toPath(), charSet()).get(i));
            }
        } catch (IOException e) {
            System.out.println("File not found!");
        }

        // write new lines into the file.

        File fout = new File(filePath);
        FileOutputStream fos = null;
        BufferedWriter bw;

        try {
            fos = new FileOutputStream(fout);
        } catch (FileNotFoundException e) {
            System.out.println("File not found!");
        }
        assert fos != null;
        bw = new BufferedWriter(new OutputStreamWriter(fos));

        for (String s : list) {
            try {
                bw.append(s);
                bw.newLine();
            } catch (IOException e) {
                System.out.println("File not found!");
            }
        }

        // close

        try {
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Validator
     * @param ip
     * @return true if correct
     */
    public static boolean isValid(Pattern pattern, String ip) {
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

    static String currentDir = System.getProperty("user.dir");
    static File activemq;
    static File server;
    static File file1;
    static File file2;
    static File file3;
    static File file4;
    static File file5;
    static File file6;

    // List of files that is needed to change.
    static {
        activemq = new File(currentDir + "/conf/activemq.xml");
        server = new File(currentDir + "/conf/server.xml");
        file1 = new File(currentDir + "/webapps-public/ROOT/file1.xml");
        file2 = new File(currentDir + "/webapps-public/ROOT/file2.xml");
        file3 = new File(currentDir + "/webapps-public/ROOT/file3.xml");
        file4 = new File(currentDir + "/webapps-public/ROOT/file4.xml");
        file5 = new File(currentDir + "/webapps-local/ROOT/file5.xml");
        file6 = new File(currentDir + "/webapps-local/ROOT/file6.xml");
    }
}