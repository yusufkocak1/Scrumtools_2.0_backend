package com.kocak.scrumtoolsbackend.repository;

import com.kocak.scrumtoolsbackend.entity.UserPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserPreferenceRepository extends JpaRepository<UserPreference, Long> {

    List<UserPreference> findByUserId(Long userId);

    Optional<UserPreference> findByUserIdAndKey(Long userId, String key);

    void deleteByUserIdAndKey(Long userId, String key);

    void deleteByUserId(Long userId);
}
