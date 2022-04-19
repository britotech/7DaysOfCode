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
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {

    public static void main(String[] args) {

        try {
            var apiKey = obterApiKey();
            validarApiKey(apiKey);

            var json = obterJsonResposta(apiKey);
            validarInexistenciaErroResposta(json);

            var filmes = obterFilmes(json);
            filmes.forEach(f -> System.out.println(f.getTitle()));
            filmes.forEach(f -> System.out.println(f.getImage()));
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

        var pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher comparator = pattern.matcher(texto);
        if (comparator.find(0)) {
            return comparator.group("meuGrupo");
        }

        return "";
    }

    private static List<Filme> obterFilmes(String json) {
        var items = extrairConteudo(json, "\\[(?<meuGrupo>.*?)\\]");
        var retorno = items.split("\\}");

        return Arrays.stream(retorno).map(item -> {
            var filme = new Filme();
            filme.setId(extrairConteudo(item, "\\\"id\":\"(?<meuGrupo>.*?)\\\""));
            filme.setRank(extrairConteudo(item, "\\\"rank\":\"(?<meuGrupo>.*?)\\\""));
            filme.setTitle(extrairConteudo(item, "\\\"title\":\"(?<meuGrupo>.*?)\\\""));
            filme.setFullTitle(extrairConteudo(item, "\\\"fullTitle\":\"(?<meuGrupo>.*?)\\\""));
            filme.setYear(extrairConteudo(item, "\\\"year\":\"(?<meuGrupo>.*?)\\\""));
            filme.setImage(extrairConteudo(item, "\\\"image\":\"(?<meuGrupo>.*?)\\\""));
            filme.setCrew(extrairConteudo(item, "\\\"crew\":\"(?<meuGrupo>.*?)\\\""));
            filme.setImDbRating(extrairConteudo(item, "\\\"imDbRating\":\"(?<meuGrupo>.*?)\\\""));
            filme.setImDbRatingCount(extrairConteudo(item, "\\\"imDbRatingCount\":\"(?<meuGrupo>.*?)\\\""));
            return filme;
        }).collect(Collectors.toList());
    }
}

class Filme {

    private String id;
    private String rank;
    private String title;
    private String fullTitle;
    private String year;
    private String image;
    private String crew;
    private String imDbRating;
    private String imDbRatingCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRank() {
        return rank;
    }

    public void setRank(String rank) {
        this.rank = rank;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFullTitle() {
        return fullTitle;
    }

    public void setFullTitle(String fullTitle) {
        this.fullTitle = fullTitle;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCrew() {
        return crew;
    }

    public void setCrew(String crew) {
        this.crew = crew;
    }

    public String getImDbRating() {
        return imDbRating;
    }

    public void setImDbRating(String imDbRating) {
        this.imDbRating = imDbRating;
    }

    public String getImDbRatingCount() {
        return imDbRatingCount;
    }

    public void setImDbRatingCount(String imDbRatingCount) {
        this.imDbRatingCount = imDbRatingCount;
    }
}

