package fr.blockincraft.faylisia.utils;

import fr.blockincraft.faylisia.core.dto.CustomPlayerDTO;
import fr.blockincraft.faylisia.items.CustomItemStack;
import fr.blockincraft.faylisia.items.event.Handlers;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HandlersUtils {
    /**
     * Call all {@link Handlers} of a player
     * @param customPlayer player to get handlers
     * @param handlerMethodName handler method name
     * @param params params of the handler method, we use an array to keep order
     */
    public static void callHandlers(CustomPlayerDTO customPlayer, String handlerMethodName, Parameter[] params) {
        // Make a model to get method
        Handlers handlersModel = new Handlers() {};

        // Get all params type
        Class<?>[] paramsType = new Class[params.length + 3];

        for (int i = 0; i < params.length; i++) {
            paramsType[i] = params[i].associatedClass;
        }

        paramsType[paramsType.length - 3] = boolean.class;
        paramsType[paramsType.length - 2] = boolean.class;
        paramsType[paramsType.length - 1] = CustomItemStack.class;

        // Try to get handler method
        Method method = null;

        try {
            method = handlersModel.getClass().getMethod(handlerMethodName, paramsType);
        } catch (NoSuchMethodException e) {
            StringBuilder sb = new StringBuilder("Cannot found method '" + handlerMethodName + "(");

            for (int i = 0; i < paramsType.length; i++) {
                Class<?> param = paramsType[i];

                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(param.getName());
            }

            sb.append(")'!");

            throw new RuntimeException(sb.toString());
        }

        // Retrieve all handlers
        List<CustomPlayerDTO.HandlersWithItemStack> mainHandHandlers = new ArrayList<>(Arrays.asList(customPlayer.getMainHandHandler()));
        List<CustomPlayerDTO.HandlersWithItemStack> armorSetHandlers = new ArrayList<>(Arrays.asList(customPlayer.getArmorSetHandlers()));
        List<CustomPlayerDTO.HandlersWithItemStack> armorSlotHandlers = new ArrayList<>(Arrays.asList(customPlayer.getArmorSlotHandlers()));
        List<CustomPlayerDTO.HandlersWithItemStack> othersHandlers = new ArrayList<>(Arrays.asList(customPlayer.getOthersHandlers()));
        List<CustomPlayerDTO.HandlersWithItemStack> miscHandlers = new ArrayList<>(Arrays.asList(customPlayer.getMiscHandlers()));

        // Make final params
        Object[] finalParams = new Object[params.length + 3];
        for (int i = 0; i < params.length; i++) {
            finalParams[i] = params[i].value;
        }

        // Call each handler
        finalParams[finalParams.length - 3] = true;
        finalParams[finalParams.length - 2] = false;
        for (CustomPlayerDTO.HandlersWithItemStack handler : mainHandHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();

            try {
                method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        finalParams[finalParams.length - 3] = false;
        finalParams[finalParams.length - 2] = true;
        for (CustomPlayerDTO.HandlersWithItemStack handler : armorSetHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();

            try {
                method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        for (CustomPlayerDTO.HandlersWithItemStack handler : armorSlotHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();

            try {
                method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        finalParams[finalParams.length - 3] = false;
        finalParams[finalParams.length - 2] = false;
        for (CustomPlayerDTO.HandlersWithItemStack handler : othersHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();

            try {
                method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        for (CustomPlayerDTO.HandlersWithItemStack handler : miscHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();

            try {
                method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }
    }

    /**
     * Apply all {@link Handlers} of a player on a value using a handler methods
     * @param customPlayer player to get handlers
     * @param handlerMethodName handler method name
     * @param value value to apply handlers on it
     * @param params params of the handler method
     * @return value after applying handlers
     * @param <T> type of value
     */
    public static <T> T getValueWithHandlers(CustomPlayerDTO customPlayer, String handlerMethodName, T value, Class<?> valueClass, Parameter[] params) {
        // Make a model to get method
        Handlers handlersModel = new Handlers() {};

        // Get all params type
        Class<?>[] paramsType = new Class[params.length + 4];

        for (int i = 0; i < params.length; i++) {
            paramsType[i] = params[i].associatedClass;
        }

        paramsType[paramsType.length - 4] = valueClass;
        paramsType[paramsType.length - 3] = boolean.class;
        paramsType[paramsType.length - 2] = boolean.class;
        paramsType[paramsType.length - 1] = CustomItemStack.class;

        // Try to get handler method
        Method method = null;

        try {
            method = handlersModel.getClass().getMethod(handlerMethodName, paramsType);
        } catch (NoSuchMethodException e) {
            StringBuilder sb = new StringBuilder("Cannot found method '" + handlerMethodName + "(");

            for (int i = 0; i < paramsType.length; i++) {
                Class<?> param = paramsType[i];

                if (i > 0) {
                    sb.append(", ");
                }

                sb.append(param.getName());
            }

            sb.append(")'!");

            throw new RuntimeException(sb.toString());
        }

        // Retrieve all handlers
        List<CustomPlayerDTO.HandlersWithItemStack> mainHandHandlers = new ArrayList<>(Arrays.asList(customPlayer.getMainHandHandler()));
        List<CustomPlayerDTO.HandlersWithItemStack> armorSetHandlers = new ArrayList<>(Arrays.asList(customPlayer.getArmorSetHandlers()));
        List<CustomPlayerDTO.HandlersWithItemStack> armorSlotHandlers = new ArrayList<>(Arrays.asList(customPlayer.getArmorSlotHandlers()));
        List<CustomPlayerDTO.HandlersWithItemStack> othersHandlers = new ArrayList<>(Arrays.asList(customPlayer.getOthersHandlers()));
        List<CustomPlayerDTO.HandlersWithItemStack> miscHandlers = new ArrayList<>(Arrays.asList(customPlayer.getMiscHandlers()));

        // Make final params
        Object[] finalParams = new Object[params.length + 4];
        for (int i = 0; i < params.length; i++) {
            finalParams[i] = params[i].value;
        }

        // Apply each handler on value
        finalParams[finalParams.length - 3] = true;
        finalParams[finalParams.length - 2] = false;
        for (CustomPlayerDTO.HandlersWithItemStack handler : mainHandHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();
            finalParams[finalParams.length - 4] = value;

            try {
                value = (T) method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        finalParams[finalParams.length - 3] = false;
        finalParams[finalParams.length - 2] = true;
        for (CustomPlayerDTO.HandlersWithItemStack handler : armorSetHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();
            finalParams[finalParams.length - 4] = value;

            try {
                value = (T) method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        for (CustomPlayerDTO.HandlersWithItemStack handler : armorSlotHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();
            finalParams[finalParams.length - 4] = value;

            try {
                value = (T) method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        finalParams[finalParams.length - 3] = false;
        finalParams[finalParams.length - 2] = false;
        for (CustomPlayerDTO.HandlersWithItemStack handler : othersHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();
            finalParams[finalParams.length - 4] = value;

            try {
                value = (T) method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        for (CustomPlayerDTO.HandlersWithItemStack handler : miscHandlers) {
            finalParams[finalParams.length - 1] = handler.itemStack();
            finalParams[finalParams.length - 4] = value;

            try {
                value = (T) method.invoke(handler.handlers(), finalParams);
            } catch (IllegalAccessException | InvocationTargetException | ClassCastException e) {
                e.printStackTrace();
                throw new RuntimeException("Error when calling handler method: " + e.getMessage());
            }
        }

        return value;
    }

    public record Parameter(Object value, Class<?> associatedClass) {}
}
