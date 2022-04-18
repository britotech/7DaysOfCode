package tech.brito.sevendaysofcode;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        try (var scanner = new Scanner(System.in)) {

            System.out.println("Insira a apiKey para consulta:");
            var apiKey = scanner.nextLine();
            if (isBlank(apiKey)) {
                System.out.println("Apikey não informada sistema será finalizado");
                return;
            }

            var request = buildHttpRequest(apiKey);
            var client = HttpClient.newHttpClient();
            var response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (Exception ex) {
            System.out.println("Falha ao obter filmes. " + ex.getMessage());
        }
    }

    private static boolean isBlank(String value) {
        return Objects.isNull(value) || value.trim().equals("");
    }

    private static HttpRequest buildHttpRequest(String apiKey) {
        var url = String.format("https://imdb-api.com/en/API/Top250Movies/%s", apiKey);
        return HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    }
}
