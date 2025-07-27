package com.kocak.scrumtoolsbackend.service;

import com.kocak.scrumtoolsbackend.dto.ApiResponse;
import com.kocak.scrumtoolsbackend.entity.User;
import com.kocak.scrumtoolsbackend.entity.UserPreference;
import com.kocak.scrumtoolsbackend.repository.UserPreferenceRepository;
import com.kocak.scrumtoolsbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class UserPreferenceService {

    @Autowired
    private UserPreferenceRepository userPreferenceRepository;

    @Autowired
    private UserRepository userRepository;

    public ApiResponse<Map<String, String>> getPreferences(Long userId) {
        try {
            List<UserPreference> preferences = userPreferenceRepository.findByUserId(userId);
            Map<String, String> preferencesMap = new HashMap<>();

            for (UserPreference preference : preferences) {
                preferencesMap.put(preference.getKey(), preference.getValue());
            }

            return ApiResponse.success(preferencesMap);
        } catch (Exception e) {
            return ApiResponse.error("Kullanıcı ayarları alınamadı: " + e.getMessage());
        }
    }

    public ApiResponse<String> getPreference(Long userId, String key) {
        try {
            Optional<UserPreference> preference = userPreferenceRepository.findByUserIdAndKey(userId, key);

            if (preference.isPresent()) {
                return ApiResponse.success(preference.get().getValue());
            } else {
                return ApiResponse.error("Ayar bulunamadı");
            }
        } catch (Exception e) {
            return ApiResponse.error("Ayar alınamadı: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> setPreference(Long userId, String key, String value) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            Optional<UserPreference> existingPreference = userPreferenceRepository.findByUserIdAndKey(userId, key);

            if (existingPreference.isPresent()) {
                // Mevcut ayarı güncelle
                UserPreference preference = existingPreference.get();
                preference.setValue(value);
                userPreferenceRepository.save(preference);
            } else {
                // Yeni ayar oluştur
                UserPreference newPreference = new UserPreference(user, key, value);
                userPreferenceRepository.save(newPreference);
            }

            return ApiResponse.success("Ayar başarıyla kaydedildi");
        } catch (Exception e) {
            return ApiResponse.error("Ayar kaydedilemedi: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> setPreferences(Long userId, Map<String, String> preferences) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            for (Map.Entry<String, String> entry : preferences.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                Optional<UserPreference> existingPreference = userPreferenceRepository.findByUserIdAndKey(userId, key);

                if (existingPreference.isPresent()) {
                    // Mevcut ayarı güncelle
                    UserPreference preference = existingPreference.get();
                    preference.setValue(value);
                    userPreferenceRepository.save(preference);
                } else {
                    // Yeni ayar oluştur
                    UserPreference newPreference = new UserPreference(user, key, value);
                    userPreferenceRepository.save(newPreference);
                }
            }

            return ApiResponse.success("Ayarlar başarıyla kaydedildi");
        } catch (Exception e) {
            return ApiResponse.error("Ayarlar kaydedilemedi: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> deletePreference(Long userId, String key) {
        try {
            userPreferenceRepository.deleteByUserIdAndKey(userId, key);
            return ApiResponse.success("Ayar başarıyla silindi");
        } catch (Exception e) {
            return ApiResponse.error("Ayar silinemedi: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> resetPreferences(Long userId) {
        try {
            userPreferenceRepository.deleteByUserId(userId);
            return ApiResponse.success("Tüm ayarlar başarıyla sıfırlandı");
        } catch (Exception e) {
            return ApiResponse.error("Ayarlar sıfırlanamadı: " + e.getMessage());
        }
    }

    @Transactional
    public ApiResponse<String> setDefaultPreferences(Long userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Kullanıcı bulunamadı"));

            // Default ayarları tanımla
            Map<String, String> defaultPreferences = getDefaultPreferences();

            // Sadece mevcut olmayan ayarları ekle
            for (Map.Entry<String, String> entry : defaultPreferences.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                Optional<UserPreference> existingPreference = userPreferenceRepository.findByUserIdAndKey(userId, key);

                if (!existingPreference.isPresent()) {
                    UserPreference newPreference = new UserPreference(user, key, value);
                    userPreferenceRepository.save(newPreference);
                }
            }

            return ApiResponse.success("Default ayarlar başarıyla eklendi");
        } catch (Exception e) {
            return ApiResponse.error("Default ayarlar eklenemedi: " + e.getMessage());
        }
    }

    private Map<String, String> getDefaultPreferences() {
        Map<String, String> defaults = new HashMap<>();
        defaults.put("theme", "light");
        defaults.put("language", "tr");
        defaults.put("timezone", "Europe/Istanbul");
        defaults.put("dateFormat", "dd/MM/yyyy");
        defaults.put("timeFormat", "24h");
        defaults.put("notifications", "true");
        defaults.put("emailNotifications", "true");
        defaults.put("sidebarCollapsed", "false");
        return defaults;
    }
}
