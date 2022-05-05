package de.xnonymous.usefulapi.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Cooldown {

    private final UUID who;
    private final int howLong;
    private final String identify;
    private final long when = System.currentTimeMillis();

    public int howLong() {
        long l = System.currentTimeMillis();

        return howLong - (int) ((l - when)) / 1000;
    }

}
