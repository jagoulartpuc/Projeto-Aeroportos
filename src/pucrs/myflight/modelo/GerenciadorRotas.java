package pucrs.myflight.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;
import java.io.BufferedReader;

public class GerenciadorRotas {

    private ArrayList<Rota> rotas;

    public GerenciadorRotas() {
        this.rotas = new ArrayList<>();
    }

    public void ordenarCias() {
        Collections.sort(rotas);
    }

    public void ordenarNomesCias() {
        rotas.sort((Rota r1, Rota r2) ->
                r1.getCia().getNome().compareTo(
                        r2.getCia().getNome()));
    }

    public void ordenarNomesAeroportos() {
        rotas.sort((Rota r1, Rota r2) ->
                r1.getOrigem().getNome().compareTo(
                        r2.getOrigem().getNome()));
    }

    public void ordenarNomesAeroportosCias() {
        rotas.sort((Rota r1, Rota r2) -> {
            int result = r1.getOrigem().getNome().compareTo(
                    r2.getOrigem().getNome());
            if (result != 0)
                return result;
            return r1.getCia().getNome().compareTo(
                    r2.getCia().getNome());
        });
    }

    public void adicionar(Rota r) {
        rotas.add(r);
    }

    public ArrayList<Rota> listarTodas() {
        return new ArrayList<>(rotas);
    }

    public ArrayList<Rota> buscarOrigem(String codigo) {
        ArrayList<Rota> result = new ArrayList<>();
        for (Rota r : rotas)
            if (r.getOrigem().getCodigo().equals(codigo))
                result.add(r);
        return result;
    }

    public void carregaDados(String nomeArq, GerenciadorAeronaves gerAvioes, GerenciadorAeroportos gerAero, GerenciadorCias gerCias) {

        Path path2 = Paths.get(nomeArq);
        try (BufferedReader br = Files.newBufferedReader(path2, Charset.defaultCharset()))
        {
            String header = br.readLine();
            String linha = null;

            while((linha = br.readLine()) != null) {
                Scanner sc = new Scanner(linha).useDelimiter(";"); // separador é ;
                String codCia, codAeroOrigem, codAeroDestino, codeshare, paradas, codAeronave;

                codCia = sc.next();
                codAeroOrigem = sc.next();
                codAeroDestino = sc.next();
                codeshare = sc.next();
                paradas = sc.next();
                codAeronave = sc.next();

                Rota rota = new Rota(gerCias.buscarCodigo(codCia), gerAero.buscarCodigo(codAeroOrigem),
                        gerAero.buscarCodigo(codAeroDestino), gerAvioes.buscarCodigo(codAeronave));
                adicionar(rota);
            }
        }
        catch (IOException x) {
            System.err.format("Erro na manipulação do arquivo.");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }


}
