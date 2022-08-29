package fr.blockincraft.faylisia.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.api.objects.Response;
import fr.blockincraft.faylisia.api.objects.State;
import fr.blockincraft.faylisia.utils.FileUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import java.io.*;

public class RequestHandler extends HandlerWrapper {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        ServletOutputStream outputStream = response.getOutputStream();
        String uri = request.getRequestURI();

        if (uri.equals("/resource_pack")) {
            File resourcePack = FileUtils.getResourcePack();

            FileInputStream fileInputStream = new FileInputStream(resourcePack);
            byte [] data = new byte [64*1024];
            for(int read; (read = fileInputStream.read(data)) > -1;) {
                outputStream.write(data, 0, read);
            }

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.flush();
            return;
        }
        if (uri.equals("/items")) {
            try {
                returnItems(outputStream, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }
        if (uri.equals("/state")) {
            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(objectMapper.writeValueAsString(new State(Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), true)));
            return;
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        outputStream.println(objectMapper.writeValueAsString(new Response("Invalid request")));
        outputStream.flush();
    }

    public void returnItems(ServletOutputStream outputStream, HttpServletResponse response) throws IOException {
        Registry registry = Faylisia.getInstance().getRegistry();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(new Response("items", registry.getItems()));

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }
}
