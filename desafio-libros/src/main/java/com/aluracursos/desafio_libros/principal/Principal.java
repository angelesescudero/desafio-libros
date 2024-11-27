package com.aluracursos.desafio_libros.principal;

import com.aluracursos.desafio_libros.model.Datos;
import com.aluracursos.desafio_libros.model.DatosLibros;
import com.aluracursos.desafio_libros.service.ConsumoAPI;
import com.aluracursos.desafio_libros.service.ConvierteDatos;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {
    private static final String URL_BASE = "https://gutendex.com/books/";
    private ConsumoAPI consumoAPI= new ConsumoAPI();
    private ConvierteDatos conversor= new ConvierteDatos();
    private Scanner teclado = new Scanner(System.in);


    public void muestraElMenu(){
        var json = consumoAPI.obtenerDatos(URL_BASE);
        System.out.println(json);
        var datos = conversor.obtenerDatos(json, Datos.class);
        System.out.println(datos);

    //Los 10 libros mas descargados
        System.out.println("Lista de los diez libros mas descargados");
        datos.resultados().stream()
                .sorted(Comparator.comparing(DatosLibros::numeroDeDescargas).reversed())
                .limit(10)
                .map(l-> l.titulo().toUpperCase())
                .forEach(System.out::println);

    //Buscando por  titulo de Libro  o una parte del titulo
        System.out.println("Por favor, ingresa el nombre del libro que estas buscando:");
        var tituloDelLibro = teclado.nextLine();
        json = consumoAPI.obtenerDatos(URL_BASE + "?search=" + tituloDelLibro.replace(" ","+"));
        var datosBusqueda = conversor.obtenerDatos(json, Datos.class);
        Optional<DatosLibros> libroBuscado = datosBusqueda.resultados().stream()
                .filter(l-> l.titulo().toUpperCase().contains(tituloDelLibro.toUpperCase()))
                .findFirst();
        if(libroBuscado.isPresent()){
            System.out.println("Libro encontrado");
            System.out.println(libroBuscado.get());
        }else{
            System.out.println("El libro no fue encontrado");
        }
    //Recolectando algunas estadisticas
        DoubleSummaryStatistics est = datos.resultados().stream()
                .filter(d->d.numeroDeDescargas()>0)
                .collect(Collectors.summarizingDouble(DatosLibros::numeroDeDescargas));
        System.out.println("Cantidad Media de descargas: " + est.getAverage());
        System.out.println("Cantidad Maxima de descargas: "+ est.getMax());
        System.out.println("Cantidad minima de descargas: "+ est.getMin());
        System.out.println("Cantidad de registros utilizados para calcular las estadisticas: " + est.getCount());


    }
}
