package alliance.team.stage.service;

import alliance.team.stage.entity.Code;
import alliance.team.stage.repository.CodeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CodeService {
    private final CodeRepository codeRepository;

    public CodeService(CodeRepository codeRepository) {
        this.codeRepository = codeRepository;
    }

    public List<Code> findByEmail(String email) {
        return codeRepository.findByEmail(email);
    }
}