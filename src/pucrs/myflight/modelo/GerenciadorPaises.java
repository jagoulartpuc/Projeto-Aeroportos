package pucrs.myflight.modelo;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class GerenciadorPaises {

    private ArrayList<Pais> paises;

    public GerenciadorPaises() {
        this.paises = new ArrayList<>();
    }

    public void adicionar(Pais pais) {
        paises.add(pais);
    }

    public ArrayList<Pais> listarPaises() {
        return new ArrayList<>(paises);
    }

    public Pais buscarPorSigla(String sigla) {
        for (Pais p1: paises) {
            if (p1.getSigla() == sigla) {
                return p1;
            }

        }
        return null;
    }

    public void carregaDados(String nomeArq) throws IOException {
        Path path = Paths.get(nomeArq);
        try (Scanner sc = new Scanner(Files.newBufferedReader(path, Charset.forName("utf8")))) {
            sc.useDelimiter("[;\n]"); // separadores: ; e nova linha
            String header = sc.nextLine(); // pula cabeçalho
            String sigla, nome;
            while (sc.hasNext()) {
                sigla = sc.next();
                nome = sc.next();
                Pais p = new Pais(sigla, nome);
                adicionar(p);
                //System.out.format("%s - %s\n", cod, nome);
            }
        }
    }
}
