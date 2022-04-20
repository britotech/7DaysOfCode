package tech.brito.sevendaysofcode;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        try {
            var apiKey = obterApiKey();
            validarApiKey(apiKey);

            var json = obterJsonResposta(apiKey);
            validarInexistenciaErroResposta(json);

            var movies = obterFilmes(json);
            movies.forEach(System.out::println);
        } catch (Exception ex) {
            System.out.println("Falha na obtenção da lista de filmes. " + ex.getMessage());
        }
    }

    private static String obterApiKey() {

        try (var scanner = new Scanner(System.in)) {
            System.out.println("Insira a apiKey para consulta:");
            return scanner.nextLine();
        }
    }

    private static void validarApiKey(String apiKey) {
        if (isBlank(apiKey)) {
            throw new RuntimeException("Apikey não informada!");
        }
    }

    private static boolean isBlank(String value) {
        return Objects.isNull(value) || value.trim().equals("");
    }

    private static String obterJsonResposta(String apiKey) throws IOException, InterruptedException {
        var request = buildHttpRequest(apiKey);
        var client = HttpClient.newHttpClient();
        var response = client.send(request, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    private static HttpRequest buildHttpRequest(String apiKey) {
        var url = String.format("https://imdb-api.com/en/API/Top250Movies/%s", apiKey);
        return HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
    }

    private static void validarInexistenciaErroResposta(String json) {
        var mensagemErro = extrairConteudo(json, "\\\"errorMessage\":\"(?<meuGrupo>.*?)\\\"");
        if (!isBlank(mensagemErro)) {
            throw new RuntimeException(mensagemErro);
        }
    }

    private static String extrairConteudo(String texto, String regex) {
        var matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(texto);
        return matcher.find(0) ? matcher.group("meuGrupo") : "";
    }

    private static List<Movie> obterFilmes(String json) {
        var items = extrairConteudo(json, "\\[(?<meuGrupo>.*?)\\]");

        return Arrays
                .stream(items.split("\\}"))
                .map(item -> new Movie(extrairConteudo(item, "\\\"id\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"rank\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"title\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"fullTitle\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"year\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"image\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"crew\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"imDbRating\":\"(?<meuGrupo>.*?)\\\""),
                                       extrairConteudo(item, "\\\"imDbRatingCount\":\"(?<meuGrupo>.*?)\\\"")))
                .collect(Collectors.toList());
    }
}