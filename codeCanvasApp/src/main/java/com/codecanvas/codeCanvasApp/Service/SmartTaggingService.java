package com.codecanvas.codeCanvasApp.Service;



import org.springframework.stereotype.Service;


import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SmartTaggingService {

    // Basic stopwords list (you can expand later)
    private static final Set<String> STOPWORDS = new HashSet<>(Arrays.asList(
            "i", "me", "my", "myself", "we", "our", "ours", "you", "your", "yours",
            "he", "him", "his", "she", "her", "it", "its", "they", "them", "their",
            "what", "which", "who", "whom", "this", "that", "am", "is", "are", "was",
            "were", "be", "been", "being", "have", "has", "had", "do", "does", "did",
            "a", "an", "the", "and", "but", "if", "or", "because", "as", "until",
            "while", "of", "at", "by", "for", "with", "about", "against", "between",
            "into", "through", "during", "before", "after", "to", "from", "up",
            "down", "in", "out", "on", "off", "over", "under", "then", "once",
            "here", "there", "when", "where", "why", "how", "all", "any", "both",
            "each", "few", "more", "most", "other", "some", "such", "no", "nor",
            "not", "only", "own", "same", "so", "than", "too", "very", "hi"
    ));

    public List<String> generateTags(String content) {
        if (content == null || content.isBlank()) {
            return Collections.emptyList();
        }

        // Tokenize and lowercase
        String[] words = content.toLowerCase().replaceAll("[^a-z0-9 ]", " ").split("\\s+");

        // Remove stopwords and short words
        List<String> filtered = Arrays.stream(words)
                .filter(w -> w.length() > 2 && !STOPWORDS.contains(w))
                .collect(Collectors.toList());

        // Count frequency of single words
        Map<String, Integer> freqMap = new HashMap<>();
        for (String word : filtered) {
            freqMap.put(word, freqMap.getOrDefault(word, 0) + 1);
        }

        // Generate bigrams
        Map<String, Integer> bigramFreq = new HashMap<>();
        for (int i = 0; i < filtered.size() - 1; i++) {
            String bigram = filtered.get(i) + " " + filtered.get(i + 1);
            bigramFreq.put(bigram, bigramFreq.getOrDefault(bigram, 0) + 1);
        }

        // Combine single + bigrams into one map
        Map<String, Integer> combined = new HashMap<>(freqMap);
        for (Map.Entry<String, Integer> entry : bigramFreq.entrySet()) {
            combined.put(entry.getKey(), entry.getValue());
        }

        // Sort by frequency (descending)
        return combined.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .map(Map.Entry::getKey)
                .limit(5)  // take top 5
                .collect(Collectors.toList());
    }
}
