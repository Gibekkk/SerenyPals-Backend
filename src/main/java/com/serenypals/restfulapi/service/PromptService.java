package com.serenypals.restfulapi.service;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Collections;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
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
import java.util.Comparator;

@Service
public class PromptService {

    @Autowired
    private AIChatRepository aiChatRepository;

    @Autowired
    private AIChatRoomRepository aiChatRoomRepository;

    @Value("${storage.gemini-id}")
    private String geminiId;

    private final String GEMINI_MODEL = "gemini-2.0-flash";
    private final String GEMINI_LINK = "https://generativelanguage.googleapis.com/v1beta/models/" + GEMINI_MODEL
            + ":generateContent";
    private static final ObjectMapper mapper = new ObjectMapper();

    public String getLink() {
        return GEMINI_LINK + "?key=" + geminiId;
    }

    public Optional<AIChatRoom> findChatRoomById(String idChatRoom) {
        return aiChatRoomRepository.findById(idChatRoom).filter(f -> f.getDeletedAt() == null);
    }

    public void deleteChatRoomById(AIChatRoom chatRoom) {
        chatRoom.setDeletedAt(LocalDate.now());
        aiChatRoomRepository.save(chatRoom);
    }

    public List<AIChatRoom> getChatRoomsByLoginInfo(LoginInfo loginInfo) {
        return aiChatRoomRepository.findAll().stream()
                .filter(chatRoom -> chatRoom.getDeletedAt() == null)
                .filter(chatRoom -> chatRoom.getIdUser().getIdLogin().equals(loginInfo))
                .sorted(Comparator.comparing(AIChatRoom::getLastChatDateTime).reversed())
                .collect(Collectors.toList());
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
        List<AIChat> chatList = aiChatRepository.findAll().stream()
                .sorted(Comparator.comparing(AIChat::getCreatedAt))
                .collect(Collectors.toList());
        Collections.reverse(chatList);
        for (AIChat chat : chatList) {
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

    public Boolean idPolite(String prompt) throws JsonProcessingException {
        String instructions = "Kamu adalah chatbot untuk menjadi administrator yang teliti dan disiplin. Periksa teks yang saya berikan apakah teks tersebut sopan dan ramah atau tidak dari setiap bahasa yang ada, dan periksa juga kata singkatan dan segalanya. Saya ingin teks tersebut tidak memiliki kemungkinan sedikit pun untuk menyinggung seseorang dari segi apapun. Berikan respon 'TRUE' jika sopan dan respon 'FALSE' jika tidak. Saya ingin respon kamu bersih dan tidak memiliki tanda baca seperti bold, italic, new line, dan lain sebagainya. Respon yang saya minta hanya berisi 1 kata sesuai instruksi sebelumnya.";
        Map<String, Object> data = Map.of("model", GEMINI_MODEL,
                "systemInstruction", Map.of("parts", List.of(Map.of("text", instructions))),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "topK", 1,
                        "topP", 1,
                        "maxOutputTokens", 1024),
                "safetySettings", List.of(
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HARASSMENT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE")),
                "contents", List.of(Map.of(
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
            String responseBody = response.body().split("text")[1].substring(4).split("\"")[0].replace("\\n", "");
            return responseBody.equalsIgnoreCase("true");
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }

    public String sendPrompt(String prompt, AIChatRoom chatRoom) throws JsonProcessingException {
        aiChatRepository.save(new AIChat(
                null,
                chatRoom,
                prompt,
                false,
                LocalDateTime.now()));
        List<Map<String, Object>> history = getHistory(chatRoom);
        String instructions = "Kamu adalah chatbot untuk menjadi psikolog yang baik bagi mahasiswa. Berikan saya konsultasi dari hasil chat ini, dan jadilah teman ngobrol saya. Saya ingin respon kamu bersih dan tidak memiliki tanda baca seperti bold, italic, new line, dan lain sebagainya.";
        Map<String, Object> data = Map.of("model", GEMINI_MODEL,
                "systemInstruction", Map.of("parts", List.of(Map.of("text", instructions))),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "topK", 1,
                        "topP", 1,
                        "maxOutputTokens", 1024),
                "safetySettings", List.of(
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HARASSMENT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE")),
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
            String responseBody = response.body().split("text")[1].substring(4).split("\"")[0].replace("\\n", "");
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
        int nameLimit = 255;
        List<Map<String, Object>> history = getHistory(chatRoom);
        String prompt = "berikan saya nama room chat berdasarkan chat kita di atas dengan maksimal " + nameLimit
                + " karakter";
        String instructions = "Berikan saya nama yang cocok untuk chat room ini berdasarkan chat yang ada, tidak perlu berikan apapun selain nama chat room saja tanpa ada respon lain";
        Map<String, Object> data = Map.of("model", GEMINI_MODEL,
                "systemInstruction", Map.of("parts", List.of(Map.of("text", instructions))),
                "generationConfig", Map.of(
                        "temperature", 0.7,
                        "topK", 1,
                        "topP", 1,
                        "maxOutputTokens", 1024),
                "safetySettings", List.of(
                        Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HATE_SPEECH",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_HARASSMENT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                        Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT",
                                "threshold", "BLOCK_MEDIUM_AND_ABOVE")),
                "contents", List.of(history, Map.of(
                        "role", "user",
                        "parts", List.of(Map.of("text",
                                prompt)))));

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
            String responseBody = response.body().split("text")[1].substring(4).split("\"")[0].replace("\\n", "");
            return responseBody.length() > nameLimit ? responseBody.substring(0, nameLimit) : responseBody;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
