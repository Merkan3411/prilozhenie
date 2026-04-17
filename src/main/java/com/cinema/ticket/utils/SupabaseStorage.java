package com.cinema.ticket.utils;

public class SupabaseStorage {
    private static final String SUPABASE_URL = "https://xvbjaflhuqchygvjwqoi.supabase.co";
    private static final String BUCKET_NAME = "movies";

    public static String getPosterUrl(String fileName) {
        if (fileName == null || fileName.trim().isEmpty()) {
            return null;
        }
        String url = SUPABASE_URL + "/storage/v1/object/public/" + BUCKET_NAME + "/" + fileName.trim();
        return url;
    }
}