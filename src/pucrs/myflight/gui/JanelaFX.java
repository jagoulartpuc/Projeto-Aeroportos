package pucrs.myflight.gui;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import javax.swing.SwingUtilities;


import javafx.scene.Node;
import javafx.scene.control.TextField;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import pucrs.myflight.modelo.*;
import javafx.scene.control.TextField;


public class JanelaFX extends Application {

    final SwingNode mapkit = new SwingNode();

    private GerenciadorCias gerCias;
    private GerenciadorAeroportos gerAero;
    private GerenciadorRotas gerRotas;
    private GerenciadorAeronaves gerAvioes;
    private TextField campo;
    private GerenciadorMapa gerenciador;
    private GerenciadorPaises gerPaises;
    private EventosMouse mouse;
    private ComboBox<CiaAerea> comboCia;
    private ComboBox<Aeroporto> destinoBox;
    private ComboBox<Aeroporto> origemBox;
    private ComboBox<Pais> paisesBox;
    private TextField tempo;
    List<MyWaypoint> lstPoints = new ArrayList<>();


    @Override
    public void start(Stage primaryStage) throws Exception {

        setup();

        GeoPosition pos = new GeoPosition(gerAero.listarTodos().get(1).getLocal().getLatitude(), gerAero.listarTodos().get(1).getLocal().getLongitude());
        gerenciador = new GerenciadorMapa(pos, GerenciadorMapa.FonteImagens.VirtualEarth);
        mouse = new EventosMouse();
        gerenciador.getMapKit().getMainMap().addMouseListener(mouse);
        gerenciador.getMapKit().getMainMap().addMouseMotionListener(mouse);

        createSwingContent(mapkit);

        BorderPane pane = new BorderPane();
        GridPane leftPane = new GridPane();

        leftPane.setAlignment(Pos.CENTER);
        leftPane.setHgap(10);
        leftPane.setVgap(10);
        leftPane.setPadding(new Insets(10, 10, 10, 10));

        Button pesquisa = new Button("Pesquisar Rotas");
        Button pesquisa2 = new Button("Pesquisar Tráfego Mundial");
        Button pesquisa21 = new Button("Pesquisar Tráfego de Países");
        Button pesquisa4 = new Button("Pesquisar alcançe de origem");
        Button pesquisa3 = new Button("Pesquisar rotas com mesma origem e destino");
        comboCia = new ComboBox(FXCollections.observableList(gerCias.listarTodas()));
        comboCia.setPromptText("Selecione a cia");
        destinoBox = new ComboBox(FXCollections.observableList(gerAero.listarTodos()));
        destinoBox.setPromptText("Selecione o aeroporto de destino");
        paisesBox = new ComboBox(FXCollections.observableList(gerPaises.listarPaises()));
        paisesBox.setPromptText("Selecione o País");
        origemBox = new ComboBox(FXCollections.observableList(gerAero.listarTodos()));
        origemBox.setPromptText("Selecione o aeroporto de origem");
        tempo = new TextField();
        tempo.setPromptText("Digite o número de horas");

//        leftPane.add(comboCia, 0, 1);
//        leftPane.add(pesquisa, 1, 1);
//        leftPane.add(paisesBox,0,2);
//        leftPane.add(pesquisa2,1,3);
//        leftPane.add(pesquisa21,1,2);
//        leftPane.add(pesquisa3,2,0);
//        leftPane.add(origemBox,0,4);
//        leftPane.add(pesquisa4,1,4);
//        leftPane.add(destinoBox,0,3);
        leftPane.add(comboCia, 0, 1);
        leftPane.add(pesquisa, 1, 1);
        leftPane.add(paisesBox,0,2);
        leftPane.add(pesquisa2,2,2);
        leftPane.add(pesquisa21,1,2);
        leftPane.add(pesquisa3,1,4);
        leftPane.add(origemBox,0,3);
        leftPane.add(pesquisa4,2,3);
        leftPane.add(destinoBox,0,4);
        leftPane.add(tempo,1,3);

        pesquisa.setOnAction(e -> consulta1(comboCia.getSelectionModel().getSelectedItem().toString().substring(0,2)));

        pesquisa2.setOnAction(e -> consulta2());

        pesquisa3.setOnAction(e -> consulta3(origemBox.getSelectionModel().getSelectedItem().toString().substring(0, 3)
                , destinoBox.getSelectionModel().getSelectedItem().toString().substring(0, 3)));

        pesquisa21.setOnAction(e -> consulta2Pais(paisesBox.getSelectionModel().getSelectedItem().toString().substring(7,9)));

        pesquisa4.setOnAction(e -> consulta4());






        leftPane.getStyleClass().add("leftPane");
        pane.setCenter(mapkit);
        pane.setTop(leftPane);

        Scene scene = new Scene(pane, 500, 500);
        scene.getStylesheets().add(JanelaFX.class.getResource("estilo.css").toExternalForm());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Consultas Aéreas");
        primaryStage.show();

    }

