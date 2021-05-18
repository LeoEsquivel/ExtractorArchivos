package com.lyon.extractorarchivos;

import java.util.Scanner;

public class App {

    public static void main(String[] args) {


        Scanner sc = new Scanner(System.in);
        System.out.println("Ingresa la ruta de la carpeta del proyecto");
        String ruta = sc.nextLine();


        Extractor  extractor = new Extractor();
        extractor.Obtener(ruta);

    }
}
