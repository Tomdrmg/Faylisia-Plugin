package fr.blockincraft.faylisia.commands.base;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandAction {
    boolean onlyPlayers();

    @NotNull
    String[] prefixes() default {};

    @NotNull
    String permission() default "";
}