    // Inicializando os dados aqui...
    private void setup() {

        gerCias = new GerenciadorCias();
        gerAero = new GerenciadorAeroportos();
        gerPaises = new GerenciadorPaises();
        gerAvioes = new GerenciadorAeronaves();
        gerRotas = new GerenciadorRotas();

        try {
            gerCias.carregaDados("airlines.dat");
            gerAero.carregaDados("airports.dat");
            gerAvioes.carregaDados("equipment.dat");
            gerRotas.carregaDados("routes.dat", gerAvioes, gerAero, gerCias);
            gerPaises.carregaDados("countries.dat");
        } catch (IOException e) {
            System.out.println("erro na abertura de arrquivos: " + e.getMessage());
        }

    }

    private void consulta1(String cia) {

        gerenciador.clear();


        for (Rota rota : gerRotas.listarTodas()) {
            if (rota.getCia().getCodigo().equals(cia)) {
                Tracado t = new Tracado();
                t.setLabel("Teste");
                t.setWidth(2);
                t.setCor(new Color(200, 0, 0, 70));
                t.addPonto(rota.getOrigem().getLocal());
                t.addPonto(rota.getDestino().getLocal());
                gerenciador.addTracado(t);
                lstPoints.add(new MyWaypoint(Color.BLUE, rota.getOrigem().getCodigo(), rota.getOrigem().getLocal(), 5));
                lstPoints.add(new MyWaypoint(Color.RED, rota.getDestino().getCodigo(), rota.getDestino().getLocal(), 5));
            }

        }
        gerenciador.getMapKit().repaint();
    }


    public void consulta2() {
        gerenciador.clear();
        int cont = 0;
        for (Aeroporto aeroporto : gerAero.listarTodos()) {
            for (Rota rota : gerRotas.listarTodas()) {
                if ((rota.getOrigem().getCodigo().equals(aeroporto.getCodigo()) ||
                        (rota.getDestino().getCodigo().equals(aeroporto.getCodigo())))) {
                    cont++;
                }
            }
            int tamanho = cont / 1100;
            int c2 = 20;
            int c3 = 10;

            Color cor = new Color(tamanho, c2, c3);


            lstPoints.add(new MyWaypoint(cor, aeroporto.getCodigo(), aeroporto.getLocal(), tamanho));
        }
        System.out.println(cont);
        gerenciador.setPontos(lstPoints);
        gerenciador.getMapKit().repaint();
    }


    public void consulta2Pais(String pais) {
        gerenciador.clear();
        int cont = 0;
        System.out.println(pais);
        for (Aeroporto aeroporto : gerAero.listarTodos()) {
            if (aeroporto.getSigla().equals(pais)) {
                for (Rota rota : gerRotas.listarTodas()) {
                    if ((rota.getOrigem().getCodigo().equals(aeroporto.getCodigo()) ||
                            (rota.getDestino().getCodigo().equals(aeroporto.getCodigo())))) {
                        cont++;
                    }
                }
                int tamanho = cont / 20;
                int c2 = 10;
                int c3 = 5;

                Color cor = new Color(tamanho, c2, c3);


                lstPoints.add(new MyWaypoint(cor, aeroporto.getCodigo(), aeroporto.getLocal(), tamanho));

            }

        }
        gerenciador.setPontos(lstPoints);
        gerenciador.getMapKit().repaint();

        //System.out.println(cont);
    }

