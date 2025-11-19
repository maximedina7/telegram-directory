package com.telegram.directory.query.bot;

import com.telegram.directory.query.model.Professional;
import com.telegram.directory.query.service.ProfessionalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component
public class QueryBot extends TelegramLongPollingBot {
    
    private final ProfessionalService professionalService;
    private final String botToken;
    private final String botUsername;
    
    public QueryBot(ProfessionalService professionalService,
                    @Value("${telegram.bot.token}") String botToken,
                    @Value("${telegram.bot.username}") String botUsername) {
        super(botToken);
        this.professionalService = professionalService;
        this.botToken = botToken;
        this.botUsername = botUsername;
    }
    
    @Override
    public String getBotUsername() {
        return botUsername;
    }
    
    @Override
    public String getBotToken() {
        return botToken;
    }
    
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            
            String response = processCommand(messageText);
            sendMessage(chatId, response);
        }
    }
    
    private String processCommand(String command) {
        String[] parts = command.split("\\s+", 2);
        String cmd = parts[0].toLowerCase();
        String args = parts.length > 1 ? parts[1] : "";
        
        try {
            switch (cmd) {
                case "/findtrade":
                    return handleFindTrade(args);
                case "/findcity":
                    return handleFindCity(args);
                case "/find":
                    return handleFind(args);
                case "/findcategory":
                    return handleFindCategory(args);
                case "/findverified":
                    return handleFindVerified();
                case "/findtop":
                    return handleFindTop(args);
                default:
                    return """
                           Comandos disponibles:
                           /findTrade <oficio>
                           /findCity <ciudad>
                           /find <oficio> | <ciudad>
                           /findCategory <nombre_categoria>
                           /findVerified
                           /findTop <rating_minimo>
                           """;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private String handleFindTrade(String args) {
        if (args.trim().isEmpty()) {
            return "Formato incorrecto. Use: /findTrade <oficio>";
        }
        
        String trade = args.trim();
        List<Professional> professionals = professionalService.findByTrade(trade);
        
        if (professionals.isEmpty()) {
            return "🔍 No se encontraron profesionales con el oficio: " + trade;
        }
        
        return formatResults("Oficio: " + trade, professionals);
    }
    
    private String handleFindCity(String args) {
        if (args.trim().isEmpty()) {
            return "Formato incorrecto. Use: /findCity <ciudad>";
        }
        
        String city = args.trim();
        List<Professional> professionals = professionalService.findByCity(city);
        
        if (professionals.isEmpty()) {
            return "🔍 No se encontraron profesionales en la ciudad: " + city;
        }
        
        return formatResults("Ciudad: " + city, professionals);
    }
    
    private String handleFind(String args) {
        String[] params = args.split("\\|");
        if (params.length != 2) {
            return "Formato incorrecto. Use: /find <oficio> | <ciudad>";
        }
        
        String trade = params[0].trim();
        String city = params[1].trim();
        
        if (trade.isEmpty() || city.isEmpty()) {
            return "Error: Ambos campos son obligatorios";
        }
        
        List<Professional> professionals = professionalService.findByTradeAndCity(trade, city);
        
        if (professionals.isEmpty()) {
            return "🔍 No se encontraron profesionales con oficio '" + trade + "' en la ciudad '" + city + "'";
        }
        
        return formatResults("Oficio: " + trade + " | Ciudad: " + city, professionals);
    }
    
    private String formatResults(String header, List<Professional> professionals) {
        StringBuilder sb = new StringBuilder("🔍 Resultados de búsqueda (" + professionals.size() + "):\n");
        sb.append(header).append("\n\n");
        professionals.forEach(p -> sb.append(formatProfessional(p)).append("\n"));
        return sb.toString();
    }
    
    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private String handleFindCategory(String args) {
        if (args.trim().isEmpty()) {
            return "Formato incorrecto. Use: /findCategory <nombre_categoria>";
        }
        String category = args.trim();
        List<Professional> professionals = professionalService.findByCategory(category);
        if (professionals.isEmpty()) {
            return "🔍 No se encontraron profesionales en la categoría: " + category;
        }
        return formatResults("Categoría: " + category, professionals);
    }

    private String handleFindVerified() {
        List<Professional> professionals = professionalService.findVerified();
        if (professionals.isEmpty()) {
            return "🔍 No hay profesionales verificados disponibles";
        }
        return formatResults("Profesionales verificados", professionals);
    }

    private String handleFindTop(String args) {
        if (args.trim().isEmpty()) {
            return "Formato incorrecto. Use: /findTop <rating_minimo>";
        }
        double minRating;
        try {
            minRating = Double.parseDouble(args.trim());
        } catch (NumberFormatException e) {
            return "Error: el rating mínimo debe ser un número";
        }
        List<Professional> professionals = professionalService.findTopRated(minRating);
        if (professionals.isEmpty()) {
            return "🔍 No se encontraron profesionales con rating >= " + minRating;
        }
        return formatResults("Rating mínimo: " + minRating, professionals);
    }

    private String formatProfessional(Professional p) {
        String category = p.getCategory() != null ? p.getCategory().getName() : "Sin categoría";
        return "ID: " + p.getId()
                + " | " + safe(p.getTrade())
                + " | " + safe(p.getName())
                + " | " + safe(p.getCity())
                + " | Tel: " + safe(p.getPhone())
                + " | Email: " + safe(p.getEmail())
                + " | Exp: " + p.getExperienceYears() + " años"
                + " | Rating: " + p.getRating()
                + " | Verificado: " + (p.isVerified() ? "Sí" : "No")
                + " | Categoría: " + category;
    }

    private String safe(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
    }
}

