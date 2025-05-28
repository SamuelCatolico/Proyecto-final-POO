/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/**
 *
 * @author Samuel
 */
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

interface buscar_pelicula {
    String buscarTitulo(String titulo) throws IOException;
    String buscarGenero(String genero, int pag) throws IOException;
}

interface analizarEstadoAnimo {
    String analizarEstadoAnimo(String estado_animo);
}

interface dar_festivo {
    String[] obtenerFestivosDisponibles();
    String[] obtenerRecomendacionFestivo(String festivo);
    String[] obtenerMasRecomendacionesFestivo(String festivo);
}

class OMDbServicio implements buscar_pelicula {
    private static final String BASE_URL = "http://www.omdbapi.com/?apikey=";
    private final String llave_OMDb;
    
    public OMDbServicio(String llave_api) {
        this.llave_OMDb = llave_api;
    }
    
    @Override
    public String buscarTitulo(String titulo) throws IOException {
        String url = BASE_URL + llave_OMDb + "&t=" + titulo.replace(" ", "+");
        return hacerPeticion(url);
    }
    
    @Override
    public String buscarGenero(String genero, int pag) throws IOException {
        String url = BASE_URL + llave_OMDb + "&s=" + genero.replace(" ", "+") + "&type=movie&page=" + pag;
        return hacerPeticion(url);
    }
    
    private String hacerPeticion(String urlString) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection objeto_peticion = (HttpURLConnection) url.openConnection();
        objeto_peticion.setRequestMethod("GET");
        
        try (BufferedReader in = new BufferedReader(new InputStreamReader(objeto_peticion.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String inputLine;
            
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            
            return response.toString();
        }
    }
}

class Analizar_estadoAnimo implements analizarEstadoAnimo {
    private final Map<String, String> animo_genero = new HashMap<>();
    private final String[] generosAleatorios = {"comedia", "drama", "aventura", "terror", "romance", "ciencia ficción", "animación", "acción"};
    private final Random random = new Random();
    
    public Analizar_estadoAnimo() {
        animo_genero.put("feliz", "comedia");
        animo_genero.put("triste", "drama");
        animo_genero.put("emocionado", "aventura");
        animo_genero.put("asustado", "terror");
        animo_genero.put("enamorado", "romance");
        animo_genero.put("aleatorio", generosAleatorios[random.nextInt(generosAleatorios.length)]);
    }
    
    @Override
    public String analizarEstadoAnimo(String animo) {
        if (animo.equalsIgnoreCase("aleatorio")) {
            return generosAleatorios[random.nextInt(generosAleatorios.length)];
        }
        return animo_genero.getOrDefault(animo.toLowerCase(), null);
    }
}

class Dias_festivos implements dar_festivo {
    private final Map<String, List<String[]>> festivos = new HashMap<>();
    
    public Dias_festivos() {
        // Navidad: 2 recomendaciones
        List<String[]> navidad = new ArrayList<>();
        navidad.add(new String[]{"Elf", "Mi pobre angelito", "El Expreso polar"});
        navidad.add(new String[]{"Klaus", "El Grinch", "Las Cronicas de Navidad"});
        festivos.put("Navidad", navidad);
        
        // Halloween: 2 recomendaciones
        List<String[]> halloween = new ArrayList<>();
        halloween.add(new String[]{"Terrifier", "Abracadabra", "Monster House-La casa de los sustos"});
        halloween.add(new String[]{"Beetlejuice", "Scream", "Halloween"});
        festivos.put("Halloween", halloween);
        
        // San Valentin: 2 recomendaciones
        List<String[]> sanValentin = new ArrayList<>();
        sanValentin.add(new String[]{"500 dias con ella", "Titanic", "La La Land"});
        sanValentin.add(new String[]{"Como si fuera la primera vez", "Pretty Woman", "Cuestion de Tiempo"});
        festivos.put("San Valentin", sanValentin);
        
        // Año Nuevo: 2 recomendaciones
        List<String[]> añoNuevo = new ArrayList<>();
        añoNuevo.add(new String[]{"Una al año no hace daño", "Al son que me toquen bailo", "Quieren volverme loco"});
        añoNuevo.add(new String[]{"Año Nuevo", "Cuando Harry conocio a Sally", "La emboscada"});
        festivos.put("Ano Nuevo", añoNuevo);
        
        // Independencia: 2 recomendaciones
        List<String[]> Vacaciones = new ArrayList<>();
        Vacaciones.add(new String[]{"El Paseo 4", "El Paseo 5", "Son como niños"});
        Vacaciones.add(new String[]{"Esposa de Mentira", "Misterio a bordo", "Que paso Ayer"});
        festivos.put("Vacaciones", Vacaciones);
    }
    
