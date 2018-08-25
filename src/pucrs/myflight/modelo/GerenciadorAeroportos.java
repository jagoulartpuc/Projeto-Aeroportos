package pucrs.myflight.modelo;


import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class GerenciadorAeroportos {

    private ArrayList<Aeroporto> aeroportos;

    public GerenciadorAeroportos() {
        this.aeroportos = new ArrayList<>();
    }

    public void ordenarNomes() {
        Collections.sort(aeroportos);
    }

    public void adicionar(Aeroporto aero) {
        aeroportos.add(aero);
    }

    public ArrayList<Aeroporto> listarTodos() {
        return new ArrayList<>(aeroportos);
    }

    public Aeroporto buscarCodigo(String codigo) {
        for(Aeroporto a: aeroportos)
            if(a.getCodigo().equals(codigo))
                return a;
        return null;
    }

    public void carregaDados(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]"); // separadores: ; e nova linha
            String header = sc.nextLine(); // pula cabeçalho
            String cod, nome, siglaPais;
            double lati, longi;
            while (sc.hasNext()) {
                String linha = sc.nextLine();
                String[] dados = linha.split(";");
                cod = dados[0];
                lati = Double.parseDouble(dados[1]);
                longi = Double.parseDouble(dados[2]);
                nome = dados[3];
                siglaPais = dados[4];
                Geo localizacao = new Geo(lati,longi);
                Aeroporto nova = new Aeroporto(cod, nome, localizacao, siglaPais);
                adicionar(nova);
            }
        }
    }
}
