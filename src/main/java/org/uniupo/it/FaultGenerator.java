package org.uniupo.it;

import org.uniupo.it.model.Fault;
import org.uniupo.it.model.FaultType;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class FaultGenerator extends Thread {
    private final String machineId;
    private final Random random;
    private volatile boolean running;
    private final List<String> genericFaultDescriptions;


    public FaultGenerator(String machineId) {
        this.machineId = machineId;
        this.random = new Random();
        this.running = true;
        this.genericFaultDescriptions = List.of(
                "Rilevato surriscaldamento del motore",
                "Malfunzionamento della pompa dell'acqua",
                "Guasto del sistema di pressione",
                "Errore del sensore di temperatura",
                "Errore di comunicazione della scheda di controllo",
                "Problema di riscaldamento della caldaia",
                "Malfunzionamento del sensore del contenitore dei rifiuti",
                "Meccanismo di macinazione bloccato",
                "Malfunzionamento dell'unità di visualizzazione"

        );
    }

    @Override
    public void run() {
        while (running) {
            try {
                if (random.nextDouble() < 0.10) {
                    Fault fault = generateFault();

                    System.out.println("Generated fault: " + fault);
                }
                Thread.sleep(random.nextInt(300000) + 300000); // 5-10 minuti
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private Fault generateFault() {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String description = genericFaultDescriptions.get(
                random.nextInt(genericFaultDescriptions.size()));

        return new Fault(
                machineId,
                description,
                111,
                timestamp,
                UUID.randomUUID(),
                FaultType.GUASTO_GENERICO
        );
    }

    public void stopGenerator() {
        running = false;
        interrupt();
    }
}