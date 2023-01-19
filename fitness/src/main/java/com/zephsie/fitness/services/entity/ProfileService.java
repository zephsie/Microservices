package com.zephsie.fitness.services.entity;

import com.zephsie.fitness.dtos.ProfileDTO;
import com.zephsie.fitness.models.entity.Profile;
import com.zephsie.fitness.repositories.JournalRepository;
import com.zephsie.fitness.repositories.ProfileRepository;
import com.zephsie.fitness.services.api.IProfileService;
import com.zephsie.fitness.utils.converters.api.IEntityDTOConverter;
import com.zephsie.fitness.utils.exceptions.NotFoundException;
import com.zephsie.fitness.utils.exceptions.NotUniqueException;
import com.zephsie.fitness.utils.exceptions.WrongVersionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProfileService implements IProfileService {

    private final ProfileRepository profileRepository;

    private final JournalRepository journalRepository;

    private final IEntityDTOConverter<Profile, ProfileDTO> profileDTOConverter;

    @Autowired
    public ProfileService(ProfileRepository profileRepository, JournalRepository journalRepository, IEntityDTOConverter<Profile, ProfileDTO> profileDTOConverter) {
        this.profileRepository = profileRepository;
        this.journalRepository = journalRepository;
        this.profileDTOConverter = profileDTOConverter;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Profile> read(UUID userId) {
        return profileRepository.findById(userId);
    }

    @Override
    @Transactional
    public Profile create(UUID userId, ProfileDTO profileDTO) {
        if (profileRepository.existsById(userId)) {
            throw new NotUniqueException("Profile with id " + userId + " already exists");
        }

        Profile newProfile = profileDTOConverter.convertToEntity(profileDTO);

        newProfile.setId(userId);

        return profileRepository.save(newProfile);
    }

    @Override
    @Transactional
    public Profile update(UUID userId, ProfileDTO profileDTO, LocalDateTime version) {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));

        if (!profile.getDtUpdate().equals(version)) {
            throw new WrongVersionException("Wrong version");
        }

        profile.setWeight(profileDTO.getWeight());
        profile.setHeight(profileDTO.getHeight());
        profile.setTarget(profileDTO.getTarget());
        profile.setBirthday(profileDTO.getBirthday());
        profile.setGender(profileDTO.getGender());
        profile.setActivityType(profileDTO.getActivityType());

        return profileRepository.save(profile);
    }

    @Override
    @Transactional
    public void delete(UUID userId, LocalDateTime version) {
        Profile profile = profileRepository.findById(userId).orElseThrow(() -> new NotFoundException("Profile not found"));

        if (!profile.getDtUpdate().equals(version)) {
            throw new WrongVersionException("Wrong version");
        }

        journalRepository.deleteAllByProfile(profile);

        profileRepository.delete(profile);
    }
}