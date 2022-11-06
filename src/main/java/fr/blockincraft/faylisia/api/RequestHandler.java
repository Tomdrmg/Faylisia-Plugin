package fr.blockincraft.faylisia.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import fr.blockincraft.faylisia.Faylisia;
import fr.blockincraft.faylisia.Registry;
import fr.blockincraft.faylisia.api.objects.Response;
import fr.blockincraft.faylisia.api.objects.State;
import fr.blockincraft.faylisia.utils.FileUtils;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.bukkit.Bukkit;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerWrapper;

import java.io.*;

public class RequestHandler extends HandlerWrapper {
    private static final Registry registry = Faylisia.getInstance().getRegistry();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
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

        /*if (uri.startsWith("/player/") && uri.length() > 8) {
            returnPlayer(outputStream, response, uri.substring(8));
            return;
        }
        if (uri.startsWith("/uuid/") && uri.length() > 6) {
            returnUuidOf(outputStream, response, uri.substring(6));
            return;
        }
        if (uri.startsWith("/inventory/") && uri.length() > 11) {
            returnInventory(outputStream, response, uri.substring(11));
            return;
        }
        if (uri.equals("/utils/build_display")) {
            returnBuiltDisplay(outputStream, request, response);
            return;
        }
        if (uri.equals("/items")) {
            returnItems(outputStream, response);
            return;
        }

        if (uri.equals("/armor_sets")) {
            returnArmorSets(outputStream, response);
            return;
        }

        if (uri.equals("/entity_types")) {
            returnEntityTypes(outputStream, response);
            return;
        }*/

        if (uri.equals("/state")) {
            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(objectMapper.writeValueAsString(new State(Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers(), true)));
            return;
        }

        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        outputStream.println(objectMapper.writeValueAsString(new Response("Invalid request")));
        outputStream.flush();
    }

    /*public void returnPlayer(ServletOutputStream outputStream, HttpServletResponse response, String uuidAsString) throws IOException {
        try {
            UUID uuid = UUID.fromString(uuidAsString);

            CustomPlayerDTO player = registry.getPlayer(uuid);

            if (player == null) {
                String json = objectMapper.writeValueAsString(new Response("Player don't exist or never connected!"));

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                outputStream.println(json);
                outputStream.flush();
                return;
            }

            String json = objectMapper.writeValueAsString(new Response("player", player));

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }

    public void returnUuidOf(ServletOutputStream outputStream, HttpServletResponse response, String name) throws IOException {
        UUID uuid = null;

        for (Map.Entry<UUID, CustomPlayerDTO> entry : registry.getPlayers().entrySet()) {
            if (entry.getValue().getLastName().equalsIgnoreCase(name)) {
                uuid = entry.getKey();
            }
        }

        try {
            String json = objectMapper.writeValueAsString(new Response("UUID", uuid));

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }

    public void returnInventory(ServletOutputStream outputStream, HttpServletResponse response, String uuidAsString) throws IOException {
        try {
            UUID uuid = UUID.fromString(uuidAsString);

            CustomPlayerDTO player = registry.getPlayer(uuid);

            if (player == null || player.getLastInventoryAsJson() == null || player.getLastInventoryAsJson().isEmpty()) {
                String json = objectMapper.writeValueAsString(new Response("Player don't or never connected!"));

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                outputStream.println(json);
                outputStream.flush();
                return;
            }

            String json = "{\"success\":true,\"inventory\":" + player.getLastInventoryAsJson() + "}";

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }

    public void returnBuiltDisplay(ServletOutputStream outputStream, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            BufferedReader reader = request.getReader();

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = reader.readLine()) != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
            }

            ObjectMapper mapper = new ObjectMapper();
            SimpleModule module = new SimpleModule();
            module.addDeserializer(CustomItemStack.class, new CustomItemStackDeserializer());
            mapper.registerModule(module);

            CustomItemStack customItemStack = mapper.readValue(sb.toString(), CustomItemStack.class);

            if (customItemStack == null) {
                String json = objectMapper.writeValueAsString(new Response("Invalid custom item stack data send"));

                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                outputStream.println(json);
                outputStream.flush();
                return;
            }

            ItemStack model = customItemStack.getAsItemStack();

            String json = objectMapper.writeValueAsString(new Response("display", new ItemDisplayData(model)));

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }

    public void returnItems(ServletOutputStream outputStream, HttpServletResponse response) throws IOException {
        Registry registry = Faylisia.getInstance().getRegistry();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(new Response("items", registry.getItems()));

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }

    public void returnArmorSets(ServletOutputStream outputStream, HttpServletResponse response) throws IOException {
        Registry registry = Faylisia.getInstance().getRegistry();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(new Response("armor_sets", registry.getArmorSets()));

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }

    public void returnEntityTypes(ServletOutputStream outputStream, HttpServletResponse response) throws IOException {
        Registry registry = Faylisia.getInstance().getRegistry();
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String json = objectMapper.writeValueAsString(new Response("entity_types", registry.getEntityTypes()));

            response.setStatus(HttpServletResponse.SC_OK);
            outputStream.println(json);
            outputStream.flush();
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            outputStream.println(objectMapper.writeValueAsString(new Response("Internal java error: " + e.getMessage())));
            outputStream.flush();
        }
    }

    public static class ItemDisplayData {
        private final String name;
        private final String[] lore;

        public ItemDisplayData(ItemStack itemStack) {
            ItemMeta meta = itemStack.getItemMeta();

            if (meta != null) {
                this.name = meta.getDisplayName();

                List<String> lore = meta.getLore();
                this.lore = lore == null ? new String[0] : lore.toArray(new String[0]);
            } else {
                this.name = itemStack.getType().name().toLowerCase(Locale.ROOT);
                this.lore = new String[0];
            }
        }

        public String getName() {
            return name;
        }

        public String[] getLore() {
            return lore;
        }
    }*/
}
