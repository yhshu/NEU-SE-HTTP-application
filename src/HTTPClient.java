import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class HTTPClient {
    public static void main(String args[]) throws IOException {
        while (true) {
            Socket socket = null;
            PrintStream writer = null;
            BufferedReader reader = null;
            ByteArrayOutputStream byteOS = new ByteArrayOutputStream();
            InputStream socketIS = null;

            Scanner sc = new Scanner(System.in);
            // 连接服务器
            socket = new Socket("localhost", 8888);
            // 发送请求头
            writer = new PrintStream(socket.getOutputStream());
            System.out.println("Enter the filename:");
            String fileName = sc.nextLine();

            writer.println("GET /" + fileName + " HTTP/1.1");
            writer.println("Host:localhost");
            writer.println("connetion:keep-alive");
            writer.println();
            writer.flush();

            // 接收响应状态
            socketIS = socket.getInputStream();
            DataInputStream dataIS = new DataInputStream(socketIS);

            int b = 0;
            ArrayList<Character> charList = new ArrayList<>();
            while (true) {
                b = dataIS.read();
                charList.add((char) b);
                if (b == '\n')
                    break;
            }
            StringBuilder firstLineOfResponse = new StringBuilder();
            for (char ch : charList) {
                firstLineOfResponse.append(String.valueOf(ch));
            }
            System.out.println(firstLineOfResponse);

            while (readLines(dataIS)) ;

            if (firstLineOfResponse.toString().contains("200 OK")) {
                // 读取响应数据，保存文件
                System.out.println("Transmission starts...");

                String saveLocation = "C:\\Users\\舒意恒\\Documents\\GitHub\\HTTP-application\\saveLocation"; // 保存的位置
                FileOutputStream fileOS = new FileOutputStream(saveLocation + "/" + fileName);

                byte[] bytes = new byte[1024];
                int len;
                while ((len = dataIS.read(bytes)) != -1) {
                    fileOS.write(bytes, 0, len);
                    fileOS.flush();
                }
                fileOS.close();

                System.out.println("Transmission complete.");

                if (fileName.endsWith(".html") || fileName.endsWith("htm"))// 请求的是HTML文档
                {
                    File HTMLdoc = new File(saveLocation + "/" + fileName);
                    Document doc = Jsoup.parse(HTMLdoc, "UTF-8");
                    Elements jpgs = doc.select("img[src$=.jpg]");
                    for (Element jpg : jpgs) {
                        String url = jpg.attr("src"); // 获得相对路径
                        FileOutputStream resourceOS = new FileOutputStream(saveLocation + "/" + url);
                        bytes = new byte[1024];
                        while ((len = dataIS.read(bytes)) != -1) {
                            resourceOS.write(bytes, 0, len);
                            resourceOS.flush();
                        }
                        resourceOS.close();
                    }
                }
            }
            socket.close();
        }
    }

    private static boolean readLines(InputStream is) throws IOException {
        boolean endTag = false;
        ArrayList<Character> charList = new ArrayList<>();
        int b = 0;
        while (true) {
            b = is.read();
            charList.add((char) b);
            if (b == '\n')
                break;
        }
        StringBuilder line = new StringBuilder();
        for (char ch : charList)
            line.append(String.valueOf(ch));
        if (line.toString().equals("\r\n"))
            endTag = true;
        return !endTag;
    }
}
