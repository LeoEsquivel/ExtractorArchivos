package com.lyon.extractorarchivos;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Extractor {

    private HashMap<String, File> archivos = new HashMap<>();
    private String[] extencionesGuardadas = {".png", ".jpg", ".java", ".xml", ".jar", ".obj", ".glsl", ".mp3" };

    private File carpeta;
    private String[] lista;
    private String rutaGuardado;


    public void searchFileByDeepness(String srcFolder){

        if (srcFolder != null){
            File directorioPrincipal = new File(srcFolder);
            if (directorioPrincipal.isDirectory()){
                String[] subdirectorio = directorioPrincipal.list();

                for (String contenido:subdirectorio){
                    File cont = new File(directorioPrincipal, contenido);

                    if (contenido.contains(".") && cont.isDirectory()){
                        System.out.println("Carpeta ignorada: "+cont.getName());

                    }else{

                        if (cont.isFile()){
                            GuardarArchivos(cont);

                        }else{

                            List<File> subDirectorios = getSubDirectories(cont);

                            do {
                                List<File> subSubDirectorios = new ArrayList<File>();

                                for (File subDirectorio: subDirectorios){

                                    if (subDirectorio.isDirectory()){
                                        String [] contSubDirectorio = subDirectorio.list();

                                        for(String conte:contSubDirectorio){
                                            File fileEnSubDirectorio = new File(subDirectorio, conte);

                                            if (fileEnSubDirectorio.isFile() && fileEnSubDirectorio.getPath().contains("src")) {
                                                GuardarArchivos(fileEnSubDirectorio);
                                            }else{
                                                subSubDirectorios.addAll(getSubDirectories(subDirectorio));
                                            }
                                            subDirectorios = subSubDirectorios;
                                        }
                                    }

                                }
                            }while (subDirectorios != null && ! subDirectorios.isEmpty());
                        }
                    }
                }
            }
            crearZip(srcFolder);
        }
    }

    public void Obtener(String ruta){
        String srcFolder = null;
        this.rutaGuardado = ruta;
        this.carpeta = new File(ruta);
        if(this.carpeta.isDirectory()){
            this.lista = this.carpeta.list();
            for (String objeto:this.lista) {
                if (objeto.equals("app")){
                    ReApuntado(this.carpeta.getAbsolutePath()+"\\"+objeto);
                    this.lista = actualizarLista(this.carpeta);
                    for (String objeto2:this.lista) {
                        if (objeto2.equals("src")){
                            searchFileByDeepness(this.carpeta.getAbsolutePath()+"\\"+objeto2);
                        }
                    }
                }
            }
        }

    }

    public String[] actualizarLista(File listaCarpeta){
        return listaCarpeta.list();
    }

    public File ReApuntado(String nuevaCarpeta){
        return this.carpeta = new File(nuevaCarpeta);
    }

    private List<File> getSubDirectories(File directory) {
        File[] subDirectories = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File current, String name) {
                return new File(current, name).isDirectory();
            }
        });
        return Arrays.asList(subDirectories);
    }

    public void GuardarArchivos(File contenido){
        if (contenido.isFile()){
            for (String extension:extencionesGuardadas) {
                if (contenido.getName().contains(extension) && !contenido.getName().contains("ic_")){
                    if (!this.archivos.containsKey(contenido.getName())){
                        System.out.println("Se agrego: "+contenido.getName() + " al archivo ZIP");
                        this.archivos.put(contenido.getName(), contenido);
                    }

                }
            }
        }
    }

    public void crearZip(String ruta){
            this.archivos.put("AndroidManifest.xml", new File(this.carpeta.getAbsolutePath()+"\\src\\main\\AndroidManifest.xml"));
            try{
                byte[] buffer = new byte[1024];
                ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(this.carpeta.getParent() +this.carpeta.getName()+".zip"));

                for (Map.Entry<String, File> archivo:archivos.entrySet()){
                    FileInputStream archivoGuardadoFIS = new FileInputStream(archivo.getValue());
                    zos.putNextEntry(new ZipEntry(archivo.getKey()));

                    int lenght;
                    while ((lenght=archivoGuardadoFIS.read(buffer)) > 0){
                        zos.write(buffer, 0, lenght);
                    }
                    zos.closeEntry();
                    archivoGuardadoFIS.close();
                }
                zos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        System.out.println("Archivo almacenado en: "+this.carpeta.getParent() +this.carpeta.getName()+".zip");
    }
}
