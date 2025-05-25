package com.serenypals.restfulapi.service;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import java.util.Optional;
import java.time.LocalDateTime;
import java.time.LocalDate;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.serenypals.restfulapi.util.PasswordHasherMatcher;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.serenypals.restfulapi.repository.AIChatRepository;
import com.serenypals.restfulapi.repository.AIChatRoomRepository;
import com.serenypals.restfulapi.model.AIChat;
import com.serenypals.restfulapi.model.LoginInfo;
import com.serenypals.restfulapi.model.AIChatRoom;

@Service
public class PromptService {

    @Autowired
    private AIChatRepository aiChatRepository;

    @Autowired
    private AIChatRoomRepository aiChatRoomRepository;

    @Value("${storage.gemini-id}")
    private String geminiId;

    private String geminiLink = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent";
    private static final ObjectMapper mapper = new ObjectMapper();

    public String getLink() {
        return geminiLink + "?key=" + geminiId;
    }

    public Optional<AIChatRoom> findChatRoomById(String idChatRoom) {
        return aiChatRoomRepository.findById(idChatRoom);
    }

    public String renameChatRoom(AIChatRoom chatRoom, String newName) {
        chatRoom.setJudulChat(newName);
        chatRoom.setEditedAt(LocalDate.now());
        aiChatRoomRepository.save(chatRoom);
        return chatRoom.getJudulChat();
    }

    public AIChatRoom createChatRoom(LoginInfo loginInfo) {
        AIChatRoom chatRoom = new AIChatRoom();
        chatRoom.setIdUser(loginInfo.getIdUser());
        chatRoom.setJudulChat("Chat Room Baru");
        chatRoom.setCreatedAt(LocalDate.now());
        chatRoom.setEditedAt(LocalDate.now());
        return aiChatRoomRepository.save(chatRoom);
    }

    public List<Map<String, Object>> getHistory(AIChatRoom chatRoom) {
        ArrayList<Map<String, Object>> history = new ArrayList<Map<String, Object>>();
        for (AIChat chat : aiChatRepository.findAll()) {
            if (chat.getIdChatRoom().equals(chatRoom)) {
                Map<String, Object> chatEntry = new HashMap<>();
                chatEntry.put("role", chat.getIsBot() ? "assistant" : "user");
                chatEntry.put("parts", List.of(Map.of("text", chat.getChat())));
                history.add(chatEntry);
            }
        }
        List<Map<String, Object>> result = history;
        return result;
    }

    public String sendPrompt(String prompt, AIChatRoom chatRoom) throws JsonProcessingException {
        aiChatRepository.save(new AIChat(
                null,
                chatRoom,
                prompt,
                false,
                LocalDateTime.now()));
        List<Map<String, Object>> history = getHistory(chatRoom);
        String instructions = "Kamu adalah chatbot untuk menjadi psikolog yang baik bagi mahasiswa. Berikan saya konsultasi dari hasil chat ini";
        Map<String, Object> data = Map.of("model", "gemini-2.0-flash",
                "systemInstruction", Map.of("parts", List.of(Map.of("text", instructions))),
                "contents", List.of(history, Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text", prompt)))));

        // Parsing Map to JSON body
        String jsonBody = mapper.writeValueAsString(data);
        // Create HttpClient with default settings
        HttpClient client = HttpClient.newHttpClient();
        // Build POST request with no body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getLink()))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // no payload :contentReference[oaicite:1]{index=1}
                .build();

        try {
            // Send the request and retrieve the response as a String
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString() // handle the response body as String
                                                         // :contentReference[oaicite:2]{index=2}
            );
            // Output response
            String responseBody = response.body().split("text")[1].substring(4).split("\"")[0];
            responseBody = responseBody.substring(0, responseBody.length() - 2);
            aiChatRepository.save(new AIChat(
                    null,
                    chatRoom,
                    responseBody,
                    true,
                    LocalDateTime.now()));
            return responseBody;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String generateRoomName(AIChatRoom chatRoom) throws JsonProcessingException {
        List<Map<String, Object>> history = getHistory(chatRoom);
        String instructions = "Berikan saya nama yang cocok untuk chat room ini berdasarkan chat yang ada";
        Map<String, Object> data = Map.of("model", "gemini-2.0-flash",
                "systemInstruction", Map.of("parts", List.of(Map.of("text", instructions))),
                "contents", List.of(history, Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text",
                                "berikan saya nama room chat berdasarkan chat kita di atas dengan maksimal 50 karakter")))));

        // Parsing Map to JSON body
        String jsonBody = mapper.writeValueAsString(data);
        // Create HttpClient with default settings
        HttpClient client = HttpClient.newHttpClient();
        // Build POST request with no body
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(getLink()))
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody)) // no payload :contentReference[oaicite:1]{index=1}
                .build();

        try {
            // Send the request and retrieve the response as a String
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString() // handle the response body as String
                                                         // :contentReference[oaicite:2]{index=2}
            );
            // Output response
            String responseBody = response.body().split("text")[1].substring(4).split("\"")[0];
            return responseBody;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
