package model;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

// Inside CountryRanking class
public class CountryRanking {
    private ExecutorService executor = Executors.newFixedThreadPool(5);

    public CompletableFuture<CountryList> calculate(MyLinkedList list) {
        return CompletableFuture.supplyAsync(() -> {
            CountryList listCountry = new CountryList();
            MyNode currentMyNode = list.getHeadElement();
            while (currentMyNode != null) {
                listCountry.append(currentMyNode.score, currentMyNode.country);
                currentMyNode = currentMyNode.next;
            }
            listCountry.recalibrateList();
            return listCountry;
        }, executor);
    }
}