    @Override
    public String[] obtenerFestivosDisponibles() {
        return festivos.keySet().toArray(new String[0]);
    }
    
    @Override
    public String[] obtenerRecomendacionFestivo(String festivo) {
        return festivos.get(festivo).get(0);
    }
    
    @Override
    public String[] obtenerMasRecomendacionesFestivo(String festivo) {
        List<String[]> recomendaciones = festivos.get(festivo);
        if (recomendaciones.size() > 1) {
            return recomendaciones.get(1);
        }
        return null;
    }
}

public class Bot_para_peliculass {
    private final buscar_pelicula buscar_pelicula;
    private final analizarEstadoAnimo analizarEstadoAnimo;
    private final dar_festivo dar_festivo;
    private JFrame frame;
    private JTextArea outputArea;
    private JTextField inputField;
    private boolean esperandoEstadoAnimo = false;
    private boolean esperandoMasRecomendaciones = false;
    private boolean esperandoOpcionInicial = false;
    private boolean esperandoSeleccionFestivo = false;
    private boolean esperandoBusquedaTitulo = false;
    private String generoActual = null;
    private String festivoActual = null;
    private int paginaActual = 1;
    
    public Bot_para_peliculas(buscar_pelicula buscador, analizarEstadoAnimo analizar_animo, dar_festivo analizar_festivo) {
        this.buscar_pelicula = buscador;
        this.analizarEstadoAnimo = analizar_animo;
        this.dar_festivo = analizar_festivo;
        inicializarGUI();
        iniciarConversacion();
    }
    
    private void inicializarGUI() {
        frame = new JFrame("Recomendador de Peliculas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());
        
        outputArea = new JTextArea();
        outputArea.setEditable(false);
        outputArea.setLineWrap(true);
        outputArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(outputArea);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputField = new JTextField();
        JButton sendButton = new JButton("Enviar");
        
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);
        
        ActionListener sendAction = e -> procesarEntrada();
        sendButton.addActionListener(sendAction);
        inputField.addActionListener(sendAction);
        
        frame.setVisible(true);
    }
    
    private void iniciarConversacion() {
        mostrarMensaje("Hola, Soy tu asistente de recomendacion de peliculas.");
        preguntarOpcionesIniciales();
    }
    
    private void preguntarOpcionesIniciales() {
        mostrarMensaje("\n¿Qué tipo de recomendacion prefieres?");
        mostrarMensaje("1. Basada en tu estado de animo");
        mostrarMensaje("2. Peliculas relacionadas con dias festivos");
        mostrarMensaje("3. Buscar pelicula por titulo");
        mostrarMensaje("4. Salir");
        esperandoOpcionInicial = true;
        paginaActual = 1;
    }
    
