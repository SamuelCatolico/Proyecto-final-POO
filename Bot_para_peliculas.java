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
//Interfaz con metodos abstractos para buscar peliculas por genero o por titulo
interface buscar_pelicula {
    String buscarTitulo(String titulo) throws IOException;
    String buscarGenero(String genero, int pag) throws IOException;
}
//Interfaz con metodos abstractos para analizar el estado de animo que selecciono el usuario
interface analizarEstadoAnimo {
    String analizarEstadoAnimo(String estado_animo);
}
//Interfaz con metodos asbtractos para obtener el festivo que selecciono el usuario y para obtener las peliculas recomendadas
interface dar_festivo {
    String[] obtenerFestivosDisponibles();
    String[] obtenerRecomendacionFestivo(String festivo);
    String[] obtenerMasRecomendacionesFestivo(String festivo);
}
//clase que se comunica con la API, inicializa la llave y la url para comunicarse con la API, y mediante estas
//hace la peticion y filtra por genero o titulo
class OMDbServicio implements buscar_pelicula {
    private static final String BASE_URL = "http://www.omdbapi.com/?apikey=";
    private final String llave_OMDb;
    //constructor que inicializa la llave
    public OMDbServicio(String llave_api) {
        this.llave_OMDb = llave_api;
    }
    //se sobreescribe el metodo para hacer una peticion y filtrar la pelicula por titulo
    @Override
    public String buscarTitulo(String titulo) throws IOException {
        String url = BASE_URL + llave_OMDb + "&t=" + titulo.replace(" ", "+");
        return hacerPeticion(url);
    }
    //se sobreescribe el metodo para hacer una peticion y filtrar la pelicula por genero
    @Override
    public String buscarGenero(String genero, int pag) throws IOException {
        String url = BASE_URL + llave_OMDb + "&s=" + genero.replace(" ", "+") + "&type=movie&page=" + pag;
        return hacerPeticion(url);
    }
    //metodo que hace la peticion a la API
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
//clase que obtiene el estado de animo elegido por el usuario y lo relaciona en un mapa con un genero de pelicula,
//y devuelve este mapa para que se pueda buscar y filtrar por este genero
class Analizar_estadoAnimo implements analizarEstadoAnimo {
    private final Map<String, String> animo_genero = new HashMap<>();
    private final String[] generosAleatorios = {"comedia", "drama", "aventura", "terror", "romance", "ciencia ficción", "animación", "acción"};
    private final Random random = new Random();
    //se relacionan los estados de animo con un genero, añadiendolos al mapa
    public Analizar_estadoAnimo() {
        animo_genero.put("feliz", "comedia");
        animo_genero.put("triste", "drama");
        animo_genero.put("emocionado", "aventura");
        animo_genero.put("asustado", "terror");
        animo_genero.put("enamorado", "romance");
        animo_genero.put("aleatorio", generosAleatorios[random.nextInt(generosAleatorios.length)]);
    }
    //se sobreescribe el metodo para regrese el genero dependiendo de la eleccion del usuario
    @Override
    public String analizarEstadoAnimo(String animo) {
        if (animo.equalsIgnoreCase("aleatorio")) {
            return generosAleatorios[random.nextInt(generosAleatorios.length)];
        }
        return animo_genero.getOrDefault(animo.toLowerCase(), null);
    }
}
//clase que recibe el dia festivo seleccionado por el usuario y lo relaciona con una lista de peliculas relacionadas
//en un mapa 
class Dias_festivos implements dar_festivo {
    private final Map<String, List<String[]>> festivos = new HashMap<>();
    //metodo que añade las listas con las peliculas recomendadas a la lista relacionada con un dia festivo
    public Dias_festivos() {
        //Navidad
        List<String[]> navidad = new ArrayList<>();
        navidad.add(new String[]{"Elf", "Mi pobre angelito", "El Expreso polar"});
        navidad.add(new String[]{"Klaus", "El Grinch", "Las Cronicas de Navidad"});
        festivos.put("Navidad", navidad);
        
        //Halloween
        List<String[]> halloween = new ArrayList<>();
        halloween.add(new String[]{"Terrifier", "Abracadabra", "Monster House-La casa de los sustos"});
        halloween.add(new String[]{"Beetlejuice", "Scream", "Halloween"});
        festivos.put("Halloween", halloween);
        
        //San Valentin
        List<String[]> sanValentin = new ArrayList<>();
        sanValentin.add(new String[]{"500 dias con ella", "Titanic", "La La Land"});
        sanValentin.add(new String[]{"Como si fuera la primera vez", "Pretty Woman", "Cuestion de Tiempo"});
        festivos.put("San Valentin", sanValentin);
        
        //Año Nuevo
        List<String[]> añoNuevo = new ArrayList<>();
        añoNuevo.add(new String[]{"Una al año no hace daño", "Al son que me toquen bailo", "Quieren volverme loco"});
        añoNuevo.add(new String[]{"Año Nuevo", "Cuando Harry conocio a Sally", "La emboscada"});
        festivos.put("Ano Nuevo", añoNuevo);
        
        //Vacaciones
        List<String[]> Vacaciones = new ArrayList<>();
        Vacaciones.add(new String[]{"El Paseo 4", "El Paseo 5", "Son como niños"});
        Vacaciones.add(new String[]{"Esposa de Mentira", "Misterio a bordo", "Que paso Ayer"});
        festivos.put("Vacaciones", Vacaciones);
    }
    //se sobreescribe el metodo para que regrese el mapa
    @Override
    public String[] obtenerFestivosDisponibles() {
        return festivos.keySet().toArray(new String[0]);
    }
    //se sobreescribe el metodo para que regrese la lista de peliculas recomendadas en el indice 0
    @Override
    public String[] obtenerRecomendacionFestivo(String festivo) {
        return festivos.get(festivo).get(0);
    }
    //se sobreescribe el metodo para que regrese la lista de peliculas recomendadas en el indice 1
    @Override
    public String[] obtenerMasRecomendacionesFestivo(String festivo) {
        List<String[]> recomendaciones = festivos.get(festivo);
        if (recomendaciones.size() > 1) {
            return recomendaciones.get(1);
        }
        return null;
    }
}
//clase principal 
public class Bot_para_peliculas {
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
    //construcotor que inicializa las interfaces, la interfaz grafica e inicia el bot
    public Bot_para_peliculas(buscar_pelicula buscador, analizarEstadoAnimo analizar_animo, dar_festivo analizar_festivo) {
        this.buscar_pelicula = buscador;
        this.analizarEstadoAnimo = analizar_animo;
        this.dar_festivo = analizar_festivo;
        inicializarGUI();
        iniciarConversacion();
    }
    //interfaz grafica
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
    //metodo que hace que el bot salude 
    private void iniciarConversacion() {
        mostrarMensaje("Hola, Soy tu asistente de recomendacion de peliculas.");
        preguntarOpcionesIniciales();
    }
    //metodo que hace que el bot de el menu de recomendaciones
    private void preguntarOpcionesIniciales() {
        mostrarMensaje("\n¿Qué tipo de recomendacion prefieres?");
        mostrarMensaje("1. Basada en tu estado de animo");
        mostrarMensaje("2. Peliculas relacionadas con dias festivos");
        mostrarMensaje("3. Buscar pelicula por titulo");
        mostrarMensaje("4. Salir");
        esperandoOpcionInicial = true;
        paginaActual = 1;
    }
    //muestra el mensaje del bot en la interfaz grafica, siempre en una linea nueva y ajustandolo para que se vea el ultimo mensaje
    private void mostrarMensaje(String mensaje) {
        outputArea.append(mensaje + "\n");
        outputArea.setCaretPosition(outputArea.getDocument().getLength());
    }
    //Obtiene el texto ingresado por el usuario y gestiona el estado de las variables que controlan el flujo 
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
    //dependiendo de la eleccion del usuario invoca metodos, cambia el estado de la variable o vuelve a mostrar el menu
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
                mostrarMensaje("\nHasta luego");
                esperandoOpcionInicial = false;
                break;
            default:
                mostrarMensaje("\nOpción no válida. Por favor elige 1, 2, 3 o 4.");
                preguntarOpcionesIniciales();
                return;
        }
        esperandoOpcionInicial = false;
    }
    //muestra la lista de festivos
    private void mostrarOpcionesFestivos() {
        String[] festivos = dar_festivo.obtenerFestivosDisponibles();
        mostrarMensaje("\nFestivos disponibles:");
        for (int i = 0; i < festivos.length; i++) {
            mostrarMensaje((i+1) + ". " + festivos[i]);
        }
        mostrarMensaje("\nElige un festivo (1-" + festivos.length + "):");
        esperandoSeleccionFestivo = true;
    }
    //procesa la seleccion del festivo del usuario
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
    //obtiene el estado de animo, e invoca el metodo para buscar peliculas por genero segun el genero relacionado al estado de animo
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
    //pregunta el estado de animo al usuario
    private void preguntarEstadoAnimo() {
        mostrarMensaje("\n¿Cómo te sientes hoy? (feliz, triste, emocionado, asustado, enamorado, aleatorio)");
        esperandoEstadoAnimo = true;
    }
    //segun el usuario eliga o no mas recomendaciones vuelve a invocar el metodo de buscar por genero o de volver al menu
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
    //retorna al menu principal de opciones
    private void volverAlMenuPrincipal() {
        mostrarMensaje("\nBot: Volviendo al menú principal...");
        festivoActual = null;
        generoActual = null;
        paginaActual = 1;
        preguntarOpcionesIniciales();
    }
    //muestra la lista de peliculas recomendadas
    private void mostrarRecomendacionesFestivo(String festivo) {
        String[] recomendaciones = dar_festivo.obtenerRecomendacionFestivo(festivo);
        if (recomendaciones != null) {
            mostrarMensaje("\nBot: Películas recomendadas para " + festivo + ":");
            for (String pelicula : recomendaciones) {
                mostrarMensaje("- " + pelicula.trim());
            }
        }
    }
    //segun el usuario eliga o no mas recomendaciones vuelve a invocar el metodo de dar las peliculas recomendadas o de volver al menu
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
    //busca peliculas por genero
    private void buscarPorGenero(String genero, int pag) {
        try {
            String resultado = buscar_pelicula.buscarGenero(genero, pag);
            mostrarMensaje(parsearResultados(resultado));
        } catch (IOException e) {
            mostrarMensaje("Error al buscar películas: " + e.getMessage());
        }
    }
    //busca peliculas por titulo
    private void buscarPeliculaEspecifica(String titulo) {
        try {
            String resultado = buscar_pelicula.buscarTitulo(titulo);
            mostrarMensaje("\nResultados para '" + titulo + "':");
            mostrarMensaje(parsearResultados(resultado));
        } catch (IOException e) {
            mostrarMensaje("Error al buscar la película: " + e.getMessage());
        }
    }
    //pregunta al usuario si quiere mas recomendaciones
    private void preguntarMasRecomendaciones() {
        mostrarMensaje("\n¿Quieres que te recomiende más películas? (sí/no)");
        esperandoMasRecomendaciones = true;
    }
    //convierte el JSON de la API en texto
    private String parsearResultados(String jsonResultado) {
        if (jsonResultado.contains("\"Search\"")) {
            return "Películas encontradas:\n" + extraerTitulosDeJSON(jsonResultado);
        } else {
            return "Características de la película:\n" + extraerDetallesDeJSON(jsonResultado);
        }
    }
    //estrae los titulos de las peliculas de un JSON
    private String extraerTitulosDeJSON(String json) {
        StringBuilder result = new StringBuilder();
        String[] parts = json.split("\"Title\":\"");
        for (int i = 1; i < parts.length; i++) {
            String title = parts[i].split("\"")[0];
            result.append("- ").append(title).append("\n");
        }
        return result.toString();
    }
    //estrae los detalles de una pelicula especifica de un JSON
    private String extraerDetallesDeJSON(String json) {
        String titulo = extraerValor(json, "Titulo");
        String año = extraerValor(json, "Ano");
        String genero = extraerValor(json, "Genero");
        String trama = extraerValor(json, "Trama");
        
        return String.format("Titulo: %s\nAno: %s\nGenero: %s\nTrama: %s", 
                           titulo, año, genero, trama);
    }
    //estrae el valor de una clave especifica en un JSON
    private String extraerValor(String json, String llave) {
        try {
            String[] parts = json.split("\"" + llave + "\":\"");
            return parts[1].split("\"")[0];
        } catch (Exception e) {
            return "No disponible";
        }
    }
    //metodo main
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