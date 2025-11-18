package com.telegram.directory.management.bot;

import com.telegram.directory.management.model.Professional;
import com.telegram.directory.management.service.ProfessionalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Optional;

@Component
public class ManagementBot extends TelegramLongPollingBot {
    
    private final ProfessionalService professionalService;
    private final String botToken;
    private final String botUsername;
    
    public ManagementBot(ProfessionalService professionalService,
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
                case "/add":
                    return handleAdd(args);
                case "/update":
                    return handleUpdate(args);
                case "/delete":
                    return handleDelete(args);
                case "/list":
                    return handleList();
                default:
                    return "Comandos disponibles:\n" +
                           "/add <oficio> | <nombre> | <ciudad>\n" +
                           "/update <id> | <oficio> | <nombre> | <ciudad>\n" +
                           "/delete <id>\n" +
                           "/list";
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private String handleAdd(String args) {
        String[] params = args.split("\\|");
        if (params.length != 3) {
            return "Formato incorrecto. Use: /add <oficio> | <nombre> | <ciudad>";
        }
        
        String trade = params[0].trim();
        String name = params[1].trim();
        String city = params[2].trim();
        
        if (trade.isEmpty() || name.isEmpty() || city.isEmpty()) {
            return "Error: Todos los campos son obligatorios";
        }
        
        Professional professional = professionalService.add(trade, name, city);
        return "✅ Profesional agregado:\n" +
               "ID: " + professional.getId() + "\n" +
               "Oficio: " + professional.getTrade() + "\n" +
               "Nombre: " + professional.getName() + "\n" +
               "Ciudad: " + professional.getCity();
    }
    
    private String handleUpdate(String args) {
        String[] params = args.split("\\|");
        if (params.length != 4) {
            return "Formato incorrecto. Use: /update <id> | <oficio> | <nombre> | <ciudad>";
        }
        
        try {
            long id = Long.parseLong(params[0].trim());
            String trade = params[1].trim();
            String name = params[2].trim();
            String city = params[3].trim();
            
            if (trade.isEmpty() || name.isEmpty() || city.isEmpty()) {
                return "Error: Todos los campos son obligatorios";
            }
            
            Optional<Professional> result = professionalService.update(id, trade, name, city);
            if (result.isPresent()) {
                Professional professional = result.get();
                return "✅ Profesional actualizado:\n" +
                       "ID: " + professional.getId() + "\n" +
                       "Oficio: " + professional.getTrade() + "\n" +
                       "Nombre: " + professional.getName() + "\n" +
                       "Ciudad: " + professional.getCity();
            } else {
                return "❌ No se encontró un profesional con ID: " + id;
            }
        } catch (NumberFormatException e) {
            return "Error: El ID debe ser un número válido";
        }
    }
    
    private String handleDelete(String args) {
        try {
            long id = Long.parseLong(args.trim());
            boolean deleted = professionalService.delete(id);
            if (deleted) {
                return "✅ Profesional con ID " + id + " eliminado correctamente";
            } else {
                return "❌ No se encontró un profesional con ID: " + id;
            }
        } catch (NumberFormatException e) {
            return "Error: El ID debe ser un número válido";
        }
    }
    
    private String handleList() {
        List<Professional> professionals = professionalService.listAll();
        if (professionals.isEmpty()) {
            return "📋 No hay profesionales registrados";
        }
        
        StringBuilder sb = new StringBuilder("📋 Lista de profesionales:\n\n");
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

