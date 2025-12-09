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
                default:
                    return "Comandos disponibles:\n" +
                           "/findTrade <oficio>\n" +
                           "/findCity <ciudad>\n" +
                           "/find <oficio> | <ciudad>";
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
        
        for (Professional p : professionals) {
            sb.append("ID: ").append(p.getId())
              .append(" | ").append(p.getTrade())
              .append(" | ").append(p.getName())
              .append(" | ").append(p.getCity())
              .append("\n");
        }
        
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
}

