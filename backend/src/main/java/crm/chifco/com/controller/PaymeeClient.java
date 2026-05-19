package crm.chifco.com.controller;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.crypto.*;
import java.security.*;
import java.math.BigInteger;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymeeClient {

    private final String apiKey;
    private final String apiToken;
    private final String baseUrl;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public PaymeeClient(String apiKey, String apiToken, boolean sandbox) {
        this.apiKey = apiKey;
        this.apiToken = apiToken;
        this.baseUrl = sandbox 
            ? "https://sandbox.paymee.tn/api/v2" 
            : "https://app.paymee.tn/api/v2";
    }

    // DTOs
    public static class CreatePaymentRequest {
        public Double amount;
        public String note;
        @JsonProperty("first_name") public String firstName;
        @JsonProperty("last_name") public String lastName;
        public String email;
        public String phone;
        @JsonProperty("return_url") public String returnUrl;
        @JsonProperty("cancel_url") public String cancelUrl;
        @JsonProperty("webhook_url") public String webhookUrl;
        @JsonProperty("order_id") public String orderId;
    }
    public static class CreatePaymentResponse {
        public boolean status;
        public String message;
        public int code;
        public Data data;
        public static class Data {
            public String token;
            @JsonProperty("order_id") public String orderId;
            @JsonProperty("first_name") public String firstName;
            @JsonProperty("last_name") public String lastName;
            public String email;
            public String phone;
            public String note;
            public Double amount;
            @JsonProperty("payment_url") public String paymentUrl;
        }
    }

    /**
     * Creates a payment and returns the payment_url (to redirect the buyer).
     */
    public String createPayment(CreatePaymentRequest req) throws IOException {
        String url = baseUrl + "/payments/create";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("Authorization", "Token " + apiKey);
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(objectMapper.writeValueAsBytes(req));
        }

        int respCode = conn.getResponseCode();
        InputStream is = (respCode < 400 ? conn.getInputStream() : conn.getErrorStream());
        CreatePaymentResponse resp = objectMapper.readValue(is, CreatePaymentResponse.class);
        if (!resp.status) {
            throw new RuntimeException("Paymee create payment failed: " + resp.message);
        }
        return resp.data.paymentUrl;
    }

    /**
     * Verifies the webhook callback by computing check_sum and comparing.
     * Example payload fields: token, payment_status (boolean), order_id, etc.
     */
    public boolean verifyWebhookCallback(String token, boolean paymentStatus, String checkSumFromPayload) {
        String statusValue = paymentStatus ? "1" : "0";
        String raw = token + statusValue + apiToken;
        String computed = md5(raw);
        return computed.equalsIgnoreCase(checkSumFromPayload);
    }

    private String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes("UTF-8"));
            BigInteger bi = new BigInteger(1, digest);
            String hex = bi.toString(16);
            // pad leading zeros
            while (hex.length() < 32) {
                hex = "0" + hex;
            }
            return hex;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

