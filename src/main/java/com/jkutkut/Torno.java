package com.jkutkut;

import java.util.Random;
import java.util.concurrent.Semaphore;

/**
 * Considérese el caso de un parque público que dispone de tres puertas de
 * acceso. El acceso por cada una de las puertas del parque, está controlada
 * por un torno independiente, que envía un evento propio a una aplicación de
 * computador que debe contarlas y proporcionar en cualquier instante el
 * número total de visitantes que han entrado en el parque.
 * La solución concurrente inicial que se propone, se basa en las siguientes
 * ideas:
 * ■ Los eventos que genera cada torno van a ser gestionados por un
 * proceso independiente.
 * ■ Los procesos de control de los tornos se ejecutan de forma concurrente.
 * ■ En el programa existe una variable global entera “cuenta" que representa
 * el número de visitantes que ha entrado en el parque.
 * Cuando la actividad de los tornos ha concluido proporciona la información
 * sobre el número de visitantes que se han producido.
 *
 *
 * Para poder usar el semáforo, suponemos que el sistema que cuenta tiene
 * que gestionar un máximo de 1 visitante a la vez.
 */
public class Torno extends Thread {
    private final String name;

    private static final Semaphore semaphore = new Semaphore(1);
    private static int cuenta = 0;
    private static final int MIN = 5;
    private static final int MAX = 10;

    private static final int NUM_TORNOS = 3;

    public Torno(String name) {
        this.name = name;
    }

    public void run() {
        Random r = new Random();
        int people = r.nextInt(MIN, MAX);

        System.out.println(name + " abre sus puertas (" + people + " personas)");
        try {
            while (people-- > 0) {
                System.out.println(this.name + " gestionando entrada");
                Thread.sleep(r.nextInt(1000, 3000));
                allowSomeoneIn();
                System.out.println(this.name + " ha dejado entrar a alguien. Quedan " + people);
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println(name + " cierra sus puertas");
    }

    public static void allowSomeoneIn() throws InterruptedException {
        semaphore.acquire(); // To prevent data races
        cuenta++;
        semaphore.release();
    }

    public static void main(String[] args) {
        Thread[] tornos = new Thread[NUM_TORNOS];
        for (int i = 0; i < NUM_TORNOS; i++) {
            tornos[i] = new Torno("T" + i);
            tornos[i].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < NUM_TORNOS; i++) {
            try {
                tornos[i].join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("El parque ha recibido " + cuenta + " visitantes");
    }
}
