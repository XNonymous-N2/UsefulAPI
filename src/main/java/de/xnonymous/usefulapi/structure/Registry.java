package de.xnonymous.usefulapi.structure;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Registry<T> {

    @Getter
    protected final List<T> objects;

    /**
     * Constructor to init the object list.
     */
    public Registry() {
        this.objects = new ArrayList<>();
    }

    /**
     * Register a object by adding it
     * to the list.
     *
     * @param object
     */

    @SafeVarargs
    public final void register(T... object) {
        this.objects.addAll(Arrays.asList(object));
    }

    /**
     * Unregister a object by removing
     * it from the list.
     *
     * @param objects
     */
    @SafeVarargs
    public final void unregister(T... objects) {
        this.objects.removeAll(Arrays.asList(objects));
    }

    /**
     * Get a object by a class
     * by searching in the list.
     *
     * @param c
     * @return
     */
    public final T getByClass(Class<? extends T> c) {
        return this.objects.stream().filter(object -> object.getClass().equals(c)).findFirst().orElse(null);
    }

}