    public void consulta3(String origem, String destino){
        System.out.println(origem + "  ===  " + destino);
        //        origem = "POA";
        //        destino = "GRU";
        gerenciador.clear();
        ArrayList<Rota> rotasOrigem = new ArrayList<>();
        ArrayList<Rota> rotasDestino = new ArrayList<>();
        for (Rota r : gerRotas.listarTodas()){
            if (r.getOrigem().getCodigo().equals(origem)) rotasOrigem.add(r);
        }
        for (Rota r : gerRotas.listarTodas()){
            if (r.getDestino().getCodigo().equals(destino)) rotasDestino.add(r);
        }

        for (Rota ro : rotasOrigem){
            for (Rota rd : rotasDestino){
                if (ro.getDestino().getCodigo().equals(rd.getOrigem().getCodigo())){

                        Tracado t = new Tracado();
                        t.setWidth(2);
                        t.addPonto(ro.getOrigem().getLocal());
                        t.addPonto(ro.getDestino().getLocal());
                        t.addPonto(rd.getDestino().getLocal());
                        gerenciador.addTracado(t);
                        lstPoints.add(new MyWaypoint(Color.BLUE, ro.getOrigem().getCodigo(), ro.getOrigem().getLocal(), 5));
                        lstPoints.add(new MyWaypoint(Color.BLUE, ro.getDestino().getCodigo(), ro.getDestino().getLocal(), 5));
                        lstPoints.add(new MyWaypoint(Color.BLUE, rd.getDestino().getCodigo(), rd.getDestino().getLocal(), 5));

                }
            }
        }
        gerenciador.setPontos(lstPoints);
        gerenciador.getMapKit().repaint();
    }

    public void consulta4() {
        gerenciador.clear();

        final double velocidade = 400.0;

        double distanciaQUeEPossivelPercorrer = velocidade * Double.parseDouble(tempo.getText());

        //d = v.t

        for (Rota rota : gerRotas.listarTodas()) {
            if (origemBox.getValue().getCodigo().equals(rota.getOrigem().getCodigo())) {
                for (Rota rota1: gerRotas.listarTodas()) {
                    if (rota1.getOrigem().getCodigo().equals(rota.getDestino().getCodigo())) {
                        double distancia1 = Geo.distancia(rota.getDestino().getLocal(), origemBox.getValue().getLocal());
                        double distancia2 = Geo.distancia(rota1.getDestino().getLocal(), rota1.getOrigem().getLocal());
                        double distancia = distancia1 + distancia2;
                        if (distancia < distanciaQUeEPossivelPercorrer) {
                            lstPoints.add(new MyWaypoint(Color.BLUE, rota1.getDestino().getCodigo(), rota1.getDestino().getLocal(), 50));
                            lstPoints.add(new MyWaypoint(Color.BLUE, rota.getDestino().getCodigo(), rota.getDestino().getLocal(), 50));
                        }
                    }
                }
            }
        }
        gerenciador.setPontos(lstPoints);
        gerenciador.getMapKit().repaint();

    }



        private class EventosMouse extends MouseAdapter {

        private int lastButton = -1;

        @Override
        public void mousePressed(MouseEvent e) {
            JXMapViewer mapa = gerenciador.getMapKit().getMainMap();
            GeoPosition loc = mapa.convertPointToGeoPosition(e.getPoint());
            //System .err.println(loc.getLatitude() + ", " + loc.getLongitude());
            lastButton = e.getButton();
            // Botão 3: seleciona localização
            if (lastButton == MouseEvent.BUTTON3) {
                gerenciador.setPosicao(loc);
                gerenciador.getMapKit().repaint();
            }
        }
    }

    private void createSwingContent(final SwingNode swingNode) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                swingNode.setContent(gerenciador.getMapKit());
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}