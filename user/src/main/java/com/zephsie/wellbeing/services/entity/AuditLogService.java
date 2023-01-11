package com.zephsie.wellbeing.services.entity;

import com.zephsie.wellbeing.dtos.AuditLogDTO;
import com.zephsie.wellbeing.dtos.AuditLogFullDTO;
import com.zephsie.wellbeing.models.entity.User;
import com.zephsie.wellbeing.repositories.UserRepository;
import com.zephsie.wellbeing.services.api.IAuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AuditLogService implements IAuditLogService {

    private final UserRepository userRepository;

    @Autowired
    public AuditLogService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public AuditLogFullDTO read(AuditLogDTO auditLogDTO) {
        Optional<User> user = userRepository.findById(auditLogDTO.getUserId());
        return new AuditLogFullDTO(auditLogDTO.getId(), user.orElse(null), auditLogDTO.getType(), auditLogDTO.getDescription());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AuditLogFullDTO> read(Page<AuditLogDTO> page) {
        return page.map(this::read);
    }
}
