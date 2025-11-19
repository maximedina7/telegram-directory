package com.telegram.directory.management.bot;

import com.telegram.directory.management.model.Category;
import com.telegram.directory.management.model.Professional;
import com.telegram.directory.management.service.CategoryService;
import com.telegram.directory.management.service.ProfessionalService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class ManagementBot extends TelegramLongPollingBot {
    
    private final ProfessionalService professionalService;
    private final CategoryService categoryService;
    private final String botToken;
    private final String botUsername;
    
    public ManagementBot(ProfessionalService professionalService,
                         CategoryService categoryService,
                         @Value("${telegram.bot.token}") String botToken,
                         @Value("${telegram.bot.username}") String botUsername) {
        super(botToken);
        this.professionalService = professionalService;
        this.categoryService = categoryService;
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
                case "/addcategory":
                    return handleAddCategory(args);
                case "/listcategories":
                    return handleListCategories();
                case "/deletecategory":
                    return handleDeleteCategory(args);
                default:
                    return """
                           Comandos disponibles:
                           /add <oficio> | <nombre> | <ciudad> | <tel> | <email> | <años exp> | <descripcion> | <verificado true/false> | <rating 0-5> | <categoriaId opcional>
                           /update <id> | <oficio> | <nombre> | <ciudad> | <tel> | <email> | <años exp> | <descripcion> | <verificado true/false> | <rating 0-5> | <categoriaId opcional>
                           /delete <id>
                           /list
                           /addCategory <nombre> | <descripcion>
                           /listCategories
                           /deleteCategory <id>
                           """;
            }
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
    
    private String handleAdd(String args) {
        String[] params = args.split("\\|");
        if (params.length < 9) {
            return "Formato incorrecto. Use: /add <oficio> | <nombre> | <ciudad> | <tel> | <email> | <años exp> | <descripcion> | <verificado true/false> | <rating 0-5> | <categoriaId opcional>";
        }
        
        String trade = params[0].trim();
        String name = params[1].trim();
        String city = params[2].trim();
        String phone = params[3].trim();
        String email = params[4].trim();
        int experienceYears = parseInteger(params[5].trim(), "Los años de experiencia deben ser numéricos");
        String description = params[6].trim();
        boolean verified = Boolean.parseBoolean(params[7].trim());
        double rating = clampRating(parseDouble(params[8].trim(), "El rating debe ser numérico"));
        Long categoryId = params.length > 9 && !params[9].trim().isEmpty() ? parseLong(params[9].trim(), "El ID de categoría debe ser numérico") : null;
        
        if (trade.isEmpty() || name.isEmpty() || city.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            return "Error: Todos los campos son obligatorios";
        }
        
        Professional professional = professionalService.add(
                trade,
                name,
                city,
                phone,
                email,
                experienceYears,
                description,
                verified,
                rating,
                categoryId
        );
        return "✅ Profesional agregado:\n" + formatProfessional(professional);
    }
    
    private String handleUpdate(String args) {
        String[] params = args.split("\\|");
        if (params.length < 10) {
            return "Formato incorrecto. Use: /update <id> | <oficio> | <nombre> | <ciudad> | <tel> | <email> | <años exp> | <descripcion> | <verificado true/false> | <rating 0-5> | <categoriaId opcional>";
        }
        
        try {
            long id = Long.parseLong(params[0].trim());
            String trade = params[1].trim();
            String name = params[2].trim();
            String city = params[3].trim();
            String phone = params[4].trim();
            String email = params[5].trim();
            int experienceYears = parseInteger(params[6].trim(), "Los años de experiencia deben ser numéricos");
            String description = params[7].trim();
            boolean verified = Boolean.parseBoolean(params[8].trim());
            double rating = clampRating(parseDouble(params[9].trim(), "El rating debe ser numérico"));
            Long categoryId = params.length > 10 && !params[10].trim().isEmpty() ? parseLong(params[10].trim(), "El ID de categoría debe ser numérico") : null;
            
            if (trade.isEmpty() || name.isEmpty() || city.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                return "Error: Todos los campos son obligatorios";
            }
            
            Optional<Professional> result = professionalService.update(
                    id,
                    trade,
                    name,
                    city,
                    phone,
                    email,
                    experienceYears,
                    description,
                    verified,
                    rating,
                    categoryId
            );
            if (result.isPresent()) {
                Professional professional = result.get();
                return "✅ Profesional actualizado:\n" + formatProfessional(professional);
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
        professionals.forEach(p -> sb.append(formatProfessional(p)).append("\n"));
        return sb.toString();
    }

    private String handleAddCategory(String args) {
        String[] params = args.split("\\|");
        if (params.length < 1) {
            return "Formato incorrecto. Use: /addCategory <nombre> | <descripcion opcional>";
        }
        String name = params[0].trim();
        String description = params.length > 1 ? params[1].trim() : "";
        if (name.isEmpty()) {
            return "El nombre de la categoría es obligatorio";
        }
        Category category = categoryService.create(name, description);
        return "✅ Categoría creada:\nID: " + category.getId() + "\nNombre: " + category.getName();
    }

    private String handleListCategories() {
        List<Category> categories = categoryService.listAll();
        if (categories.isEmpty()) {
            return "📂 No hay categorías registradas";
        }
        StringBuilder sb = new StringBuilder("📂 Categorías disponibles:\n\n");
        categories.forEach(c -> sb.append("ID: ").append(c.getId())
                .append(" | ").append(c.getName())
                .append(c.getDescription() != null && !c.getDescription().isEmpty() ? " | " + c.getDescription() : "")
                .append("\n"));
        return sb.toString();
    }

    private String handleDeleteCategory(String args) {
        Long id = parseLong(args.trim(), "El ID de categoría debe ser numérico");
        Objects.requireNonNull(id, "El ID de categoría no puede ser null");
        boolean deleted = categoryService.delete(id);
        if (deleted) {
            return "✅ Categoría eliminada correctamente";
        }
        return "❌ No se encontró una categoría con ID: " + id;
    }

    private String formatProfessional(Professional p) {
        String category = p.getCategory() != null ? p.getCategory().getName() + " (#" + p.getCategory().getId() + ")" : "Sin categoría";
        return "ID: " + p.getId() +
                "\nOficio: " + safe(p.getTrade()) +
                "\nNombre: " + safe(p.getName()) +
                "\nCiudad: " + safe(p.getCity()) +
                "\nTeléfono: " + safe(p.getPhone()) +
                "\nEmail: " + safe(p.getEmail()) +
                "\nExperiencia: " + p.getExperienceYears() + " años" +
                "\nDescripción: " + (p.getDescription() == null || p.getDescription().isEmpty() ? "-" : p.getDescription()) +
                "\nVerificado: " + (p.isVerified() ? "Sí" : "No") +
                "\nRating: " + p.getRating() +
                "\nCategoría: " + category + "\n";
    }

    private int parseInteger(String value, String errorMessage) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private double parseDouble(String value, String errorMessage) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private Long parseLong(String value, String errorMessage) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private double clampRating(double rating) {
        if (rating < 0) return 0;
        if (rating > 5) return 5;
        return rating;
    }

    private String safe(String value) {
        return (value == null || value.isEmpty()) ? "-" : value;
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