    private void mostrarMensaje(String mensaje) {
        outputArea.append(mensaje + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    
    private void procesarEntrada() {
        String input = inputField.getText().trim();
        inputField.setText("");
        
        if (input.isEmpty()) return;
        
        mostrarMensaje("\nTú: " + input);
        
        if (esperandoOpcionInicial) {
            procesarOpcionInicial(input);
            return;
        }
        
        if (esperandoSeleccionFestivo) {
            procesarSeleccionFestivo(input);
            return;
        }
        
        if (esperandoEstadoAnimo) {
            procesarEstadoAnimo(input);
            return;
        }
        
        if (esperandoBusquedaTitulo) {
            buscarPeliculaEspecifica(input);
            preguntarOpcionesIniciales();
            esperandoBusquedaTitulo = false;
            return;
        }
        
        if (esperandoMasRecomendaciones) {
            procesarMasRecomendaciones(input);
            return;
        }
    }
    
    private void procesarOpcionInicial(String input) {
        switch(input.toLowerCase()) {
            case "1":
                preguntarEstadoAnimo();
                break;
            case "2":
                mostrarOpcionesFestivos();
                break;
            case "3":
                mostrarMensaje("\nPor favor, escribe el título de la película que buscas:");
                esperandoBusquedaTitulo = true;
                break;
            case "4":
                mostrarMensaje("\nHasta luego:");
                esperandoOpcionInicial = false;
                break;
            default:
                mostrarMensaje("\nOpción no válida. Por favor elige 1, 2, 3 o 4.");
                preguntarOpcionesIniciales();
                return;
        }
        esperandoOpcionInicial = false;
    }
    
    private void mostrarOpcionesFestivos() {
        String[] festivos = dar_festivo.obtenerFestivosDisponibles();
        mostrarMensaje("\nFestivos disponibles:");
        for (int i = 0; i < festivos.length; i++) {
            mostrarMensaje((i+1) + ". " + festivos[i]);
        }
        mostrarMensaje("\nElige un festivo (1-" + festivos.length + "):");
        esperandoSeleccionFestivo = true;
    }
    
    private void procesarSeleccionFestivo(String input) {
        try {
            String[] festivos = dar_festivo.obtenerFestivosDisponibles();
            int opcion = Integer.parseInt(input) - 1;
            
            if (opcion >= 0 && opcion < festivos.length) {
                festivoActual = festivos[opcion];
                mostrarRecomendacionesFestivo(festivoActual);
                preguntarMasRecomendaciones();
            } else {
                mostrarMensaje("\nNumero fuera de rango. Por favor elige entre 1 y " + festivos.length);
                mostrarOpcionesFestivos();
                return;
            }
        } catch (NumberFormatException e) {
            mostrarMensaje("\nPor favor ingresa un número valido.");
            mostrarOpcionesFestivos();
            return;
        }
        esperandoSeleccionFestivo = false;
    }
    
    private void procesarEstadoAnimo(String input) {
        generoActual = analizarEstadoAnimo.analizarEstadoAnimo(input);
        
        if (generoActual != null) {
            mostrarMensaje("\nBot: Basado en tu estado de ánimo (" + input + "), te recomiendo películas de " + generoActual + ":");
            buscarPorGenero(generoActual, 1);
            preguntarMasRecomendaciones();
        } else {
            mostrarMensaje("\nBot: No reconozco ese estado de ánimo. Por favor elige entre: feliz, triste, emocionado, asustado, enamorado, aleatorio");
            preguntarEstadoAnimo();
        }
        esperandoEstadoAnimo = false;
    }
    
    private void preguntarEstadoAnimo() {
        mostrarMensaje("\n¿Cómo te sientes hoy? (feliz, triste, emocionado, asustado, enamorado, aleatorio)");
        esperandoEstadoAnimo = true;
    }
    
    private void procesarMasRecomendaciones(String input) {
        if (input.equalsIgnoreCase("sí") || input.equalsIgnoreCase("si") || input.equalsIgnoreCase("Si")) {
            if (generoActual != null) {
                paginaActual++;
                mostrarMensaje("\nBot: Más recomendaciones de " + generoActual + ":");
                buscarPorGenero(generoActual, paginaActual);
            } else if (festivoActual != null) {
                mostrarMasRecomendacionesFestivo(festivoActual);
            }
        }
        volverAlMenuPrincipal();
        esperandoMasRecomendaciones = false;
    }
    
    private void volverAlMenuPrincipal() {
        mostrarMensaje("\nBot: Volviendo al menú principal...");
        festivoActual = null;
        generoActual = null;
        paginaActual = 1;
        preguntarOpcionesIniciales();
    }
    
    private void mostrarRecomendacionesFestivo(String festivo) {
        String[] recomendaciones = dar_festivo.obtenerRecomendacionFestivo(festivo);
        if (recomendaciones != null) {
            mostrarMensaje("\nBot: Películas recomendadas para " + festivo + ":");
            for (String pelicula : recomendaciones) {
                mostrarMensaje("- " + pelicula.trim());
            }
        }
    }
    
    private void mostrarMasRecomendacionesFestivo(String festivo) {
        String[] recomendaciones = dar_festivo.obtenerMasRecomendacionesFestivo(festivo);
        if (recomendaciones != null) {
            mostrarMensaje("\nBot: Más películas recomendadas para " + festivo + ":");
            for (String pelicula : recomendaciones) {
                mostrarMensaje("- " + pelicula.trim());
            }
        } else {
            mostrarMensaje("\nBot: No hay más recomendaciones disponibles para " + festivo);
        }
    }
    
    private void buscarPorGenero(String genero, int pag) {
        try {
            String resultado = buscar_pelicula.buscarGenero(genero, pag);
            mostrarMensaje(parsearResultados(resultado));
        } catch (IOException e) {
            mostrarMensaje("Error al buscar películas: " + e.getMessage());
        }
    }
    
    private void buscarPeliculaEspecifica(String titulo) {
        try {
            String resultado = buscar_pelicula.buscarTitulo(titulo);
            mostrarMensaje("\nResultados para '" + titulo + "':");
            mostrarMensaje(parsearResultados(resultado));
        } catch (IOException e) {
            mostrarMensaje("Error al buscar la película: " + e.getMessage());
        }
    }
    
    private void preguntarMasRecomendaciones() {
        mostrarMensaje("\n¿Quieres que te recomiende más películas? (sí/no)");
        esperandoMasRecomendaciones = true;
    }
    
    private String parsearResultados(String jsonResultado) {
        if (jsonResultado.contains("\"Search\"")) {
            return "Películas encontradas:\n" + extraerTitulosDeJSON(jsonResultado);
        } else {
            return "Características de la película:\n" + extraerDetallesDeJSON(jsonResultado);
        }
    }
    
    private String extraerTitulosDeJSON(String json) {
        StringBuilder result = new StringBuilder();
        String[] parts = json.split("\"Title\":\"");
        for (int i = 1; i < parts.length; i++) {
            String title = parts[i].split("\"")[0];
            result.append("- ").append(title).append("\n");
        }
        return result.toString();
    }
    
    private String extraerDetallesDeJSON(String json) {
        String titulo = extraerValor(json, "Titulo");
        String año = extraerValor(json, "Ano");
        String genero = extraerValor(json, "Genero");
        String trama = extraerValor(json, "Trama");
        
        return String.format("Titulo: %s\nAno: %s\nGenero: %s\nTrama: %s", 
                           titulo, año, genero, trama);
    }
    
    private String extraerValor(String json, String llave) {
        try {
            String[] parts = json.split("\"" + llave + "\":\"");
            return parts[1].split("\"")[0];
        } catch (Exception e) {
            return "No disponible";
        }
    }
    
    public static void main(String[] args) {
        String llave = "f0bd5901"; 
        
        SwingUtilities.invokeLater(() -> {
            buscar_pelicula buscador = new OMDbServicio(llave);
            analizarEstadoAnimo analizar_animo = new Analizar_estadoAnimo();
            dar_festivo analizar_festivo = new Dias_festivos();
            new Bot_para_peliculas(buscador, analizar_animo, analizar_festivo);
        });
    }
}