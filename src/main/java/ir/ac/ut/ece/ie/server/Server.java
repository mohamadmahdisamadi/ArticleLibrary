package ir.ac.ut.ece.ie.server;

import ir.ac.ut.ece.ie.server.common.resultpattern.ApiResult;
import ir.ac.ut.ece.ie.utils.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Server {
    private final int PORT = 9092;
    private static final String SERVER_PACKAGE = "ir.ac.ut.ece.ie.server.";
    private static final String ENDPOINTS_PACKAGE = SERVER_PACKAGE + "endpoints.";
    private static final String STATIC_FILES_PATH;

    static {
        STATIC_FILES_PATH = System.getProperty("user.dir")
                + "/src/main/java/ir/ac/ut/ece/ie/server/static/";
    }


    public void start() throws IOException {
        ServerSocket serverSocket = new ServerSocket(PORT);
        Socket socket;
        System.out.println(String.format("Server started on port %s...\n\n", String.valueOf(PORT)));

        while ((socket = serverSocket.accept()) != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String readLine = reader.readLine();
            if (readLine == null)
                continue;

            System.out.println("Received new request: " + readLine);

            try {
                StringTokenizer tokenizer = new StringTokenizer(readLine, " ");
                String httpMethod = tokenizer.nextToken();
                String url = tokenizer.nextToken();
                if (url.startsWith("/"))
                    url = url.substring(1);

                int contentLength = extractContentLength(reader);

                Map<String, String> params = new HashMap<>();

                Pair<String, String> urlAndData = extractDataFromRoute(url);
                if (urlAndData.second != null) {
                    url = urlAndData.first;
                    params.put("id", urlAndData.second);
                    System.out.println("Received a parameter from route: " + urlAndData.second);
                }

                Pair<String, Map<String, String>> urlAndParams = separatePathFromQueryParams(url);
                String path = urlAndParams.first;
                params.putAll(urlAndParams.second);

                if (urlAndParams.second.isEmpty() == false) {
                    System.out.println("Extracted query params:");
                    for (Map.Entry<String, String> entry : urlAndParams.second.entrySet())
                        System.out.println(String.format("    %s: %s", entry.getKey(), entry.getValue()));
                }

                if ("POST".equalsIgnoreCase(httpMethod) && contentLength > 0) {
                    handlePost(socket, reader, path, contentLength);
                } else if ("DELETE".equalsIgnoreCase(httpMethod)) {
                    handleDelete(socket, path, params);
                } else if ("GET".equalsIgnoreCase(httpMethod)) {
                    handleGet(socket, path, params);
                } else if ("PUT".equalsIgnoreCase(httpMethod) && contentLength > 0) {
                    handlePut(socket, reader, path, params, contentLength);
                }

            } catch (Exception e) {
                String header = "HTTP/1.1 404 Page Not Found\r\n\r\n";
                socket.getOutputStream().write(header.getBytes());

            } finally {
                socket.getOutputStream().flush();
                socket.close();
                System.out.println("────────────────────────────────────────────────────────────────────────────────────────");
            }

        }
        serverSocket.close();
    }



    private int extractContentLength(BufferedReader reader) throws Exception {
        String headerLine;
        int contentLength = 0;

        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex > 0) {
                String headerName = headerLine.substring(0, colonIndex).trim();
                String headerValue = headerLine.substring(colonIndex + 1).trim();

                if (headerName.equalsIgnoreCase("Content-Length"))
                    contentLength = Integer.parseInt(headerValue);
            }
        }
        return contentLength;
    }

    private Pair<String, Map<String, String>> separatePathFromQueryParams(String url) {
        Map<String, String> queryParams = new HashMap<>();
        String path;
        int questionMarkIndex = url.indexOf('?');
        if (questionMarkIndex != -1) {
            path = url.substring(0, questionMarkIndex);
            String queryString = url.substring(questionMarkIndex + 1);
            queryParams.putAll(parseQueryParams(queryString));
        } else {
            path = url;
        }

        return new Pair<>(path, queryParams);
    }

    private Pair<String, String> extractDataFromRoute(String url) {
        int slashIndex = url.indexOf('/');
        if (slashIndex == -1) {
            return new Pair<>(url, null);
        } else {
            return new Pair<>(url.substring(0, slashIndex), url.substring(slashIndex + 1));
        }
    }

    private Map<String, String> parseQueryParams(String queryString) {
        Map<String, String> params = new HashMap<>();
        if (queryString == null || queryString.isEmpty())
            return params;

        StringTokenizer tokenizer = new StringTokenizer(queryString, "&");
        while (tokenizer.hasMoreTokens()) {
            String parameterAsString = tokenizer.nextToken();
            int equalsIndex = parameterAsString.indexOf('=');
            if (equalsIndex != -1) {
                String parameterName = parameterAsString.substring(0, equalsIndex);
                String parameterValue = parameterAsString.substring(equalsIndex + 1);

                parameterValue = decodeUrl(parameterValue);
                params.put(parameterName, parameterValue);
            }
        }
        return params;
    }

    private String decodeUrl(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }

        value = value.replace("+", " ");

        StringBuilder result = new StringBuilder();
        int i = 0;
        while (i < value.length()) {
            if (value.charAt(i) == '%' && i + 2 < value.length()) {
                try {
                    String hex = value.substring(i + 1, i + 3);
                    int decoded = Integer.parseInt(hex, 16);
                    result.append((char) decoded);
                    i += 3;
                } catch (NumberFormatException e) {
                    result.append(value.charAt(i));
                    i++;
                }
            } else {
                result.append(value.charAt(i));
                i++;
            }
        }
        return result.toString();
    }


    private void handlePost(Socket socket, BufferedReader reader, String handlerClassName, int contentLength) throws Exception {
        byte[] data = {};
        try {
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars);
            String requestBody = new String(bodyChars);

            if (requestBody != null && requestBody.isEmpty() == false)
                System.out.println("\nPOST request body: " + requestBody);

            Class<?> c = Class.forName(ENDPOINTS_PACKAGE + "post." + handlerClassName);
            Object page = c.getDeclaredConstructor().newInstance();

            Method method = c.getMethod("handle", String.class);

            data = (byte[]) method.invoke(page, requestBody);
        } catch (Exception e) {
            data = new ApiResult(false, e.getMessage()).toString().getBytes();
        } finally {
            String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: " + data.length + "\r\n\r\n";
            socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(data);
        }
    }

    private void handleGet(Socket socket, String pageName, Map<String, String> params) throws Exception {
        if (pageName.endsWith(".css")) {
            handleStaticFile(socket, pageName, "text/css");
            return;
        }

        try {
            Class<?> c = Class.forName(ENDPOINTS_PACKAGE + "get." + pageName);
            Object page = c.getDeclaredConstructor().newInstance();
            Method method = c.getMethod("getPage", Map.class);
            byte[] data = (byte[]) method.invoke(page, params);

            String header = "HTTP/1.1 200 OK\r\nContent-Type: text/html\r\nContent-Length: " + data.length + "\r\n\r\n";
            socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(data);

        } catch (Exception e) {
            String header = "HTTP/1.1 404 Page Not Found\r\n\r\n";
            socket.getOutputStream().write(header.getBytes());
        }
    }

    private void handleDelete(Socket socket, String handlerClassName, Map<String, String> params) throws Exception {
        byte[] data = {};
        try {
            Class<?> c = Class.forName(ENDPOINTS_PACKAGE + "delete." + handlerClassName);
            Object page = c.getDeclaredConstructor().newInstance();
            Method method = c.getMethod("handle", Map.class);
            data = (byte[]) method.invoke(page, params);

            String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: " + data.length + "\r\n\r\n";
            socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(data);
        } catch (Exception e) {
            data = new ApiResult(false, e.getMessage()).toString().getBytes();
        } finally {
            String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: " + data.length + "\r\n\r\n";
            socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(data);
        }

    }

    private void handlePut(Socket socket, BufferedReader reader, String handlerClassName, Map<String, String> params, int contentLength) throws Exception {
        byte[] data = {};
        try {
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars);
            String requestBody = new String(bodyChars);

            if (requestBody != null && requestBody.isEmpty() == false)
                System.out.println("\nPUT request body: " + requestBody);

            Class<?> c = Class.forName(ENDPOINTS_PACKAGE + "put." + handlerClassName);
            Object page = c.getDeclaredConstructor().newInstance();

            Method method = c.getMethod("handle", Map.class, String.class);

            data = (byte[]) method.invoke(page, params, requestBody);
        } catch (Exception e) {
            data = new ApiResult(false, e.getMessage()).toString().getBytes();
        } finally {
            String header = "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\nContent-Length: " + data.length + "\r\n\r\n";
            socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(data);
        }
    }


    private void handleStaticFile(Socket socket, String fileName, String contentType) {
        try {
            String sanitizedPath = fileName.replace("..", "").replace("//", "/");

            String fullPath = STATIC_FILES_PATH + sanitizedPath;

            File file = new File(fullPath);

            if (!file.exists() || !file.isFile()) {
                String error = "HTTP/1.1 404 Not Found\r\nContent-Type: text/plain\r\n\r\nFile Not Found";
                socket.getOutputStream().write(error.getBytes());
                return;
            }

            byte[] fileContent = Files.readAllBytes(file.toPath());

            String header = "HTTP/1.1 200 OK\r\n" +
                    "Content-Type: " + contentType + "\r\n" +
                    "Content-Length: " + fileContent.length + "\r\n" +
                    "Cache-Control: max-age=86400\r\n" +
                    "Connection: close\r\n" +
                    "\r\n";

            socket.getOutputStream().write(header.getBytes());
            socket.getOutputStream().write(fileContent);

        } catch (Exception e) {
            try {
                String error = "HTTP/1.1 500 Internal Server Error\r\nContent-Type: text/plain\r\n\r\nServer Error";
                socket.getOutputStream().write(error.getBytes());
            } catch (Exception ignored) {}
        }
    }

    public static void main(String[] args) throws IOException {
        Server dcs = new Server();
        try {
            dcs.start();
        } catch (Exception e) {
            System.err.println("Server Error: " + e.getMessage());
        }
    }
}